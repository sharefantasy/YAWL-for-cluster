package cluster.general.service;

import cluster.general.entity.Engine;
import cluster.general.entity.data.EngineRoleSpeedRcd;
import cluster.util.PersistenceManager;
import cluster.general.entity.EngineRole;
import cluster.general.entity.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by fantasy on 2016/2/6.
 */
@Service("engineRoleService")
public class EngineRoleService {

    @Autowired
    private PersistenceManager _pm;

    public List<EngineRole> generateRoleToTenant(Tenant tenant) {
        return generateRoleToTenant(tenant, 1);
    }

    public List<EngineRole> generateRoleToTenant(Tenant tenant, int number) {
        List<EngineRole> engineRoleList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            EngineRole engineRole = new EngineRole();
            engineRole.setRole(UUID.randomUUID().toString());
            engineRole.setTenant(tenant);
            engineRoleList.add(engineRole);
            _pm.exec(engineRole, HibernateEngine.DB_INSERT, true);
        }
        return engineRoleList;
    }

    public EngineRole updateSpeed(EngineRole role, Date time, double speed) {
        if (role == null) return null;
        EngineRoleSpeedRcd speedRcd = new EngineRoleSpeedRcd();
        speedRcd.setRole(role);
        speedRcd.setTime(time);
        speedRcd.setSpeed(speed);
        role.getSpeedRcds().add(speedRcd);
        role.setCurrentSpeed(speed);
        role.setCurrentRcdTime(time);
        _pm.exec(speedRcd, HibernateEngine.DB_INSERT, true);
        _pm.exec(role, HibernateEngine.DB_UPDATE, true);
        return role;
    }

    public List<EngineRole> getUnallocateRoles() {
        return (List<EngineRole>) _pm.getObjectsForClassWhere("EngineRole", "engine=null");
    }
}
