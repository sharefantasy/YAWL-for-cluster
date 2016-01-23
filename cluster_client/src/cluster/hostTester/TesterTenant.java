package cluster.hostTester;

import cluster.entity.EngineRole;
import cluster.entity.Tenant;

/**
 * Created by fantasy on 2016/1/23.
 */
public class TesterTenant extends Tenant {

    public TesterTenant() {
        super();
        setName("_HOST_TESTER_");
    }
    public void createEngine(int engineNum){
        for (int i = 0; i < engineNum; i++) {
            EngineRole r = new EngineRole();
            getEngineList().add(r);
            r.setTenant(this);
        }
    }
}
