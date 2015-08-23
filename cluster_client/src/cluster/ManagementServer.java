package cluster;

import cluster.data.EngineInfo;
import cluster.event.exceptions.GeneralException;
import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.engine.interfce.interfaceC.InterfaceC_Controller;
import org.yawlfoundation.yawl.engine.interfce.interfaceC.InterfaceC_EnvironmentBasedServer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.rmi.RemoteException;

/**
 * Created by fantasy on 2015/8/21.
 */
public class ManagementServer extends InterfaceC_EnvironmentBasedServer implements InterfaceC_Controller{

    private static final Logger _logger = Logger.getLogger(ManagementServer.class);
    private Manager _manager = Manager.getInstance();
    public void init(ServletConfig config) throws ServletException{
        this.controller = this;
        _logger.info("cluster service started");
        System.out.println("cluster service started");

    }

    @Override
    public String connect(String engineID, String identifier) throws RemoteException {
        try {
            _manager.login(new EngineInfo(engineID, identifier));
        } catch (GeneralException e) {
            _logger.info(e.getMsg());
            return e.getMsg();
        }
        return "success";
    }

    @Override
    public String disconnect(String engineID, String identifier) throws RemoteException {
        try {
            _manager.logout(new EngineInfo(engineID, identifier));
        } catch (GeneralException e) {
            _logger.info(e.getMsg());
            return e.getMsg();
        }
        return "success";
    }


    @Override
    public String register(String engineID, String identifier) throws RemoteException {
        try {
            _manager.register(new EngineInfo(engineID, identifier));
        } catch (GeneralException e) {
            _logger.info(e.getMsg());
            return e.getMsg();
        }
        return "success";
    }

    @Override
    public String unregister(String engineID, String identifier) throws RemoteException {
        try {
            _manager.unregister(new EngineInfo(engineID, identifier));
        } catch (GeneralException e) {
            _logger.info(e.getMsg());
            return e.getMsg();
        }
        return "success";
    }

    @Override
    public String heartbeat(String engineID, String identifier) throws RemoteException {
        if (_manager.isLogin(engineID)){
            _manager.heartbeat(engineID);
        }
        System.out.println(engineID + " heartbeat");
        return "success";
    }

}
