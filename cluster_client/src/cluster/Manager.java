package cluster;

import cluster.data.EngineInfo;
import cluster.event.exceptions.GeneralException;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fantasy on 2015/8/22.
 */
public class Manager {
    private static Manager _manager;
    private static final Logger _logger = Logger.getLogger(Manager.class);
    private ConcurrentHashMap<String, EngineInfo> activeEngineRepo;
    private ConcurrentHashMap<String, EngineInfo> registerEngineRepo;
    private HeartbeatChecker checker = new HeartbeatChecker();
    private Manager(boolean persist){
        if (persist){
            //TODO: 初始化管理数据库
        }
        activeEngineRepo = new ConcurrentHashMap<String, EngineInfo>();
        registerEngineRepo = new ConcurrentHashMap<String, EngineInfo>();
    }
    public static Manager getInstance(){
        if (_manager == null){
            _manager = new Manager(false);
        }
        return _manager;
    }

    public void login(EngineInfo engine) throws GeneralException {
        if (registerEngineRepo.containsKey(engine.getEngineID())){
            if (registerEngineRepo.get(engine.getEngineID())
                    .getIdentifier().equals(engine.getIdentifier())){
                engine.clearLost();
                activeEngineRepo.put(engine.getEngineID(), engine);
                _logger.info(engine.getEngineID() + " login");
                System.out.println(engine.getEngineID() + " login");
            }else{
                throw new GeneralException(engine.getEngineID() + " wrong identifier.");
            }
        }
        else throw new GeneralException("no such engine");
    }
    public void logout(EngineInfo engine) throws GeneralException {
        if (!registerEngineRepo.containsKey(engine.getEngineID()))
            throw new GeneralException(engine.getEngineID() + " doesn't login.");
        else if (activeEngineRepo.containsKey(engine.getEngineID())){
            if (activeEngineRepo.get(engine.getEngineID())
                    .getIdentifier().equals(engine.getIdentifier())){
                activeEngineRepo.remove(engine.getEngineID(), engine);
            }
            else throw new GeneralException(engine.getEngineID() + " is not registered");
        }
    }

    public void register(EngineInfo engine) throws GeneralException {
        if (registerEngineRepo.containsKey(engine.getEngineID()))
            throw new GeneralException(engine.getEngineID() + "is already registered");
        if (!registerEngineRepo.containsKey(engine.getEngineID())){
                engine.clearLost();
                registerEngineRepo.put(engine.getEngineID(), engine);
                activeEngineRepo.put(engine.getEngineID(), engine);
            _logger.info(engine.getEngineID() + " login");
            System.out.println(engine.getEngineID() + " login");
            }
        else throw new GeneralException(engine.getEngineID() + " register failed");
    }
    public void unregister(EngineInfo engine) throws GeneralException {
        if (registerEngineRepo.containsKey(engine.getEngineID())){
            registerEngineRepo.remove(engine.getEngineID(), engine);
            if (activeEngineRepo.containsKey(engine.getEngineID())){
                activeEngineRepo.remove(engine.getEngineID(), engine);
            }
        }
        else throw new GeneralException(engine.getEngineID() + " no such engine");
    }

    public boolean isLogin(String engineID){
        return activeEngineRepo.containsKey(engineID);
    }

    public String heartbeat(String engineID){
        if (isLogin(engineID)){
            EngineInfo engine = activeEngineRepo.get(engineID);
            engine.clearLost();
        }
        return "success";
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
                            System.out.println("lost " + e.getEngineID() + " at " + d.toString());
                            flag = false;
                        }
                    }
                    if (flag){
                        System.out.println("heartbeat all clear");
                    }
                }
            },10000,10000);
        }
    }
}
