import org.junit.Test;
import org.yawlfoundation.yawl.authentication.YExternalClient;
import org.yawlfoundation.yawl.engine.YEngine;
import org.yawlfoundation.yawl.engine.interfce.EngineGateway;
import org.yawlfoundation.yawl.engine.interfce.EngineGatewayImpl;
import org.yawlfoundation.yawl.exceptions.YPersistenceException;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fantasy on 2016/7/16.
 */
public class YEngineTest {
	@Test
	public void test() {
		YEngine engine = null;
		try {
			engine = YEngine.getInstance(true);
			System.out.println(YEngine.getPersistenceManager().isEnabled());
			System.out.println(YEngine.getInstance().addExternalClient(new YExternalClient("dfdd", "sdf", "df")));
			// System.out.println(engine.addExternalClient(new
			// YExternalClient("adsf","sdf","sdfad")));
		} catch (YPersistenceException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testGateway() {
		try {
			EngineGateway gateway = new EngineGatewayImpl(true, true);
			String session = gateway.connect("admin", PasswordEncryptor.encrypt("YAWL"), 0);

			System.out.println(session);
			System.out.println(gateway.checkConnection(session));
			System.out.println(gateway.getClientAccount("admin", PasswordEncryptor.encrypt("YAWL")));
			gateway.shutdown();
		} catch (YPersistenceException | RemoteException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
