package cluster.general.service;

import cluster.general.entity.EngineRole;
import cluster.general.entity.Host;
import cluster.general.entity.data.HostSpeedRcd;
import cluster.general.entity.data.TenantSpeedRcd;
import cluster.util.PersistenceManager;
import cluster.general.entity.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.util.Date;
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
        Tenant t = new Tenant();
        t.setName(tenantName);
        t.setSLOspeed(SLOspeed);
        t.setCreateTime(new Date());
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

    public long violatedTime(Tenant tenant) {
        final long PERIOD = 5000;
        final long[] violated = {0};
        // TODO: 2016/2/21 method to calculate tenant violation time and violation rate.
//        historySpeed.forEach((Date d, Double s)->{
//            if (s < SLOspeed) violated[0] +=PERIOD;     //magic method .5000 is the period of report.
//        });
//        return violated[0];
        return 0;
    }

    public Tenant updateSpeed(Tenant tenant) {
        if (tenant == null) return null;
        double speed = tenant.getEngineList().stream()
                .map(EngineRole::getCurrentSpeed)
                .reduce((double) 0, (a, b) -> a + b);
        TenantSpeedRcd speedRcd = new TenantSpeedRcd();
        Date time = new Date();
        speedRcd.setTenant(tenant);
        speedRcd.setTime(time);
        speedRcd.setSpeed(speed);
        tenant.getSpeedRcds().add(speedRcd);
        tenant.setCurrentSpeed(speed);
        tenant.setRecordTime(time);
        _pm.exec(speedRcd, HibernateEngine.DB_INSERT);
        _pm.exec(tenant, HibernateEngine.DB_UPDATE);
        _pm.commit();
        return tenant;
    }
}
