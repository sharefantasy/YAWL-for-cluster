package cluster.general.service;

import cluster.general.entity.data.HostSpeedRcd;
import cluster.util.PersistenceManager;
import cluster.general.entity.EngineRole;
import cluster.general.entity.Host;
import cluster.util.exceptions.GeneralException;
import cluster.workflowService.ServiceProvider;
import cluster.util.iaasClient.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fantasy on 2016/2/7.
 */
@Service("hostService")
@Transactional
public class HostService {
    @Autowired
    private PersistenceManager _pm;

    @Autowired
    @Qualifier("engineDataGenerator")
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

    public void addEngine(Host host, EngineRole e) throws GeneralException {
        if (e != null) {
            if (!host.getEngineList().contains(e)) {
                host.getEngineList().add(e);
                _pm.exec(host, HibernateEngine.DB_UPDATE, true);
            } else {
                throw new GeneralException("duplicated engine");
            }
        }

    }

    public void removeEngine(Host host, EngineRole e) throws GeneralException {
        if (e != null) {
            if (host.getEngineList().contains(e)) {
                host.getEngineList().remove(e);
                _pm.exec(host, HibernateEngine.DB_UPDATE, true);
            } else {
                throw new GeneralException("invalid engine");
            }
        }
    }

    public Host updateSpeed(Host host) {
        if (host == null) return null;
        double speed = host.getEngineList().stream()
                .map(EngineRole::getCurrentSpeed)
                .reduce((double) 0, (a, b) -> a + b);
        HostSpeedRcd speedRcd = new HostSpeedRcd();
        Date time = new Date();
        speedRcd.setHost(host);
        speedRcd.setTime(time);
        speedRcd.setSpeed(speed);
        host.getSpeedRcds().add(speedRcd);
        host.setCurrentSpeed(speed);
        host.setRecordTime(time);
        _pm.exec(speedRcd, HibernateEngine.DB_INSERT);
        _pm.exec(host, HibernateEngine.DB_UPDATE);
        _pm.commit();
        return host;
    }
}
