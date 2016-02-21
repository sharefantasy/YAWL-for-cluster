package cluster.hostTester.service;

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

import java.util.*;

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


    private static final Logger _logger = Logger.getLogger(TestPlanService.class);
    private Map<Long, Timer> _executor = new HashMap<>();   // identify by testplan.id
    private Map<Long, Timer> _shutdownNotifier = new HashMap<>();

    public TestPlanEntity getTestPlanByID(long id) {
        return (TestPlanEntity) _pm.get(TestPlanEntity.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<TestPlanEntity> getAllTestPlan() {
        return (List<TestPlanEntity>) _pm.getObjectsForClass("TestPlanEntity");
    }

    public TestPlanEntity createTestPlan(TestPlanEntity testPlan) {
        testPlan.setStartTime(new Date());
        testPlan.setTestTenant(tenantService.getTesterTenant(UUID.randomUUID().toString(), testPlan.getEngineNumber()));
        _pm.exec(testPlan, HibernateEngine.DB_INSERT, true);
        return testPlan;
    }

    public void startTest(TestPlanEntity testPlan) {
        List<EngineRole> engines = testPlan.getTestTenant().getEngineList();
        Host tester = testPlan.getHost();
        hostService.setEngineOnHost(engines);
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
        }, 0, 5000);
        Timer shut = new Timer();
        shut.schedule(new TimerTask() {
            @Override
            public void run() {
                exe.cancel();
                _logger.info(String.format("test %s stop, tested capability: %f",
                        testPlan, tester.getCapability(engines.size())));
                for (EngineRole e : engines) {
                    e.getEngine().setStatus(EngineStatus.INACTIVE);
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
}
