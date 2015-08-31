package org.yawlfoundation.yawl.engine.interfce.interfaceC;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.engine.ObserverGateway;
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
    private String identifier;
    public InterfaceC_EngineBasedClient(String uri, String engine_id, String identifier){
        clusterURI = uri;
        engineID = engine_id;
        this.identifier = identifier;
    }
    public String connect() throws IOException {
        Map<String, String> params = prepareParamMap("connect", null);
        params.put("engineID", engineID);
        params.put("identifier", PasswordEncryptor.encrypt(identifier, null));
        return executePost(clusterURI, params);
    }


    public String disconnect() throws IOException {
        Map<String, String> params = prepareParamMap("disconnect", null);
        params.put("engineID", engineID);
        params.put("identifier", PasswordEncryptor.encrypt(identifier, null));
        return executePost(clusterURI, params);
    }

    public String register() throws IOException {
        Map<String, String> params = prepareParamMap("register", null);
        params.put("engineID", engineID);
        params.put("identifier", PasswordEncryptor.encrypt(identifier, null));
        return executePost(clusterURI, params);
    }


    public String unregister() throws IOException {
        Map<String, String> params = prepareParamMap("unregister", null);
        params.put("engineID", engineID);
        params.put("identifier", PasswordEncryptor.encrypt(identifier, null));
        return executePost(clusterURI, params);
    }

    public void heartbeat(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Map<String, String> params = prepareParamMap("heartbeat", null);
                params.put("engineID", engineID);
                params.put("identifier", PasswordEncryptor.encrypt(identifier, null));
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
