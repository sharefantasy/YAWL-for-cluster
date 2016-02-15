package cluster.ditribute.strategy;

import cluster.entity.EngineRole;
import cluster.entity.ServiceProvider;
import cluster.entity.Tenant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.SortedMap;

/**
 * Created by fantasy on 2016/2/14.
 */
@Component("BrutalScheduler")
public class BrutalScheduler implements Scheduler {
    private static final Logger _logger = Logger.getLogger(BrutalScheduler.class);
    @Autowired
    private ServiceProvider serviceProvider;

    List<Double>[] capabilityset = new ArrayList()[hosts.length];
    double[] tenantSLO = new double[tenants.length];


    double[] tenantspeedBeforeSchedule = new double[tenants.length];
    double[] tenantSpeedAfterSchedule = new double[tenants.length];

    @Override
    public ArrayList<EngineRole>[][] schedule(ArrayList<EngineRole>[][] oldSolution) {
        int[][] oldConfiguration = new int[tenants.length][hosts.length];
        int[][] newConfiguration = oldConfiguration.clone();

        // FIXME: 2016/2/14 make these final
        double clusterMaxCapability = 0;
        for (List<Double> capability : capabilityset) {
            clusterMaxCapability += capability.get(capability.size() - 1);
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
        double[] limitExceedRate = new double[tenant.length];


        int[][] bestConfig = oldConfiguration.clone();

        double bestConfigMaxSpeed = getConfigTotalSpeed(bestConfig);// TODO: 2016/2/15 it should be weighted ranking to be more accurate

        class TenantRankingPair implements Comparable {


            public int id;
            public double rate;
            public boolean isExceed;

            public TenantRankingPair() {
            }

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
        for (int i = 0; i < tenants.size(); i++) {
            ranking.add(new TenantRankingPair(i, limitExceedRate[i], isTenantSatisfied[i]));
        }

        ranking.poll();


        for (int i = 0; i < 200; i++) {
            if (!isLimitExceeded(newConfiguration)) {
                break;
            }


        }
        return newConfiguration;
    }

    private double getConfigTotalSpeed(int[][] config) {
        double result = 0;
        for (int i = 0; i < config.length; i++) {
            for (int j = 0; j < config[0].length; j++) {
                if (config[i][j] == 0) continue;
                result += capabilityset[j].get(config[i][j]) * config[i][j];
            }
        }
        return result;
    }

    private boolean isLimitExceeded(int[][] configuration) {
        for (int i = 0; i < configuration.length; i++) {
            double currentTenantSpeedSum = 0;
            for (int j = 0; j < configuration[0].length; j++) {
                currentTenantSpeedSum += capabilityset[j].get(hosts.get(i).getEngineList().size());
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
