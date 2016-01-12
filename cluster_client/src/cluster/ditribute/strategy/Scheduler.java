package cluster.ditribute.strategy;
import cluster.entity.EngineRole;

public interface Scheduler {
    EngineRole[][] schedule(EngineRole[][] oldSolution);
    String getProgressMessage();
    SchedulerStatus getProgress();
}
