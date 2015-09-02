package org.yawlfoundation.yawl.engine.interfce.interfaceC;

import com.mchange.v1.identicator.IdHashMap;
import org.apache.commons.collections.map.HashedMap;
import org.yawlfoundation.yawl.engine.interfce.Interface_Client;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fantasy on 2015/8/5.
 */
public class InterfaceC_EnvironmentBasedClient extends Interface_Client{
    public Map<String, String> urls;
    public Map<String ,String> sessionHandles = new HashMap<>();
    private String engineServiceName;
    private String engineServicePassword;
    public InterfaceC_EnvironmentBasedClient(String engineServiceName, String engineServicePassword){
        urls = new HashMap<>();
        this.engineServiceName = engineServiceName;
        this.engineServicePassword = engineServicePassword;
    }
    public InterfaceC_EnvironmentBasedClient(Map<String, String> urls){
        this.urls = urls;
    }


    public String connect(String engineId, String url, String sessionHandle) throws IOException {
        urls.put(engineId, url);
        sessionHandles.put(engineId, sessionHandle);
        Map<String, String> params = this.prepareParamMap("connect", null);
        params.put("userID", engineServiceName);
        params.put("password", PasswordEncryptor.encrypt(engineServicePassword, null));
        String handle = this.executePost(url, params);
        if (handle != null){
            sessionHandles.put(engineId, handle);
            return "success";
        } else{
          return "failed";
        }


    }

    public String checkConnection(String engineId, String sessionHandle) throws IOException {
        if (urls.containsKey(engineId)){
            return this.executeGet(urls.get(engineId), this.prepareParamMap("checkConnection", sessionHandle));
        }
        return "no such engine";
    }

    public String disconnect(String engineId) throws IOException {
        if (urls.containsKey(engineId)){
            return this.executePost(urls.get(engineId), this.prepareParamMap("disconnect", sessionHandles.get(engineId)));
        }
        return "no such engine";
    }

    public String setEngineRole(String engineId, String engineRole) throws IOException {
        if (urls.containsKey(engineId)){
            Map<String, String> params = prepareParamMap("setEngineRole", sessionHandles.get(engineId));
            params.put("engineRole", engineRole);
            return executePost(urls.get(engineId), params);
        }
        return "no such engine";
    }

    public String clusterShutdown() throws IOException {
        for (String i : urls.keySet()){
            Map<String, String> params = prepareParamMap("clusterShutdown", sessionHandles.get(i));
            disconnect(i);
            executePost(urls.get(i), params);
        }
        return "success";
    }
}
