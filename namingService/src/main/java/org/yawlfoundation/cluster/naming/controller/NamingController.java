package org.yawlfoundation.cluster.naming.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yawlfoundation.cluster.naming.service.ZkClientFactory;

/**
 * Created by fantasy on 2016/8/6.
 */
@RestController
@RequestMapping("/naming")
public class NamingController implements DisposableBean {

	private CuratorFramework client;
	private PathChildrenCache serviceCache;
	private static final Logger _logger = Logger.getLogger(NamingController.class);

	private final ZkClientFactory factory;
	@Value("${naming.service.address}")
	private String selfAddress;
	@Value("${naming.service.path}")
	private String selfPath;
	@Value("${service.namespace}")
	private String serviceNamespace;

	@Autowired
	public NamingController(ZkClientFactory factory) {
		this.factory = factory;
	}

	@PostConstruct
	public void init() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		client = factory.getClient();
		try {
			if (client.checkExists().forPath(selfPath) != null) {
				_logger.warn("Another naming service instance exists, this application will serve as follower");
			} else {
				client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(selfPath,
						selfAddress.getBytes());
				// build cache
				serviceCache = new PathChildrenCache(client, serviceNamespace, true);
				serviceCache.start();
				serviceCache.getListenable().addListener((client1, event) -> serviceCache.rebuild(),
						Executors.newSingleThreadExecutor());

				stopWatch.stop();
				_logger.info(String.format("Naming service starts in %d ms", stopWatch.getLastTaskTimeMillis()));
			}
		} catch (Exception e) {
			_logger.error("Zookeeper is not up at present. Please execute zkServer first");
		}
	}
	@RequestMapping("/list")
	public Map<String, String> list() {
		Map<String, String> result = new HashMap<>();
		for (ChildData data : serviceCache.getCurrentData()) {
			result.put(data.getPath(), new String(data.getData()));
		}
		return result;
	}
	@RequestMapping("/service/{id}")
	public String get(@PathVariable("id") String id) {
		ChildData data = serviceCache.getCurrentData(id);
		return data != null ? new String(data.getData()) : "";
	}

	public void destroy() {
		try {
			serviceCache.close();
		} catch (IOException e) {
			_logger.error("cannot close service cache");
		}
	}
}
