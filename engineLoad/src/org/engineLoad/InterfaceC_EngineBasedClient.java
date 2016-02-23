package org.engineLoad;

import org.yawlfoundation.yawl.engine.interfce.Interface_Client;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fantasy on 2015/8/5.
 */
public class InterfaceC_EngineBasedClient extends Interface_Client {
    private static Timer timer = new Timer();
    private String clusterURI;
    private String engineID;
    private String password;
    private String selfURI;
    private String rsURL;
    private WorkitemCounter workitemCounter = WorkitemCounter.getInstace();

    public void setClusterURI(String clusterURI) {
        this.clusterURI = clusterURI;
    }

    public void setEngineID(String engineID) {
        this.engineID = engineID;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public InterfaceC_EngineBasedClient(String clusterURI, String engine_id, String password, String selfURI, String rsURL) {
        this.clusterURI = clusterURI;
        engineID = engine_id;
        this.password = password;
        this.selfURI = selfURI;
        this.rsURL = rsURL;
    }

    public String connect() throws IOException {
        Map<String, String> params = prepareParamMap("connect", null);
        params.put("engineID", engineID);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        params.put("url", selfURI);

        String session = engineID;
        params.put("sessionHandle", session);
        String res = executePost(clusterURI, params);
        if (res.equals("success")) {
            return "success";
        } else {
            return "failed";
        }
    }


    public String disconnect() throws IOException {
        Map<String, String> params = prepareParamMap("disconnect", null);
        params.put("engineID", engineID);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        return executePost(clusterURI, params);
    }

    public String register() {
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

    public void heartbeat() {
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
        }, 5000, 2000);
    }

    public void stopHeartbeat() {
        timer.cancel();
    }

    public String setRSEngineRole(String engineRole) throws IOException {
        Map<String, String> params = prepareParamMap("setEngine", null);
        params.put("engineID", engineRole);
        params.put("password", PasswordEncryptor.encrypt(password, null));
        return executePost(rsURL, params);
    }

    public void clusterShutdown() {
        timer.cancel();
        workitemCounter.shutdown();
    }


}
