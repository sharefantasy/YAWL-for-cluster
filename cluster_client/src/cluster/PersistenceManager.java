package cluster;

import cluster.entity.Engine;
import cluster.entity.EngineRole;
import cluster.entity.Host;
import cluster.entity.Tenant;
import cluster.hostTester.entity.TestPlanEntity;
import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by fantasy on 2015/8/26.
 */
public class PersistenceManager extends HibernateEngine {

    private static Class[] persistclasses = {
              Engine.class,
            EngineRole.class, Host.class, Tenant.class,
            TestPlanEntity.class
    };
    private static Logger _logger = Logger.getLogger(PersistenceManager.class);
    private static PersistenceManager _pm ;
    public PersistenceManager(boolean persistOn) {
        super(persistOn, new HashSet<>(Arrays.asList(persistclasses)));
    }

}
