package cluster.gateway;

import cluster.Manager;
import cluster.entity.Host;
import cluster.hostTester.TesterServer;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by fantasy on 2016/1/19.
 */
public class CommandGateway extends HttpServlet {
    public void init(ServletConfig config){
        TesterServer test = new TesterServer();
        Manager manager = Manager.getInstance();
        Host h = new Host("dd", 0, manager.getActiveEngineRoles());
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){

    }
}
