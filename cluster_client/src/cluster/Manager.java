package cluster;

import cluster.data.EngineInfo;
import cluster.data.EngineStatus;
import cluster.event.exceptions.GeneralException;
import org.apache.log4j.Logger;
import org.omg.PortableInterceptor.INACTIVE;
import org.yawlfoundation.yawl.engine.interfce.interfaceC.InterfaceC_EnvironmentBasedClient;
import org.yawlfoundation.yawl.util.HibernateEngine;
import org.yawlfoundation.yawl.util.StringUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fantasy on 2015/8/22.
 */
public class Manager {
    private static Manager _manager;
    private static final Logger _logger = Logger.getLogger(Manager.class);
    private ConcurrentHashMap<String, EngineInfo> activeEngineRepo;
    private HeartbeatChecker checker = new HeartbeatChecker();
    private PersistenceManager _pm;
    private InterfaceC_EnvironmentBasedClient _client;
    private int MAX_WORKER = -1;
    private int MIN_BACKUP = 1;
    private Manager(boolean persist){
        _pm = new PersistenceManager(persist);
        activeEngineRepo = new ConcurrentHashMap<>();
    }
    private Manager(boolean persist, Map<String, String> params){
        _pm = new PersistenceManager(persist);
        activeEngineRepo = new ConcurrentHashMap<>();
        MAX_WORKER =  StringUtil.strToInt(params.get("MAX_WORKER"), 10);
        MIN_BACKUP =  StringUtil.strToInt(params.get("MIN_BACKUP"), 10);
    }
    public static Manager getInstance(){
        if (_manager == null){
            _manager = new Manager(false);
        }
        return _manager;
    }

    public void login(String id, String password) throws GeneralException, IOException {
        EngineInfo engine = (EngineInfo)_pm.get(EngineInfo.class, id);
        if (engine != null){
            if (engine.getPassword().equals(password)){
                engine.clearLost();
                engine.setLastLogineTime(new Date());
                activeEngineRepo.put(engine.getEngineID(), engine);
                _logger.info(engine.getEngineID() + " login");
                // TODO: raise login_event(this, engine)
                //distribute(engine);
            }else{
                throw new GeneralException("wrong password.");
            }
        }
        else throw new GeneralException(id + " password is not correct");
    }
    public void logout(String id, String password) throws GeneralException {
        if (activeEngineRepo.containsKey(id)){
            if (activeEngineRepo.get(id)
                    .getPassword().equals(password)){
                _logger.info(id + " disconnect");
                activeEngineRepo.remove(id);
            }
            else {
                EngineInfo engine = (EngineInfo)_pm.get(EngineInfo.class, id);
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
        EngineInfo engine = (EngineInfo)_pm.get(EngineInfo.class, id);
        if (engine != null)
            throw new GeneralException(id + "is already registered");
        engine = new EngineInfo(id, password);
        _pm.exec(engine, HibernateEngine.DB_INSERT, true);
        _logger.info(engine.getEngineID() + " registered");
    }

    public void unregister(String id, String password) throws GeneralException {
        EngineInfo engine = (EngineInfo)_pm.get(EngineInfo.class, id);
        if (engine == null)
            throw new GeneralException(id + "is not registered");
        if (activeEngineRepo.containsKey(engine.getEngineID())){
            activeEngineRepo.remove(engine.getEngineID(), engine);
        }
        _pm.exec(engine, HibernateEngine.DB_DELETE, true);
        _logger.info(id + " unregistered");
    }

    public boolean isLogin(String id, String password) throws GeneralException {
        EngineInfo engine = (EngineInfo)_pm.get(EngineInfo.class, id);
        if (engine == null)
            throw new GeneralException(id + "is not registered");
        if (!engine.getPassword().equals(password))
            throw new GeneralException(id + " password is not correct");
        return activeEngineRepo.containsKey(id);
        
    }

    public String heartbeat(String id, String password) throws GeneralException {
        if (isLogin(id, password)){
            EngineInfo engine = activeEngineRepo.get(id);
            engine.clearLost();

        }
        //TODO: raise heartbeat(engine)
        return "success";
    }
    public void setEngineRole(String id, String role){
        EngineInfo engine = (EngineInfo)_pm.get(EngineInfo.class, id);
        if (engine != null){
            engine.setEngineRole(role);
        }
    }
    private void distribute(EngineInfo engine){
        int size = activeEngineRepo.values().size();
        String id = engine.getEngineID();
        try {
            if (size < MAX_WORKER){
                if (size % 2 == 0){
                    String role = UUID.randomUUID().toString();
                    engine.setStatus(EngineStatus.WORKER);
                    engine.setEngineRole(role);
                    _client.setEngineRole(id,role);
                }
            }
            else {
                engine.setStatus(EngineStatus.BACKUP);
                engine.setEngineRole(null);
                _client.setEngineRole(id, null);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            _logger.error(engine.getEngineID() + " lost connect");
        }
    }

    public void set_client(InterfaceC_EnvironmentBasedClient _client) {
        this._client = _client;
    }
    public void releaseAllEngine(){
        activeEngineRepo.clear();
        List<EngineInfo> list = _pm.getObjectsForClass("EngineInfo");
        for (EngineInfo i : list){
            i.setStatus(EngineStatus.INACTIVE);
        }
    }
    private class HeartbeatChecker{
        private Timer timer = new Timer();
        public HeartbeatChecker(){
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Date d = new Date();
                    boolean flag = true;
                    for(EngineInfo e : activeEngineRepo.values()){
                        if (d.getTime() - e.getLastHeartbeatTime().getTime() > 5000){
                            _logger.warn("lost " + e.getEngineID() + " at " + d.toString());
                            flag = false;
                            e.setStatus(EngineStatus.UNHEALTHY);
                            //TODO: raise miss_heartbeat(engine)
                        }
                    }
                    if (flag){
                        _logger.info("heartbeat all clear");
                    }
                }
            },10000,10000);
        }
    }


}
