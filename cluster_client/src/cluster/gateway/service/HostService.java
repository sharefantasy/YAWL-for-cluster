package cluster.gateway.service;

import cluster.PersistenceManager;
import cluster.entity.EngineRole;
import cluster.entity.Host;
import cluster.entity.ServiceProvider;
import cluster.iaasClient.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.util.List;

/**
 * Created by fantasy on 2016/2/7.
 */
@Service("hostService")
@Transactional
public class HostService {
    @Autowired
    private PersistenceManager _pm;

    @Autowired
    private EngineRoleService roleService;

    @Autowired
    private Adapter adapter;

    @Autowired
    private ServiceProvider serviceProvider;

    public void setEngineOnHost(List<EngineRole> engineList) {

    }


    @SuppressWarnings("unchecked")
    public List<Host> getAllHosts() {
        return (List<Host>) _pm.getObjectsForClass("Host");
    }

    private List<Host> loadFromPlatform() {   //requires synchronization between host platform and db.
        List<Host> oshosts = adapter.getHosts();
        List<Host> pmhosts = (List<Host>) _pm.getObjectsForClass("Host");
        for (Host h : oshosts) {
            if (!pmhosts.contains(h)) {
                _pm.exec(h, HibernateEngine.DB_INSERT);
                pmhosts.add(h);
            }
        }
        _pm.commit();
        return pmhosts;
    }

    public Host getHostById(long id) {
        return (Host) _pm.get(Host.class, id);
    }

    public void setHostCapability(long hid, int engineNum, double capability) {
        Host host = (Host) _pm.get(Host.class, hid);
        host.setCapability(engineNum, capability);
        _pm.exec(host, HibernateEngine.DB_UPDATE);
    }

    public void reloadHosts() {
        adapter.getHosts();
    }
}
