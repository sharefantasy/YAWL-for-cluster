package org.yawlfoundation.yawl.engine.interfce.interfaceC;

import java.rmi.RemoteException;

/**
 * Created by fantasy on 2015/8/22.
 */
public interface InterfaceC_Controller {
    String connect(String engineID, String identifier, String url) throws RemoteException;
    String disconnect(String engineID, String identifier) throws RemoteException;
    String register(String engineID, String identifier) throws RemoteException;
    String unregister(String engineID, String identifier) throws RemoteException;
    String heartbeat(String engineID, String identifier) throws RemoteException;

}
