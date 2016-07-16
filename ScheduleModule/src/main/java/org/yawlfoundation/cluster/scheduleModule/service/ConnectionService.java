package org.yawlfoundation.cluster.scheduleModule.service;

import net.dongliu.requests.Requests;
import net.sf.ehcache.store.chm.ConcurrentHashMap;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.User;
import org.springframework.stereotype.Service;
import org.yawlfoundation.yawl.engine.interfce.Interface_Client;
import org.yawlfoundation.yawl.util.PasswordEncryptor;
import org.yawlfoundation.yawl.util.StringUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by fantasy on 2016/5/25.
 */

@Service
public class ConnectionService extends Interface_Client {
    Map<Engine, String> connectionPool = new ConcurrentHashMap<>();
    ;

    public String getSession(Engine e) {
        if (!connectionPool.containsKey(e)) {
            String session = null;
            try {
                session = getSessionOnline(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (session == null) {
                return null;
            }
        }
        return connectionPool.get(e);
    }

    public String getSessionOnline(Engine e) throws IOException {
        String session = connect(e);
        if (!session.startsWith("<failure>")) {
            session = StringUtil.unwrap(session);
            connectionPool.put(e, session);
            return session;
        }
        return null;
    }

    private String connect(Engine e) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("action", "connect");
        params.put("userID", "admin");
        params.put("password", PasswordEncryptor.encrypt("YAWL", null));
        return executePost(String.format("http://%s:%s/yawl/ia/", e.getAddress(), e.getPort()), params);
    }


    public String connectByUser(Engine e, User u) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("action", "connect");
        params.put("userID", u.getUserName());
        params.put("password", PasswordEncryptor.encrypt(u.getPassword(), null));
        return executePost(String.format("http://%s:%s/yawl/ia/", e.getAddress(), e.getPort()), params);
    }

    public String forward(Engine e, Map<String, String> params, String interfce) throws IOException {
		return executeGet(String.format("http://%s:%s/yawl/%s/", e.getAddress(), e.getPort(), interfce), params);
    }

    public String forward(String uri, Map<String, String> params) throws IOException {
		return executeGet(uri, params);
    }

}
