package cluster.ditribute.strategy;
import cluster.entity.EngineRole;

import java.util.ArrayList;
import java.util.List;

public interface Scheduler {
    ArrayList<EngineRole>[][] schedule(ArrayList<EngineRole>[][] oldSolution);
    String getProgressMessage();
    SchedulerStatus getProgress();
}
