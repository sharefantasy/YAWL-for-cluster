package cluster.simulation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by fantasy on 2016/2/18.
 */
@RequestMapping("/simulation")
public class SimulationController {
    private int tenantNum = 40;
    private static final Logger _logger = Logger.getLogger(SimulationController.class);

    @Autowired
    private EngineDataGenerator dg;

    @RequestMapping("/")
    public String testSimulate() {

        // virtual scheduler test

        return "";
    }
}
