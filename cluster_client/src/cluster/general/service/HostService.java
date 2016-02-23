package cluster.general.service;

import cluster.general.entity.Engine;
import cluster.general.entity.data.HostSpeedRcd;
import cluster.util.PersistenceManager;
import cluster.general.entity.EngineRole;
import cluster.general.entity.Host;
import cluster.util.exceptions.GeneralException;
import cluster.workflowService.ServiceProvider;
import cluster.util.iaasClient.Adapter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.util.ArrayList;
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
    private EngineService engineService;

    @Autowired
    private Adapter adapter;

    @Autowired
    private ServiceProvider serviceProvider;

    private static final Logger _logger = Logger.getLogger(HostService.class);

    public void setEngineOnHost(List<Engine> engineList, Host host) {
//        host.getEngineList().addAll(engineList);
//        engineList.stream().forEach(e->e.setHost(host));
    }


    @SuppressWarnings("unchecked")
    public List<Host> getAllHosts() {
        return (List<Host>) _pm.getObjectsForClass("Host");
    }

    //requires synchronization between host platform and db.
    public List<Host> loadFromPlatform() {
        List<Host> hosts = adapter.getHosts();
        List<Engine> engines = adapter.bindEngineAndHost(engineService.getAllEngines(), hosts);
        _pm.beginTransaction();
        engines.stream().forEach(e -> _pm.exec(e, HibernateEngine.DB_UPDATE));
        hosts.stream().forEach(h -> _pm.exec(h, HibernateEngine.DB_UPDATE));
        _pm.commit();
        return hosts;
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
        _pm.exec(speedRcd, HibernateEngine.DB_INSERT, true);
        _pm.exec(host, HibernateEngine.DB_UPDATE, true);
        return host;
    }

    public boolean kickEnginesToRandomHost(List<Engine> enginesToBeKicked, Host hostToAvoid) {
        List<Host> hosts = getAllHosts();
        hosts.remove(hostToAvoid);
        if (hosts.isEmpty()) {
            _logger.error("No Hosts available.");
            return false;
        }
        enginesToBeKicked.parallelStream().forEach(e -> {
            Host dest = hosts.get(0);
            _logger.info(String.format("Engine %d will be migrate to Host: %s", e.getId(), dest.getName()));
            _logger.warn(String.format("Engine %d should be migrate to Host: %s", e.getId(), dest.getName()));
            // TODO: 2016/2/22 should call adapter to do by migration.
//            adapter.Migrate(e,dest);
        });

        _logger.info("migration complete");
        return true;
    }

    public Host createHost(String name, String ip) {
        Host host = new Host();
        host.setName(name);
        host.setIp(ip);
        _pm.exec(host, HibernateEngine.DB_INSERT, true);
        return null;
    }
}
