import cluster.util.iaasClient.OSAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.model.compute.HostAggregate;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by fantasy on 2016/2/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class testOSplatform {

    private OSClient os;

    @Before
    public void getOS() {
        String OS_AUTH_URL = "http://192.168.0.15:5000/v2.0/";
        String OS_AUTH_NAME = "admin";
        String OS_AUTH_PASSWORD = "password";
        String OS_AUTH_PROJECT = "demo";
        try {
            os = OSFactory.builderV2()
                    .endpoint(OS_AUTH_URL)
                    .credentials(OS_AUTH_NAME, OS_AUTH_PASSWORD)
                    .tenantName(OS_AUTH_PROJECT)
                    .authenticate();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void connect() {
    }

    @Test
    public void getHost() {
        List<HostAggregate> oshost = (List<HostAggregate>) os.compute().hostAggregates().list();
        oshost.stream().forEach(e -> System.out.println(e.getId()));
    }
}
