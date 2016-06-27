import cluster.workflowService.ServiceProvider;
import cluster.simulation.EngineDataGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by fantasy on 2016/2/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class testsim {
    @Autowired
    @Qualifier("engineDataGenerator")
    private EngineDataGenerator dg;

    private ServiceProvider sp;

    @Before
    public void beforeTest() {

//        TimeScaler.getInstance().destroy();
    }

    @Test(timeout = 60 * 1000)
    public void testScheduler() {

    }
}
