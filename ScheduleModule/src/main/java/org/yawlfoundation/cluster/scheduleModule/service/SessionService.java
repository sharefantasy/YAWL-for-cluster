package org.yawlfoundation.cluster.scheduleModule.service;

import org.yawlfoundation.cluster.scheduleModule.entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fantasy on 2016/5/25.
 */
@Service
public class SessionService {
    private Map<User, String> sessionRepo;
    private Map<String, User> reverseSession;

    public SessionService() {
        sessionRepo = new ConcurrentHashMap<>();
        reverseSession = new ConcurrentHashMap<>();
    }

    public String connect(User u) {
        if (reverseSession.containsKey(u)) {
            return sessionRepo.get(u);
        }
        String session = UUID.randomUUID().toString();
        sessionRepo.put(u, session);
        reverseSession.put(session, u);

        return session;
    }

    public boolean checkConnection(String sessionHandle) {
        return reverseSession.containsKey(sessionHandle);
    }

    public boolean disconnect(String sessionHandle) {
        if (sessionRepo.containsValue(sessionHandle)) {
            User u = reverseSession.get(sessionHandle);
            reverseSession.remove(sessionHandle);
            sessionRepo.remove(u);
            return true;
        }
        return false;
    }

    public User getUserBySession(String sessionHandle) {
        return reverseSession.get(sessionHandle);
    }
}
