package cluster.workflowService.schedule;

public interface Scheduler {
    int[][] schedule(int[][] oldSolution);

    String getProgressMessage();

    SchedulerStatus getProgress();
}
