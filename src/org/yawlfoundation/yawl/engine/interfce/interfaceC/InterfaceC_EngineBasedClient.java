package org.yawlfoundation.yawl.engine.interfce.interfaceC;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.authentication.YExternalClient;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.engine.ObserverGateway;
import org.yawlfoundation.yawl.engine.WorkitemCounter;
import org.yawlfoundation.yawl.engine.YEngine;
import org.yawlfoundation.yawl.engine.interfce.Interface_Client;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
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
    private String rsURL;
    private WorkitemCounter workitemCounter = WorkitemCounter.getInstace();

    public InterfaceC_EngineBasedClient(String clusterURI, String engine_id, String password, String selfURI, String rsURL){
        this.clusterURI = clusterURI;
        engineID = engine_id;
        this.password = password;
        this.selfURI = selfURI;
        this.rsURL = rsURL;
    }
    public String connect() throws IOException {
        YEngine _engine = YEngine.getInstance();
        Map<String, String> params = prepareParamMap("connect", null);
        params.put("engineID", engineID);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        params.put("url", selfURI);

        YExternalClient cluster= _engine.getExternalClient("cluster");
        String session = _engine.getSessionCache().connect("cluster", cluster.getPassword(), -1);
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

    public String register()  {
        Map<String, String> params = prepareParamMap("register", null);
        params.put("engineID", engineID);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        try {
            return executePost(clusterURI, params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String unregister() throws IOException {
        Map<String, String> params = prepareParamMap("unregister", null);
        params.put("engineID", engineID);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        return executePost(clusterURI, params);
    }
    public String getEngineRole() throws IOException {
        Map<String, String> params = prepareParamMap("getEngineRole", null);
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
                String reportDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(workitemCounter.getReportDate());
                params.put("time", reportDate);
                params.put("speed", String.valueOf(workitemCounter.getReportCounter()));
                try {
                    executePost(clusterURI, params);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },5000,2000);
    }
    public String setRSEngineRole(String engineRole) throws IOException {
        Map<String, String> params = prepareParamMap("setEngine", null);
        params.put("engineID", engineRole);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        return executePost(rsURL, params);
    }
    public void clusterShutdown(){
        timer.cancel();
        workitemCounter.shutdown();
    }
}
