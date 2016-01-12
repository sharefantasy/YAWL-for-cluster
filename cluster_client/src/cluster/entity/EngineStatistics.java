package cluster.entity;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by fantasy on 2016/1/7.
 */
public class EngineStatistics {
    public Date recordTime;
    public HashMap<String, Double> violatedTime;

    public EngineStatistics(ServiceProvider serviceProvider) {

    }
}
