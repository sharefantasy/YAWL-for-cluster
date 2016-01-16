package cluster.iaasClient;

import java.util.ArrayList;

/**
 * Created by fantasy on 2016/1/14.
 */
public abstract class BaseAdapter implements Adapter{
    private ArrayList<envObserver> obs = new ArrayList<>();

    @Override
    public void notifyShutdown() {
        obs.stream().forEach(envObserver::notifyEnvShutdown);
    }

    @Override
    public void addObserver(envObserver ob) {
        if (ob != null && !obs.contains(ob)){
            obs.add(ob);
        }
    }

    @Override
    public void notifyStart() {
        obs.stream().forEach(envObserver::notifyEnvStart);
    }
}
