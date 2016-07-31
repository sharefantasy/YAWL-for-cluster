package org.yawlfoundation.plugin.interfce;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by fantasy on 2016/7/31.
 */
public class InterfaceCManager {
	private AutowireCapableBeanFactory factory;
	private InterfaceC_EngineBaseClient client;

	public InterfaceCManager() {

	}

	private void loadSpring() {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		factory = context.getAutowireCapableBeanFactory();
	}

}
