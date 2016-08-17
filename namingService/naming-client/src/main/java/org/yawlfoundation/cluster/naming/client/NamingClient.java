package org.yawlfoundation.cluster.naming.client;

import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Created by fantasy on 2016/8/9.
 */
@Component
public interface NamingClient {
	Map<String, String> list();
	String get(String serviceId);
	String attach();
	String detach();
}
