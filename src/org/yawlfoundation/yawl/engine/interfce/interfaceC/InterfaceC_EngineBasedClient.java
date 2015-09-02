package org.yawlfoundation.yawl.engine.interfce.interfaceC;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.authentication.YExternalClient;
import org.yawlfoundation.yawl.engine.ObserverGateway;
import org.yawlfoundation.yawl.engine.YEngine;
import org.yawlfoundation.yawl.engine.interfce.Interface_Client;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fantasy on 2015/8/5.
 */
public class InterfaceC_EngineBasedClient extends Interface_Client {
    private static Timer timer = new Timer();
    private final StopWatch watch = new StopWatch();
    protected Logger _logger = Logger.getLogger(InterfaceC_EngineBasedClient.class);
    private String clusterURI;
    private String engineID;
    private String password;
    private String selfURI;

    public InterfaceC_EngineBasedClient(String clusterURI, String engine_id, String password, String selfURI){
        this.clusterURI = clusterURI;
        engineID = engine_id;
        this.password = password;
        this.selfURI = selfURI;
    }
    public String connect() throws IOException {
        YEngine _engine = YEngine.getInstance();
        Map<String, String> params = prepareParamMap("connect", null);
        params.put("engineID", engineID);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        params.put("url", selfURI);
        YExternalClient cluster= _engine.getExternalClient("cluster");
        String session = _engine.getSessionCache().connect("cluster", cluster.getPassword(), 10000);
        params.put("sessionHandle", session);
        String res = executePost(clusterURI, params);
        if (res.equals("success")){
            return "success";
        }else{
            _engine.getSessionCache().disconnect(session);
            return "failed";
        }
    }


    public String disconnect() throws IOException {
        Map<String, String> params = prepareParamMap("disconnect", null);
        params.put("engineID", engineID);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        return executePost(clusterURI, params);
    }

    public String register() throws IOException {
        Map<String, String> params = prepareParamMap("register", null);
        params.put("engineID", engineID);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        return executePost(clusterURI, params);
    }


    public String unregister() throws IOException {
        Map<String, String> params = prepareParamMap("unregister", null);
        params.put("engineID", engineID);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        return executePost(clusterURI, params);
    }
    public String getEngineRole() throws IOException {
        Map<String, String> params = prepareParamMap("getenginerole", null);
        params.put("engineID", engineID);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        return executePost(clusterURI, params);
    }
    public void heartbeat(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Map<String, String> params = prepareParamMap("heartbeat", null);
                params.put("engineID", engineID);
                params.put("password", PasswordEncryptor.encrypt(password, null));
                try {
                    executePost(clusterURI, params);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },5000,5000);
   }
    public void clusterShutdown(){
        timer.cancel();
    }
}
