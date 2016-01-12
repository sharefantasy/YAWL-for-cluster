package org.yawlfoundation.yawl.engine.interfce.interfaceC;

import java.rmi.RemoteException;
import java.util.Date;

/**
 * Created by fantasy on 2015/8/22.
 */
public interface InterfaceC_Controller {
    String connect(String engineID, String password, String url, String sessionHandle) throws RemoteException;
    String disconnect(String engineID, String password) throws RemoteException;
    String register(String engineID, String password) throws RemoteException;
    String unregister(String engineID, String password) throws RemoteException;
    String heartbeat(String engineID, String password, Date time, double speed) throws RemoteException;
    String getEngineRole(String engineID, String password) throws RemoteException;
}
