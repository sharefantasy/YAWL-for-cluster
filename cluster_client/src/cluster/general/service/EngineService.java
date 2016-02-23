package cluster.general.service;

import cluster.general.entity.Engine;
import cluster.general.entity.EngineRole;
import cluster.general.entity.EngineStatus;
import cluster.util.PersistenceManager;
import cluster.util.event.EventCenter;
import cluster.util.exceptions.GeneralException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yawlfoundation.yawl.engine.interfce.interfaceC.InterfaceC_EnvironmentBasedClient;
import org.yawlfoundation.yawl.util.HibernateEngine;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fantasy on 2015/8/22.
 */
@Service("EngineService")
public class EngineService implements DisposableBean {
    private static EngineService _engineService;
    private static final Logger _logger = Logger.getLogger(EngineService.class);
    private HeartbeatChecker checker = new HeartbeatChecker();

    @Autowired
    private PersistenceManager _pm;

    @Autowired
    private InterfaceC_EnvironmentBasedClient _client;

    @Autowired
    private EngineRoleService engineRoleService;

    @Autowired
    private EventCenter _ec;

    public EngineService() {
        _engineService = this;
        startHeartBeat();
    }

    @Deprecated
    public static EngineService getInstance() {
        return _engineService;
    }

    @Deprecated
    public void set_client(InterfaceC_EnvironmentBasedClient _client) {
        this._client = _client;
    }


    public Engine getEngineByEngineID(String id) throws GeneralException {
        List<Engine> engines = _pm.getObjectsForClassWhere("Engine", String.format("engineID = '%s'", id));
        Engine engine;
        if (engines != null) {
            engine = engines.get(0);
        } else {
            throw new GeneralException(id + " doesn't exist");
        }
        return engine;
    }

    public Engine getEngineById(long id) {
        return (Engine) _pm.get(Engine.class, id);
    }

    public List<Engine> getAllEngines() {
        return (List<Engine>) _pm.getObjectsForClass("Engine");
    }

    public void login(String id, String password) throws GeneralException, IOException {
        Engine engine = (Engine) _pm.get(Engine.class, id);
        if (engine != null) {
            if (checkPassword(password, engine)) {
                engine.setLastHeartbeatTime(new Date());
                engine.setLastLoginTime(new Date());
                engine.setStatus(EngineStatus.IDLE);
                save(engine);
                _logger.info(engine.getEngineID() + " login");
                _ec.trigger("engine_login", engine);
            } else {
                throw new GeneralException("wrong password.");
            }
        } else
            throw new GeneralException(id + " password is not correct");
    }

    public void save(Engine engine) {
        if (_pm.get(Engine.class, engine.getId()) != null) {
            _pm.exec(engine, HibernateEngine.DB_UPDATE, true);
        } else {
            _pm.exec(engine, HibernateEngine.DB_INSERT, true);
        }
    }

    public void logout(String id, String password) throws GeneralException {
        Engine engine = getEngineByEngineID(id);
        if (engine.getStatus() == EngineStatus.SERVING
                || engine.getStatus() == EngineStatus.IDLE
                || engine.getStatus() == EngineStatus.TESTING) {
            if (checkPassword(password, engine)) {
                _logger.info(id + " disconnect");
                engine.setStatus(EngineStatus.INACTIVE);
                save(engine);
            } else {
                throw new GeneralException(engine.getEngineID() + " password is not correct");
            }
        } else
            throw new GeneralException(id + " is not logined");
    }

    public void register(String id, String password) throws GeneralException {
        Engine engine = getEngineByEngineID(id);
        if (engine != null)
            throw new GeneralException(id + "is already registered");
        engine = new Engine(id, password);
        engine.setLastHeartbeatTime(new Date());
        save(engine);
        _logger.info(engine.getEngineID() + " registered");
    }

    public void unregister(String id, String password) throws GeneralException {
        Engine engine = getEngineByEngineID(id);
        if (checkPassword(password, engine)) {
            _pm.exec(engine, HibernateEngine.DB_DELETE, true);
        } else {
            throw new GeneralException(id + " password incorrect");
        }

        _logger.info(id + " unregistered");
    }

    private boolean checkPassword(String password, Engine engine) {
        try {
            return password.equals(PasswordEncryptor.encrypt(engine.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isLogin(String id, String password) throws GeneralException {
        Engine engine = getEngineByEngineID(id);
        if (!checkPassword(password, engine))
            throw new GeneralException(id + " password is not correct");
        EngineStatus status = engine.getStatus();
        return status != null
                && status != EngineStatus.LOST
                && status != EngineStatus.UNHEALTHY
                && status != EngineStatus.INACTIVE;
    }

    public String heartbeat(String id, String password, Date time, double speed) throws GeneralException {
        if (isLogin(id, password)) {
            Engine engine = getEngineByEngineID(id);
            engine.setLastHeartbeatTime(new Date());
            engineRoleService.updateSpeed(engine.getEngineRole(), time, speed);
            save(engine);
            _logger.info(String.format("{%s} %s as %s heartbeat, speed %f",
                    time.toString(), id, engine.getEngineRole(), speed));
        } else
            throw new GeneralException(id + " doesn't not login");
        return "success";
    }

    public void setEngineRole(String id, EngineRole role) throws GeneralException {
        Engine engine = getEngineByEngineID(id);
        if (engine != null) {
            engine.setEngineRole(role);
        }
    }


    public boolean setRemoteEngineRole(Engine engine, EngineRole role) {
        if (engine == null) {
            return false;
        }
        _pm.beginTransaction();
        if (role != null) {
            Engine switchedEngine = role.getEngine();
            if (switchedEngine != null) {
                if (switchedEngine.getStatus() == EngineStatus.TESTING) {
                    _logger.error("try to switch testing engine: " + switchedEngine.getId());
                    _pm.commit();
                    return false;
                }
                switchedEngine.setEngineRole(null);
                if (switchedEngine.getStatus() == EngineStatus.SERVING) {
                    try {
                        _client.setEngineRole(switchedEngine.getEngineID(), null);
                        switchedEngine.setStatus(EngineStatus.IDLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                        _logger.info("reset engine role failed at engine: " + switchedEngine.getId());
                        _pm.commit();
                        return false;
                    }
                }
            }
        }
        try {
            _client.setEngineRole(engine.getEngineID(), role == null ? null : role.getRole());
        } catch (IOException e) {
            e.printStackTrace();
            _logger.error(String.format("Set %s role failed", engine.getEngineID()));
            _pm.rollback();
            return false;
        }
        EngineRole switchedRole = engine.getEngineRole();
        if (switchedRole != null) {
            switchedRole.setEngine(null);
        }
        engine.setEngineRole(role);
        if (role != null)
            role.setEngine(engine);
        save(engine);
        _pm.commit();
        return true;
    }

    public boolean inviteEngine(String engineAddress, String engineID, EngineRole engineRole, String ip) {
        String role = engineRole != null ? engineRole.getRole() : null;
        String password = UUID.randomUUID().toString();
        String res;
        try {
            res = _client.inviteEngine(engineAddress, engineID, password, role);
        } catch (IOException e) {
            _logger.warn(engineID + " connection lost.");
            return false;
        }
        if ("success".equalsIgnoreCase(res)) {
            Engine engine = new Engine();
            engine.setAddress(engineAddress);
            engine.setEngineID(engineID);
            engine.setPassword(password);
            engine.setEngineRole(engineRole);
            engine.setIp(ip);
            engine.setStatus(EngineStatus.IDLE);
            save(engine);
        }
        return true;
    }

    public EngineRole distribute(String id) {
        Engine engine = (Engine) _pm.get(Engine.class, id);
        long size = getActiveEngines().size();
        List<EngineRole> roles = _pm.getObjectsForClassWhere("EngineRole", "engine = null");
        EngineRole role = null;
        if (roles.size() == 0) {
            engine.setStatus(EngineStatus.IDLE);
            save(engine);
        } else {
            role = roles.get(0);
            engine.setEngineRole(role);
            engine.setStatus(EngineStatus.SERVING);
            save(engine);
            _logger.info("engine " + engine.getEngineID() + " is attached to " + role.getTenant().getId());
        }
        _logger.info("worker_num = " + size);
        return role;
    }


    private String takeBackup(Engine engine) throws GeneralException, IOException, NoSuchElementException {
        try {
            Engine backup = getIdleEngines().get(0);
            roleTaking(engine, backup);
            _client.setEngineRole(backup.getEngineID(), backup.getEngineRole().getRole());
            save(backup);
            return backup.getEngineID();
        } catch (NullPointerException e) {
            throw new GeneralException("no backup, engineRole " + engine.getEngineRole() + " lost");
        }
    }

    private void roleTaking(Engine toRest, Engine backup) {
        backup.setEngineRole(toRest.getEngineRole());
        toRest.setEngineRole(null);
        backup.setStatus(EngineStatus.SERVING);
        toRest.setStatus(EngineStatus.IDLE);
    }


    @SuppressWarnings("unchecked")
    public List<Engine> getIdleEngines() {
        return ((List<Engine>) _pm.getObjectsForClass("Engine")).stream()
                .filter(e -> e.getStatus() == EngineStatus.IDLE)
                .collect(Collectors.toList());
    }

    public void releaseAllEngine() {
        List<Engine> list = getAllEngines();
        list.stream().forEach(i -> i.setStatus(EngineStatus.INACTIVE));
    }

    public void startHeartBeat() {
        checker.start(10000);
    }

    public void stopHeartBeat() {
        checker.stop();
    }

    @Override
    public void destroy() throws Exception {
        releaseAllEngine();
    }

    private class HeartbeatChecker {
        private Timer timer = new Timer();
        private TimerTask task = new TimerTask() {
            @Override
            public void run() {
                boolean flag = true;
                for (Engine e : getActiveEngines()) {
                    if (e.getLastHeartbeatTime() == null) continue;
                    if ((new Date()).getTime() - e.getLastHeartbeatTime().getTime() > 5000) {
                        _logger.error(String.format("lost %s at %s", e.getEngineID(), (new Date()).toString()));
                        flag = false;
                        try {
                            if (e.getEngineRole() == null) continue;
                            _logger.debug(String.format("%s take up %s", takeBackup(e), e.getEngineRole()));
                            e.setStatus(EngineStatus.UNHEALTHY);
                            save(e);
                        } catch (GeneralException e1) {
                            _logger.error(e1.getMsg());
                        } catch (IOException e1) {
                            _logger.error(e1.getMessage());
                        }
                    }
                }
                if (flag)
                    _logger.debug("heartbeat all clear");
            }
        };

        public void finalize() throws Throwable {
            timer.cancel();
            _logger.debug("stop recording heartbeat...");
            super.finalize();
        }

        public void start(long heartBeatRate) {
            timer.schedule(task, heartBeatRate, heartBeatRate); // FIXME: 2016/1/24 magic number heartbeat rate
        }

        public void stop() {
            timer.cancel();
        }
    }

    public List<Engine> getActiveEngines() {
        return getAllEngines().stream()
                .filter(e -> e.getStatus() == EngineStatus.SERVING)
                .collect(Collectors.toCollection(ArrayList::new));
    }


}
