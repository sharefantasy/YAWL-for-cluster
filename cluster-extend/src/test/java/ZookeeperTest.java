import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Thread.yield;

/**
 * Created by fantasy on 2016/7/24.
 */
public class ZookeeperTest {
	public static void main(String[] args) {
		CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181",
				new ExponentialBackoffRetry(100, Integer.MAX_VALUE));
		client.start();
		String s = null;
		try {
			s = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/slave-");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(s);
		// ExecutorService service = Executors.newFixedThreadPool(6);
		//
		// try {
		// client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/yawl/test",
		// "bb".getBytes());
		// List<TestRun> masters = new ArrayList<>();
		// List<TestRun> slaves = new ArrayList<>();
		// System.out.println("start test");
		// for (int i = 0; i < 2; i++) {
		// TestRun testRun = new TestRun("role" + i);
		// testRun.result = service.submit(testRun);
		// masters.add(testRun);
		// }
		// for (int i = 0; i < 2; i++){
		// TestRun testRun = new TestRun();
		// testRun.result = service.submit(testRun);
		// slaves.add(testRun);
		// }
		// service.submit(new TestProxy(client,masters,slaves));
		// service.submit(new TestInterruptRun(masters));
		// Thread.sleep(8000);
		// service.shutdownNow();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}
}
class TestRun implements Callable<String> {
	private static int count = 0;
	private volatile String role;
	private volatile boolean running = false;
	private CuratorFramework client;
	public Future<String> result;
	public TestRun(String role) {
		this.role = role;
		this.client = CuratorFrameworkFactory.newClient("127.0.0.1:2181",
				new ExponentialBackoffRetry(100, Integer.MAX_VALUE));
		client.start();
	}
	public TestRun() {
		this.role = null;
		this.client = CuratorFrameworkFactory.newClient("127.0.0.1:2181",
				new ExponentialBackoffRetry(100, Integer.MAX_VALUE));
		client.start();
	}
	public void interrupt() {
		running = false;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public void roleRun() throws Exception {
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/yawl/role/" + role,
				role.getBytes());

		running = true;
		Date start = new Date();
		System.out.println(role + " start at " + start);
		while (running) {
			yield();
		}
		client.close();
		System.out.println(role + " stop at " + new Date() + " , interval " + (new Date().getTime() - start.getTime()));
	}
	private void slaveRun() throws Exception {
		int thisCount = count++;
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/yawl/slave/" + thisCount,
				new byte[]{new Integer(thisCount).byteValue()});
		System.out.println("slave " + thisCount + " up");
		while (role == null) {
			yield();
		}
		System.out.println("slave " + thisCount + " ready to takeover.");
		client.delete().guaranteed().forPath("/yawl/slave/" + thisCount);
	}
	@Override
	public String call() throws Exception {
		if (this.role != null) {
			roleRun();
		} else {
			slaveRun();
			System.out.println("slave switch to " + role);
			roleRun();
		}
		return null;
	}

}
class TestProxy implements Callable<String> {
	private CuratorFramework client;
	private List<TestRun> masters;
	private List<TestRun> slaves;
	private ExecutorService executorService = Executors.newFixedThreadPool(3);
	public TestProxy(CuratorFramework client, List<TestRun> masters, List<TestRun> slaves) {
		this.client = client;
		this.masters = masters;
		this.slaves = slaves;
	}

	@Override
	public String call() throws Exception {
		final PathChildrenCache childrenCache = new PathChildrenCache(client, "/yawl/role", false);
		childrenCache.start();
		childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				switch (event.getType()) {
					case CHILD_REMOVED :
						String role = event.getData().getPath().split("/")[3];
						List<String> slaveString = client.getChildren().forPath("/yawl/slave");
						int moved = Integer.parseInt(slaveString.get(0));
						System.out.println("chosen slave " + moved);
						TestRun spare = slaves.get(moved);
						slaves.remove(moved);
						spare.setRole(role);
						masters.add(spare);
						break;
				}
			}
		}, executorService);
		final PathChildrenCache slaveCache = new PathChildrenCache(client, "/yawl/slave", false);
		slaveCache.start();
		slaveCache.getListenable().addListener(new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				switch (event.getType()) {
					case CHILD_ADDED :
						System.out.printf("slave %s in monitor\n", event.getData().getPath().split("/")[3]);
						break;
					case CHILD_REMOVED :
						System.out.printf("slave %s moved\n", event.getData().getPath().split("/")[3]);
						break;
				}
			}
		}, executorService);
		return null;
	}
}
class TestInterruptRun implements Callable<String> {

	private List<TestRun> masters;
	public TestInterruptRun(List<TestRun> masters) {
		this.masters = masters;
	}
	@Override
	public String call() throws Exception {
		System.out.println("waiting for interruption");
		Thread.sleep(5000);
		masters.get(0).interrupt();
		System.out.println("interrupted");
		return null;
	}
}