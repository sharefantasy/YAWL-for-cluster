package org.yawlfoundation.test.service;

import org.yawlfoundation.yawl.engine.interfce.interfaceA.InterfaceA_EnvironmentBasedClient;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

/**
 * Created by fantasy on 2016-12-27.
 */
public class asdf {
	public static void main(String[] args) {
		InterfaceA_EnvironmentBasedClient ia = new InterfaceA_EnvironmentBasedClient("http://localhost:8080/yawl/ia");
		System.out.println(PasswordEncryptor.encrypt("YAWL", null));
	}
}
