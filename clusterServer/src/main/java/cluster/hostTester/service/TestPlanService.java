package cluster.hostTester.service;

import cluster.general.entity.Engine;
import cluster.general.service.EngineService;
import cluster.util.PersistenceManager;
import cluster.general.entity.EngineRole;
import cluster.general.entity.EngineStatus;
import cluster.general.entity.Host;
import cluster.general.service.HostService;
import cluster.general.service.TenantService;
import cluster.hostTester.entity.TestPlanEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yawlfoundation.yawl.util.HibernateEngine;
import sun.security.pkcs11.wrapper.CK_INFO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fantasy on 2016/2/7.
 */
@Service("testplanService")
@Transactional
public class TestPlanService {

	@Autowired
	private PersistenceManager _pm;

	@Autowired
	private TenantService tenantService;

	@Autowired
	private HostService hostService;

	@Autowired
	private EngineService engineService;

	private static final Logger _logger = Logger.getLogger(TestPlanService.class);
	private Map<Long, Timer> _executor = new HashMap<>(); // identify by
															// testplan.id
	private Map<Long, Timer> _shutdownNotifier = new HashMap<>();

	public TestPlanEntity getTestPlanByID(long id) {
		return (TestPlanEntity) _pm.get(TestPlanEntity.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<TestPlanEntity> getAllTestPlan() {
		return (List<TestPlanEntity>) _pm.getObjectsForClass("TestPlanEntity");
	}

	public TestPlanEntity createTestPlan(Host host, int eNum, Date endTime) {
		TestPlanEntity testPlan = new TestPlanEntity();
		testPlan.setHost(host);
		testPlan.setEngineNumber(eNum);
		testPlan.setEndTime(endTime);
		testPlan.setStartTime(new Date());
		testPlan.setTestTenant(tenantService.getTesterTenant(UUID.randomUUID().toString(), testPlan.getEngineNumber()));
		_pm.exec(testPlan, HibernateEngine.DB_INSERT, true);
		return testPlan;
	}

	public void startTest(TestPlanEntity testPlan, boolean bypass) {
		if (bypass) {
			_logger.warn("short cut all engine check, make sure host configuration correct");
			initTestShortCut(testPlan);
		} else {
			if (!initTest(testPlan)) {
				_logger.info("initialization failed, need restart test");
				return;
			}
		}
		startTest(testPlan);
		_logger.info("host tester started, please load test process manually");
	}

	private void startTest(TestPlanEntity testPlan) {
		Host tester = testPlan.getHost();
		List<EngineRole> engines = tester.getEngineList();
		Timer exe = new Timer();
		exe.schedule(new TimerTask() {
			@Override
			public void run() {
				double avgSpeed = 0;
				for (EngineRole e : engines) {
					avgSpeed += e.getCurrentSpeed();
				}
				avgSpeed = avgSpeed / engines.size();
				double speed = avgSpeed * 0.8 + tester.getCapability(engines.size()) * 0.2;
				tester.setCapability(engines.size(), speed);
				_pm.exec(tester, HibernateEngine.DB_UPDATE, true);
				_logger.info("current speed: " + speed);
			}
		}, 1000, 5000);
		Timer shut = new Timer();
		shut.schedule(new TimerTask() {
			@Override
			public void run() {
				exe.cancel();
				_logger.info(String.format("test %s stop, tested capability: %f", testPlan,
						tester.getCapability(engines.size())));
				for (EngineRole e : engines) {
					e.getEngine().setStatus(EngineStatus.SERVING);
				}
				_pm.exec(tester, HibernateEngine.DB_UPDATE, true);
				testPlan.setFinished(true);
			}
		}, testPlan.getEndTime().getTime() - (new Date()).getTime());
		testPlan.setStartTime(new Date());
		_executor.put(testPlan.getId(), exe);
		_shutdownNotifier.put(testPlan.getId(), shut);
		_logger.info(String.format("test %s started", testPlan));
	}

	private boolean initTest(TestPlanEntity testPlan) {
		List<EngineRole> engineRoles = testPlan.getTestTenant().getEngineList();
		Host tester = testPlan.getHost();
		List<Engine> idleEngines = engineService.getIdleEngines().stream().collect(Collectors.toList());
		List<Engine> engineOnTester = tester.getEngineList().stream().map(EngineRole::getEngine)
				.collect(Collectors.toList());
		if (idleEngines.size() + tester.getEngineList().size() < engineRoles.size()) {
			_logger.error(String.format("Testplan: %d can't init. Not enough available engines", testPlan.getId()));
			return false;
		}
		tester.getEngineList().stream().filter(e -> e.getEngine() != null)
				.forEach(e -> e.getEngine().setStatus(EngineStatus.IDLE));
		if (roleSettingOperation(testPlan, tester, idleEngines, engineOnTester))
			return false;
		tester.getEngineList().stream().filter(e -> e.getEngine() != null)
				.forEach(e -> e.getEngine().setStatus(EngineStatus.TESTING));
		return true;
	}

	private boolean roleSettingOperation(TestPlanEntity testPlan, Host tester, List<Engine> idleEngines,
			List<Engine> engineOnTester) {
		Queue<Engine> engineQ = new ArrayDeque<>();
		engineQ.addAll(engineOnTester);
		boolean finished = setUpRemoteEngines(testPlan, engineQ);
		if (!finished) {
			if (engineQ.isEmpty()) {
				engineQ.addAll(idleEngines);
				finished = setUpRemoteEngines(testPlan, engineQ);
				if (!finished) {
					_logger.error(
							String.format("Testplan: %d can't init. Too many engine role faults", testPlan.getId()));
					return true;
				}
			} else {
				_logger.error(String.format("Testplan: %d can't init. Not enough available engines", testPlan.getId()));
				return true;
			}
		} else {
			if (!engineQ.isEmpty()) {
				List<Engine> enginesToBeKicked = engineQ.stream().collect(Collectors.toList());
				_logger.warn(String.format("Host: %d has to many engines, should kickout some.", tester.getId()));
				if (!hostService.kickEnginesToRandomHost(enginesToBeKicked, tester)) {
					_logger.warn(String.format("Host: %d kick out failed. please retry", tester.getId()));
					return true;
				}
			}
		}
		return false;
	}

	private boolean setUpRemoteEngines(TestPlanEntity testPlan, Queue<Engine> engineQ) {
		List<EngineRole> engineRoles = testPlan.getTestTenant().getEngineList();
		Queue<EngineRole> engineRoleQ = new ArrayDeque<>();
		engineRoleQ.addAll(engineRoles);
		while (!engineRoleQ.isEmpty()) {
			EngineRole role = engineRoleQ.remove();
			if (engineQ.size() < engineRoles.size()) {
				_logger.error(String.format("Testplan: %d can't init. Not enough available engines", testPlan.getId()));
				return true;
			}
			while (true) {
				Engine e = engineQ.remove();
				if (engineService.setRemoteEngineRole(e, role)) {
					break;
				}
				if (engineQ.isEmpty()) {
					_logger.error(
							String.format("Testplan: %d can't init. Not enough available engines", testPlan.getId()));
					return true;
				}
			}
		}
		return false;
	}

	public void forceShutdownTestPlan(TestPlanEntity testPlan) {
		_executor.get(testPlan.getId()).cancel();
		_shutdownNotifier.get(testPlan.getId()).cancel();
		_logger.info(String.format("%s is forced down", testPlan));
	}

	public void forceShutdownAllTestPlan() {
		_executor.values().stream().forEach(Timer::cancel);
		_shutdownNotifier.values().stream().forEach(Timer::cancel);
		_logger.info("all Testplan is down");
	}

	@Deprecated
	private void initTestShortCut(TestPlanEntity tp) {
		List<Engine> totest = engineService.getAllEngines().subList(0, tp.getEngineNumber());
		List<EngineRole> engineRoles = tp.getTestTenant().getEngineList();
		Queue<Engine> engineQ = new ArrayDeque<>();
		engineQ.addAll(totest);
		setUpRemoteEngines(tp, engineQ);
	}
}
