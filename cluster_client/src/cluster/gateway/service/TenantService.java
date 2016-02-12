package cluster.gateway.service;

import cluster.PersistenceManager;
import cluster.entity.EngineRole;
import cluster.entity.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;

/**
 * Created by fantasy on 2016/2/6.
 */
@Service("tenantService")
@Transactional
public class TenantService {

    @Autowired
    private PersistenceManager _pm;

    @Autowired
    private EngineRoleService roleService;


    private double averageSpeed = 14;

    @SuppressWarnings("unchecked")
    public List<Tenant> findAllTenant() {
        return (List<Tenant>) _pm.getObjectsForClass("Tenant");
    }

    public Tenant getTenantById(long tid) {
        return (Tenant) _pm.get(Tenant.class, tid);
    }

    public void save(Tenant tenant) {
        _pm.exec(tenant, HibernateEngine.DB_UPDATE);
    }

    public Tenant createTenant(String tenantName, double SLOspeed) {
        Tenant t = new Tenant(tenantName, SLOspeed);
        t.setEngineList(roleService.generateRoleToTenant(t, determineRoleNumber(SLOspeed)));
        _pm.exec(t, HibernateEngine.DB_INSERT, true);
        return t;
    }

    private int determineRoleNumber(double SLOspeed) {
        return (int) ceil(SLOspeed / averageSpeed) + 1;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public Tenant getTesterTenant(String testName, int engineNum) {
        List<Tenant> ts = _pm.getObjectsForClassWhere("Tenant", String.format("name='%s'", testName));
        Tenant tenant;
        if (ts.size() != 0) {
            for (Tenant t : ts) {
                _pm.exec(t, HibernateEngine.DB_DELETE, true);
            }
        }
        tenant = new Tenant();
        tenant.setName(testName);
        tenant.setSLOspeed(0);
        tenant.setEngineList(roleService.generateRoleToTenant(tenant, engineNum));
        return tenant;

    }
}
