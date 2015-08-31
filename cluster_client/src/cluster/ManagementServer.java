package cluster;

import cluster.data.EngineInfo;
import cluster.event.exceptions.GeneralException;
import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.engine.interfce.interfaceC.InterfaceC_Controller;
import org.yawlfoundation.yawl.engine.interfce.interfaceC.InterfaceC_EnvironmentBasedClient;
import org.yawlfoundation.yawl.engine.interfce.interfaceC.InterfaceC_EnvironmentBasedServer;
import org.yawlfoundation.yawl.util.StringUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fantasy on 2015/8/21.
 */
public class ManagementServer extends InterfaceC_EnvironmentBasedServer implements InterfaceC_Controller{

    private static final Logger _logger = Logger.getLogger(ManagementServer.class);
    private Manager _manager = Manager.getInstance();

    private InterfaceC_EnvironmentBasedClient _client;

    public void init(ServletConfig config) throws ServletException{
        this.controller = this;
        _logger.info("cluster service started");
        String engineServiceName = config.getInitParameter("engineServiceName");
        String engineServicePassword = config.getInitParameter("engineServicePassword");
        _client = new InterfaceC_EnvironmentBasedClient(engineServiceName, engineServicePassword);
        _manager.set_client(_client);
    }

    @Override
    public String connect(String engineID, String identifier, String url) throws RemoteException {
        try {
            _manager.login(engineID, identifier);
            _client.connect(engineID);
            _client.urls.put(engineID, url);
            _logger.info(engineID + " connected");
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
            _client.urls.remove(engineID);
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
    public String heartbeat(String engineID, String identifier) throws RemoteException {
        try {
            if (_manager.isLogin(engineID, identifier)){
                _manager.heartbeat(engineID, identifier);
            }
        } catch (GeneralException e) {
            e.printStackTrace();
            _logger.warn(engineID + " heartbeat abnormal");
        }
        _logger.info(engineID + " heartbeat");
        return "success";
    }
    public void destroy(){
        try {
            _logger.info(_client.clusterShutdown());
        } catch (IOException e) {
            _logger.error(e.getMessage());
        }
    }

}
