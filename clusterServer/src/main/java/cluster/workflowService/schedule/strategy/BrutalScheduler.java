package cluster.workflowService.schedule.strategy;

import cluster.general.entity.EngineRole;
import cluster.general.entity.Host;
import cluster.general.entity.Tenant;
import cluster.workflowService.schedule.Scheduler;
import cluster.workflowService.schedule.SchedulerStatus;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by fantasy on 2016/2/14.
 */
public class BrutalScheduler implements Scheduler {
	private static final Logger _logger = Logger.getLogger(BrutalScheduler.class);

	Tenant[] tenants;
	Host[] hosts;
	List<EngineRole> engineRoleList;
	double[][] capabilityset;
	double[] tenantSLO = new double[tenants.length];
	double[] tenantspeedBeforeSchedule;

	public BrutalScheduler(List<Tenant> tenants, List<Host> hosts, List<EngineRole> engineRoles,
			double[][] capabilities) {
		tenants.toArray(this.tenants);
		hosts.toArray(this.hosts);
		engineRoleList = engineRoles;
		capabilityset = capabilities;
	}

	@Override
	public int[][] schedule(int[][] oldConfiguration) {
		tenantspeedBeforeSchedule = new double[tenants.length];
		for (int i = 0; i < tenants.length; i++) {
			tenantspeedBeforeSchedule[i] = tenants[i].getCurrentSpeed();
		}

		int[][] newConfiguration = oldConfiguration.clone();

		// FIXME: 2016/2/14 make these final
		double clusterMaxCapability = 0;
		for (int i = 0; i < capabilityset.length; i++) {
			for (int j = capabilityset[0].length - 1; j <= 0; j--) {
				if (capabilityset[i][j] > 0) {
					clusterMaxCapability += capabilityset[i][j];
					break;
				}
			}
		}
		double maxRequirement = 0;
		for (double t : tenantSLO) {
			maxRequirement += t;
		}

		if (getConfigTotalSpeed(oldConfiguration) < maxRequirement) {
			_logger.warn("weak limit exceeded");
		}
		if (maxRequirement > clusterMaxCapability) {
			_logger.warn("strong limit exceeded");
		}

		boolean[] isTenantSatisfied = new boolean[tenants.length];
		for (int i = 0; i < tenants.length; i++) {
			isTenantSatisfied[i] = tenantspeedBeforeSchedule[i] > tenantSLO[i];
		}
		double[] limitExceedRate = new double[tenants.length];

		// int[][] bestConfig = oldConfiguration.clone();

		// double bestConfigMaxSpeed = getConfigTotalSpeed(bestConfig);// TODO:
		// 2016/2/15 it should be weighted ranking to be more accurate

		class TenantRankingPair implements Comparable {
			public int id;
			public double rate;
			public boolean isExceed;

			public TenantRankingPair(int id, double rate, boolean isExceed) {
				this.id = id;
				this.rate = rate;
				this.isExceed = isExceed;
			}

			@Override
			public int compareTo(Object o) {
				TenantRankingPair pair = (TenantRankingPair) o;
				if (isExceed && pair.isExceed) {
					return 0;
				}
				if (isExceed || pair.isExceed) {
					return isExceed ? 1 : -1;
				}
				double diff = rate - pair.rate;
				return (int) (diff / Math.abs(diff));
			}
		}
		PriorityQueue<TenantRankingPair> ranking = new PriorityQueue<>();
		for (int i = 0; i < tenants.length; i++) {
			ranking.add(new TenantRankingPair(i, limitExceedRate[i], isTenantSatisfied[i]));
		}

		TenantRankingPair currentT = ranking.poll();
		while (!currentT.isExceed) {
			List<EngineRole> roles = tenants[currentT.id].getEngineList();
			for (EngineRole r : roles) {
				int bestRatingHostIndex = 0;
				double bestRate = 0;
				for (int i = 0; i < hosts.length; i++) {
					newConfiguration[currentT.id][i] += 1;
					double rate = getConfigRating(newConfiguration);
					if (rate > bestRate) {
						bestRatingHostIndex = i;
						bestRate = rate;
					}
					newConfiguration[currentT.id][i] -= 1;
				}
				newConfiguration[currentT.id][bestRatingHostIndex] += 1;
			}
			currentT = ranking.poll();
		}
		return newConfiguration;
	}

	private double getConfigRating(int[][] configuration) {
		double result = 0;

		return 0;
	}

	private double getConfigTotalSpeed(int[][] config) {
		double result = 0;
		for (int i = 0; i < config.length; i++) {
			for (int j = 0; j < config[0].length; j++) {
				if (config[i][j] == 0)
					continue;
				result += capabilityset[j][config[i][j]] * config[i][j];
			}
		}
		return result;
	}

	// FIXME: 2016/2/18 should get the current host's size.
	private boolean isLimitExceeded(int[][] configuration) {
		for (int i = 0; i < configuration.length; i++) {
			double currentTenantSpeedSum = 0;
			for (int j = 0; j < configuration[0].length; j++) {
				Host curHost = null;
				for (int k = 0; k < hosts.length; k++) {
					if (hosts[k].getId() == i) {
						curHost = hosts[k];
						break;
					}
				}
				assert curHost != null;
				currentTenantSpeedSum += capabilityset[j][curHost.getEngineList().size()];
			}
			if (currentTenantSpeedSum < tenantSLO[i]) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getProgressMessage() {
		return null;
	}

	@Override
	public SchedulerStatus getProgress() {
		return null;
	}
}
