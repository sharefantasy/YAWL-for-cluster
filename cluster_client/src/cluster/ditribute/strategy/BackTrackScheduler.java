package cluster.ditribute.strategy;

import cluster.entity.EngineRole;
import cluster.entity.ServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fantasy on 2016/2/12.
 */
@Component("backtrackScheduler")
public class BackTrackScheduler implements Scheduler {

    @Autowired
    private ServiceProvider serviceProvider;

    @Override
    public ArrayList<EngineRole>[][] schedule(ArrayList<EngineRole>[][] oldSolution) {

        int[][] configuration = new int[tenants.length][hosts.length];
        List<Double>[] capabilityset = new ArrayList()[hosts.length];
        double[] tenantSLO = new double[tenant.length];
        double[] limitExceedRate = new double[tenant.length];

        double[] tenantspeedBeforeSchedule = new double[tenants.length];
        double[] tenantSpeedAfterSchedule = new double[tenants.length];


        return new ArrayList<EngineRole>[0][];
    }

    public
    @Override
    public String getProgressMessage() {
        return null;
    }

    @Override
    public SchedulerStatus getProgress() {
        return null;
    }
}
