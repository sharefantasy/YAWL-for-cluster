package cluster;

import cluster.entity.Engine;
import cluster.entity.EngineRole;
import cluster.entity.EngineStatus;
import cluster.event.exceptions.GeneralException;
import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.engine.interfce.interfaceC.InterfaceC_EnvironmentBasedClient;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by fantasy on 2015/8/22.
 */
public class Manager {
    private static Manager _manager;
    private static final Logger _logger = Logger.getLogger(Manager.class);
    private ConcurrentHashMap<String, Engine> activeEngineRepo;
    private HeartbeatChecker checker = new HeartbeatChecker();
    private PersistenceManager _pm;
    private InterfaceC_EnvironmentBasedClient _client;
    private int MAX_WORKER = 1;
    private int MIN_BACKUP = 1;
    private Manager(boolean persist){
        _pm = new PersistenceManager(persist);
        activeEngineRepo = new ConcurrentHashMap<>();
    }
    private Manager(boolean persist, Map<String, String> params){
        _pm = new PersistenceManager(persist);
        activeEngineRepo = new ConcurrentHashMap<>();
    }
    public static Manager getInstance(){
        if (_manager == null){
            _manager = new Manager(true);
        }
        return _manager;
    }

    public void login(String id, String password) throws GeneralException, IOException {
        Engine engine = (Engine)_pm.get(Engine.class, id);
        if (engine != null){
            if (engine.getPassword().equals(password)){
                engine.clearLost();
                engine.setLastLogineTime(new Date());
                activeEngineRepo.put(engine.getEngineID(), engine);
                _logger.info(engine.getEngineID() + " login");
                // TODO: raise login_event(this, engine)
            }else{
                throw new GeneralException("wrong password.");
            }
        }
        else
            throw new GeneralException(id + " password is not correct");
    }
    public void logout(String id, String password) throws GeneralException {
        if (activeEngineRepo.containsKey(id)){
            if (activeEngineRepo.get(id)
                    .getPassword().equals(password)){
                _logger.info(id + " disconnect");
                activeEngineRepo.remove(id);
                Engine engine = activeEngineRepo.get(id);
                engine.setStatus(EngineStatus.INACTIVE);
                _pm.exec(engine, HibernateEngine.DB_UPDATE, true);
            }
            else {
                Engine engine = (Engine)_pm.get(Engine.class, id);
                if (engine == null)
                    throw new GeneralException(id + " is not registered");
                else
                    throw new GeneralException(engine.getEngineID() + " password is not correct");
            }
        }
        else
            throw new GeneralException(id + " is not logined");
    }

    public void register(String id, String password) throws GeneralException {
        Engine engine = (Engine)_pm.get(Engine.class, id);
        if (engine != null)
            throw new GeneralException(id + "is already registered");
        engine = new Engine(id, password);
        _pm.exec(engine, HibernateEngine.DB_INSERT, true);
        _logger.info(engine.getEngineID() + " registered");
    }

    public void unregister(String id, String password) throws GeneralException {
        Engine engine = (Engine)_pm.get(Engine.class, id);
        if (engine == null)
            throw new GeneralException(id + "is not registered");
        if (activeEngineRepo.containsKey(engine.getEngineID())
                || password.equals(engine.getPassword())){
            activeEngineRepo.remove(engine.getEngineID(), engine);
        }
        _pm.exec(engine, HibernateEngine.DB_DELETE, true);
        _logger.info(id + " unregistered");
    }

    public boolean isLogin(String id, String password) throws GeneralException {
        Engine engine = (Engine)_pm.get(Engine.class, id);
        if (engine == null)
            throw new GeneralException(id + " is not registered");
        if (!engine.getPassword().equals(password))
            throw new GeneralException(id + " password is not correct");
        return activeEngineRepo.containsKey(id);
        
    }

    public String heartbeat(String id, String password, Date time, double speed) throws GeneralException {
        if (isLogin(id, password)){
            Engine engine = activeEngineRepo.get(id);
            engine.clearLost();
            engine.addSpeed(time, speed);
            _pm.exec(engine, HibernateEngine.DB_UPDATE, true);
            _logger.info(String.format("{%s} %s as %s heartbeat, speed %f",
                    time.toString(), id, engine.getEngineRole(), speed));
        }else
            throw new GeneralException(id + " doesn't not login");
        return "success";
    }
    public void setEngineRole(String id, EngineRole role){
        Engine engine = (Engine)_pm.get(Engine.class, id);
        if (engine != null){
            engine.setEngineRole(role);
        }
    }
    public EngineRole distribute(String id){
        Engine engine = (Engine)_pm.get(Engine.class, id);
        long size = activeEngineRepo.values().stream()
                .filter(i -> i.getStatus() == EngineStatus.SERVING).count();
        List roles = _pm.getObjectsForClassWhere("EngineRole", "engine = null");
        EngineRole role = null;
        if (roles.size() == 0){
            engine.setStatus(EngineStatus.IDLE);
            activeEngineRepo.replace(id, engine);
            _pm.exec(engine, HibernateEngine.DB_UPDATE, true);
        }
        else {
            role = (EngineRole) roles.get(0);
            engine.setEngineRole(role);
            engine.setStatus(EngineStatus.SERVING);
            activeEngineRepo.replace(id, engine);
            _pm.exec(engine, HibernateEngine.DB_UPDATE, true);
            size++;
            _logger.info("engine " + engine.getEngineID() + " is attached to " + role.getTenant().getId());
        }
        _logger.info("worker_num = " + size);
        return role;
    }
    private String takeBackup(Engine engine) throws GeneralException, IOException {
        Engine backup;
        try{
            backup = activeEngineRepo.values().stream()
                    .filter(e -> e.getStatus() == EngineStatus.IDLE)
                    .findFirst().get();
            backup.roleTaking(engine);
            backup.setStatus(EngineStatus.SERVING);
            _client.setEngineRole(backup.getEngineID(), backup.getEngineRole().getRole());
            _pm.exec(backup,HibernateEngine.DB_UPDATE, true);
            return backup.getEngineID();
        }catch (NullPointerException e){
            throw new GeneralException("no backup, engineRole " + engine.getEngineRole() + " lost");
        }
    }
    public void set_client(InterfaceC_EnvironmentBasedClient _client) {
        this._client = _client;
    }
    public void releaseAllEngine(){
        activeEngineRepo.clear();
        List<Engine> list = (List<Engine>)_pm.getObjectsForClass("Engine");
        list.stream().forEach(i -> i.setStatus(EngineStatus.INACTIVE));
    }
    private class HeartbeatChecker{
        private Timer timer = new Timer();
        public HeartbeatChecker(){
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    final boolean[] flag = {true};
                    activeEngineRepo.values().stream()
                            .filter(e -> (new Date()).getTime() - e.getLastHeartbeatTime().getTime() > 5000)
                            .parallel()
                            .forEach(e -> {
                                _logger.warn(String.format("lost %s at %s", e.getEngineID(), (new Date()).toString()));
                                flag[0] = false;
                                try {
                                    _logger.info(String.format("%s take up %s", takeBackup(e), e.getEngineRole()));
                                    e.setStatus(EngineStatus.UNHEALTHY);
                                    activeEngineRepo.remove(e.getEngineID());
                                } catch (GeneralException e1) {
                                    _logger.error(e1.getMsg());
                                } catch (IOException e1) {
                                    _logger.error(e1.getMessage());
                                }
                            });
                    if (flag[0])
                        _logger.info("heartbeat all clear");
                }
            },10000,10000);
        }

    }
    public List<Engine> getEngines(){
        return (List<Engine>) activeEngineRepo.values();
    }
    public List<EngineRole> getActiveEngineRoles(){
        return activeEngineRepo.values().stream()
                .map(Engine::getEngineRole)
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
