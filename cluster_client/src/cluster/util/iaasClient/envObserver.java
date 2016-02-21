package cluster.util.iaasClient;

/**
 * Created by fantasy on 2016/1/14.
 */
public interface envObserver {
    void notifyEnvShutdown();
    void notifyEnvStart();
}
