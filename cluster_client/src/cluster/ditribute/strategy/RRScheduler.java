package cluster.ditribute.strategy;

import cluster.entity.EngineRole;
import cluster.entity.ServiceProvider;

/**
 * Created by fantasy on 2016/1/6.
 */
public class RRScheduler implements Scheduler {
    private SchedulerStatus status = SchedulerStatus.NO_TASK;
    private ServiceProvider provider;
    public RRScheduler(ServiceProvider provider){
        this.provider = provider;
    }
    @Override
    public EngineRole[][] schedule(EngineRole[][] oldSolution) {
        status = SchedulerStatus.MAKING_STATEGY;
        EngineRole[][] result = new EngineRole[oldSolution.length][oldSolution[0].length];

        return result;
    }

    @Override
    public String getProgressMessage() {
        return null;
    }

    @Override
    public SchedulerStatus getProgress() {
        return status;
    }
}
