package cluster.simulation;

import cluster.general.entity.*;
import cluster.general.service.EngineRoleService;
import cluster.general.service.HostService;
import cluster.util.exceptions.GeneralException;
import cluster.util.exceptions.MigrationException;
import cluster.util.iaasClient.Adapter;
import cluster.util.iaasClient.BaseAdapter;
import cluster.simulation.model.SpeedModel;
import cluster.simulation.model.UniformModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by fantasy on 2016/1/14.
 */

public class EngineDataGenerator implements Adapter, DisposableBean {
	private List<EngineSimulator> engineList;
	private int interval;
	private ScheduledExecutorService _executor = Executors
			.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	private static final Logger _logger = Logger.getLogger(EngineDataGenerator.class);
	private boolean isStart = false;
	@Autowired
	private EngineRoleService engineRoleService;
	@Autowired
	private HostService hostService;

	// TODO: 2016/1/14 need probability distribution
	public EngineDataGenerator() {
	}

	public void init(List<EngineRole> roleList, int interval) {
		this.interval = interval;
		_executor = Executors.newScheduledThreadPool(roleList.size());
		engineList = new ArrayList<>(roleList.size());
		engineList.addAll(roleList.stream().map(EngineSimulator::new).collect(Collectors.toList()));
	}
	public EngineDataGenerator(List<EngineRole> roleList, int interval) {
		this.interval = interval;
		_executor = Executors.newScheduledThreadPool(roleList.size());
		engineList = new ArrayList<>(roleList.size());
		engineList.addAll(roleList.stream().map(EngineSimulator::new).collect(Collectors.toList()));
	}

	public EngineDataGenerator setSimulationParameter(String distribution) {
		return this; //// TODO: 2016/1/14 determine which distribution to use in
						//// generating speed
	}
	public void shutdown() {
		_executor.shutdown();
		_logger.info("Engine data simulation stop......");
		isStart = false;
	}

	public void startGenerating(long endTime) {
		startGenerating();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				shutdown();
			}
		}, endTime);
		_logger.info(String.format("Simulations stops in %s seconds.", endTime / 1000.0));
	}
	public void startGenerating() {
		_logger.info("Engine data simulation start......");

		for (EngineSimulator e : engineList) {
			_executor.scheduleAtFixedRate(e, 0, interval, TimeUnit.MICROSECONDS);
		}
		isStart = true;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	@Override
	public void Migrate(EngineRole vm, Host dest) throws MigrationException {
		vm.setHost(dest);
		((EngineSimulator) vm.getEngine()).migrate(dest);
	}

	@Override
	public List<Host> getHosts() {
		return null;
	}

	@Override
	public boolean isStarted() {
		return isStart;
	}

	@Override
	public List<Engine> bindEngineAndHost(List<Engine> engines, List<Host> hosts) {
		return null;
	}

	@Override
	public void destroy() throws Exception {
		_executor.shutdownNow();
	}

	class EngineSimulator extends Engine implements Runnable {
		private SpeedModel model;
		public EngineSimulator(EngineRole r) {
			model = new UniformModel();
			setEngineRole(r);
			setStatus(EngineStatus.SERVING);
		}
		public synchronized void migrate(Host h) {
			try {
				EngineRole e = getEngineRole();
				hostService.removeEngine(e.getHost(), e);
				e.setHost(h);
				hostService.addEngine(e.getHost(), e);
				model.updateEnvironment(h);

			} catch (GeneralException e) {
				_logger.error(getEngineRole().getRole() + " switch host fail. Reason: " + e.getMsg());
			}
		}
		@Override
		public void run() {
			engineRoleService.updateSpeed(getEngineRole(), new Date(), model.nextSpeed());
		}
	}
}
