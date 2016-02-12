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

/**
 * Created by fantasy on 2016/2/6.
 */
@Service("engineRoleService")
@Transactional
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
            engineRole.setTenant(tenant);
            engineRoleList.add(engineRole);
            _pm.exec(engineRole, HibernateEngine.DB_INSERT);
        }
        return engineRoleList;
    }

}
