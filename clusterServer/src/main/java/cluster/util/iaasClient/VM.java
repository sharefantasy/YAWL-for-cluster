package cluster.util.iaasClient;

import cluster.general.entity.Engine;
import org.openstack4j.model.compute.Server;

/**
 * Created by fantasy on 2016/1/21.
 */
public class VM {
	public Engine engine;
	public Server server;
	public VM(Engine e, Server s) {
		engine = e;
		server = s;
	}
}
