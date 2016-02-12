package cluster.hostTester;

import cluster.Manager;
import cluster.PersistenceManager;
import cluster.entity.*;
import cluster.event.exceptions.GeneralException;
import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.util.HibernateEngine;

import javax.persistence.criteria.ListJoin;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by fantasy on 2016/1/19.
 */
public class TesterServer extends HttpServlet {
    public static final Logger _logger = Logger.getLogger(TesterServer.class);
    private ScheduledExecutorService _executor = TimeScaler.getInstance().getExecutor();
    private Timer timer;
    private PersistenceManager _pm = new PersistenceManager(true);
    private TesterServer instance;
    public void init(ServletConfig config) throws ServletException{}
    private void test(Host tester, List<EngineRole> engines, long endtime){
        int count = 0;
        _executor.scheduleAtFixedRate(()->{
            double avgSpeed = 0;
            for (EngineRole e : engines){
                avgSpeed += e.getCurrentSpeed();
            }
            avgSpeed = avgSpeed / engines.size();
            double speed = avgSpeed * 0.8 + tester.getCapability(engines.size()) * 0.2;
            tester.setCapability(engines.size(), speed);
            _pm.exec(tester, HibernateEngine.DB_UPDATE, true);
            _logger.info("current speed: " +  speed);
        }, 0, 5, TimeUnit.SECONDS);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                _executor.shutdownNow();
                _logger.info("test stop, tested capability: +" + tester.getCapability(engines.size()) );
                for(EngineRole e: engines){
                    e.getEngine().setStatus(EngineStatus.INACTIVE);
                }
                _pm.exec(tester,HibernateEngine.DB_UPDATE, true);
            }
        }, endtime);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response){
        String action = request.getParameter("action");
        if (action.equals("test")){
            Host totest = new Host("dd", 0);
            Tenant t = null;
//            getTesterTenant("hosttester2",2);
            Manager manager = Manager.getInstance();
            totest.setEngineList(t.getEngineList());
            int i = 0;
            for (Engine e: manager.getEngines()) {
                e.setStatus(EngineStatus.TESTING);
                e.setEngineRole(totest.getEngineList().get(i));
                totest.getEngineList().get(i++).setEngine(e);
            }
            _pm.exec(totest,HibernateEngine.DB_INSERT,true);
            instance = new TesterServer();
            _logger.info("host name: "+totest.getName());
            _logger.info("tenant name: "+t.getName());
            instance.test(totest,totest.getEngineList(),10 * 60 * 1000);
            try {
                response.getOutputStream().println("starting");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (action.equals("stop")){
            if (instance != null){
                instance._executor.shutdownNow();
                instance.timer.cancel();
            }
            try {
                response.getOutputStream().println("stop");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean isDone(){
        return _executor.isShutdown();
    }
    protected void finalize(){
        instance._executor.shutdownNow();
        instance.timer.cancel();
    }
}