package cluster.simulation.model;

import cluster.general.entity.Host;

import java.util.Random;

/**
 * Created by fantasy on 2016/1/14.
 */
public class UniformModel implements SpeedModel {
    @Override
    public double nextSpeed() {
        return new Random().nextDouble() * 20 + 1;
    }

    @Override
    public void updateEnvironment(Host h) {}
}
