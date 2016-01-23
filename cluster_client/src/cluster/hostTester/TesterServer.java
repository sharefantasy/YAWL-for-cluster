package cluster.hostTester;

import cluster.Manager;
import cluster.entity.*;
import cluster.event.exceptions.GeneralException;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by fantasy on 2016/1/19.
 */
public class TesterServer extends HttpServlet {
    public static final Logger _logger = Logger.getLogger(TesterServer.class);
    private ScheduledExecutorService _executor = TimeScaler.getInstance().getExecutor();
    private Timer timer;
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
            }
        }, endtime);
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response){
       if (!request.getParameter("yes").equals("y"))
            try {
                response.getOutputStream().println("0");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

        TesterTenant tester = new TesterTenant();
        tester.createEngine(1);
        Host totest = new Host("dd", 0);

        Manager manager = Manager.getInstance();
        int i = 0;
        for (Engine e: manager.getEngines()) {
            e.setStatus(EngineStatus.TESTING);
            e.setEngineRole(totest.getEngineList().get(i));
            totest.getEngineList().get(i++).setEngine(e);
        }
        TesterServer server = new TesterServer();
        System.out.println("confirm? ");
        _logger.info("host name: "+totest.getName());
        _logger.info("host name: "+totest.getName());
        Scanner scaner = new Scanner(System.in);
        server.test(totest,totest.getEngineList(),10 * 60 * 1000);
        try {
            response.getOutputStream().println("starting");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public boolean isDone(){
        return _executor.isShutdown();
    }

}