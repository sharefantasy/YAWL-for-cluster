package cluster.util;

import cluster.general.entity.Engine;
import cluster.general.entity.EngineRole;
import cluster.general.entity.Host;
import cluster.general.entity.Tenant;
import cluster.general.entity.data.EngineRoleSpeedRcd;
import cluster.general.entity.data.HostCapability;
import cluster.general.entity.data.HostSpeedRcd;
import cluster.general.entity.data.TenantSpeedRcd;
import cluster.hostTester.entity.TestPlanEntity;
import cluster.workflowService.entity.WorkflowPlan;
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
            TestPlanEntity.class, WorkflowPlan.class,
            HostCapability.class,
            EngineRoleSpeedRcd.class, TenantSpeedRcd.class, HostSpeedRcd.class
    };

    public PersistenceManager(boolean persistOn) {
        super(persistOn, new HashSet<>(Arrays.asList(persistclasses)));
    }

}
