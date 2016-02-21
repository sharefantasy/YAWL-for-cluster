package cluster.simulation.model;

import cluster.general.entity.Host;

/**
 * Created by fantasy on 2016/1/14.
 */
public interface SpeedModel {
    double nextSpeed();
    void updateEnvironment(Host h);
}
