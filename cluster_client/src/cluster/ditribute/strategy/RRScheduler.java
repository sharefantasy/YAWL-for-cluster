package cluster.ditribute.strategy;

import cluster.entity.EngineRole;
import cluster.entity.ServiceProvider;

import java.util.ArrayList;

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
    public ArrayList<EngineRole>[][] schedule(ArrayList<EngineRole>[][] oldSolution) {
        status = SchedulerStatus.MAKING_STATEGY;
        ArrayList[][] result = oldSolution;
        EngineRole r1 = null;
        for (int i = 0; i < result.length; i++) {
            if (oldSolution[i] == null) {continue;}
            for (int j = 0; j < result[i].length; j++) {
                if (result[i][j] == null) {continue;}
                r1 = (EngineRole) result[i][j].get(0);
                result[i][j].remove(0);
                break;
            }
            break;
        }
        if (result[10][1] == null) {result[10][1] = new ArrayList();}
        result[10][1].add(r1);

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
