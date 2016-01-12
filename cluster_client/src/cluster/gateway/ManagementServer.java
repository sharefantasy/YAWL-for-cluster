package cluster.gateway;

import cluster.Manager;
import cluster.entity.Engine;
import cluster.entity.EngineRole;
import cluster.entity.ServiceProvider;
import cluster.event.exceptions.GeneralException;
import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.engine.interfce.interfaceC.InterfaceC_Controller;
import org.yawlfoundation.yawl.engine.interfce.interfaceC.InterfaceC_EnvironmentBasedClient;
import org.yawlfoundation.yawl.engine.interfce.interfaceC.InterfaceC_EnvironmentBasedServer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * Created by fantasy on 2015/8/21.
 */
public class ManagementServer extends InterfaceC_EnvironmentBasedServer implements InterfaceC_Controller{

    private static final Logger _logger = Logger.getLogger(ManagementServer.class);
    private Manager _manager = Manager.getInstance();

    private InterfaceC_EnvironmentBasedClient _client;

    public void init(ServletConfig config) throws ServletException{
        ServletContext context = config.getServletContext();
        this.controller = this;
        _logger.info("cluster service started");
        String engineServiceName = context.getInitParameter("EngineServiceName");
        String engineServicePassword = context.getInitParameter("EngineServicePassword");
        _client = new InterfaceC_EnvironmentBasedClient(engineServiceName, engineServicePassword);
        _manager.set_client(_client);
    }

    @Override
    public String connect(String engineID, String identifier, String url, String sessionHandle) throws RemoteException {
        try {
            if (sessionHandle != null){
                _client.urls.put(engineID, url);
                _client.sessionHandles.put(engineID, sessionHandle);
                _manager.login(engineID, identifier);
                _logger.info(engineID + " connected, session: " + sessionHandle);
            }else
                throw new GeneralException("engine "+ engineID + " login failed, check engine configuration");

        } catch (GeneralException e) {
            _logger.info(e.getMsg());
            return e.getMsg();
        } catch (IOException e) {
            _logger.error(engineID + " connection lost");
            return e.getMessage();
        }
        return "success";
    }

    @Override
    public String disconnect(String engineID, String identifier) throws RemoteException {
        try {
            _manager.logout(engineID, identifier);
            _client.disconnect(engineID);
        } catch (GeneralException e) {
            _logger.info(e.getMsg());
            return e.getMsg();
        } catch (IOException e) {
            _logger.error(engineID + " connection lost");
            return e.getMessage();
        }finally {

            _client.urls.remove(engineID);
        }
        return "success";
    }


    @Override
    public String register(String engineID, String identifier) throws RemoteException {
        try {
            _manager.register(engineID, identifier);
        } catch (GeneralException e) {
            _logger.info(e.getMsg());
            return e.getMsg();
        }
        return "success";
    }

    @Override
    public String unregister(String engineID, String identifier) throws RemoteException {
        try {
            _manager.unregister(engineID, identifier);
        } catch (GeneralException e) {
            _logger.info(e.getMsg());
            return e.getMsg();
        }
        return "success";
    }

    @Override
    public String heartbeat(String engineID, String identifier, Date time, double speed) throws RemoteException {
        try {
            if (_manager.isLogin(engineID, identifier)){
                _manager.heartbeat(engineID, identifier, time, speed);
            }
        } catch (GeneralException e) {
            e.printStackTrace();
            _logger.warn(engineID + " heartbeat abnormal");
            return "abnormal";
        }
        return "success";
    }

    @Override
    public String getEngineRole(String engineID, String password) throws RemoteException {
        try {
            if(_manager.isLogin(engineID, password)){
                EngineRole role = _manager.distribute(engineID);
                _manager.setEngineRole(engineID, role);
                return role.toString();
            }else{
                return "failed";
            }
        } catch (GeneralException e) {
            e.printStackTrace();
            return "failed";
        }

    }

    public void destroy(){
        try {
            _manager.releaseAllEngine();
            _logger.info(_client.clusterShutdown());

        } catch (IOException e) {
            _logger.error(e.getMessage());
        }
    }

}