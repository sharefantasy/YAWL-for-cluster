package org.yawlfoundation.yawl.engine.interfce.interfaceC;

import org.yawlfoundation.yawl.engine.interfce.Interface_Client;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fantasy on 2015/8/5.
 */
public class InterfaceC_EnvironmentBasedClient extends Interface_Client{
    private String _url;

    public InterfaceC_EnvironmentBasedClient(String url){
        _url = url;
    }

    public String getUrl() {
        return _url;
    }

    public String getBackEndURI() {
        return this._url;
    }

    public String connect(String userID, String password) throws IOException {
        Map params = this.prepareParamMap("connect", (String) null);
        params.put("userID", userID);
        params.put("password", PasswordEncryptor.encrypt(password, (String)null));
        return this.executePost(this._url, params);
    }

    public String checkConnection(String sessionHandle) throws IOException {
        return this.executeGet(this._url, this.prepareParamMap("checkConnection", sessionHandle));
    }

    public String disconnect(String handle) throws IOException {
        return this.executePost(this._url, this.prepareParamMap("disconnect", handle));
    }

}
