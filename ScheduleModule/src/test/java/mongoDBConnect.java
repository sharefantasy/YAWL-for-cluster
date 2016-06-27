import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.entity.User;
import org.yawlfoundation.cluster.scheduleModule.repo.CaseRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.EngineRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.TenantRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fantasy on 2016/5/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class mongoDBConnect {

    @Autowired
    private EngineRepo engineRepo;
    @Autowired
    private TenantRepo tenantRepo;
    @Autowired
    private CaseRepo caseRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
//    private SemanticService semanticService;

    @Test
    public void connectDB() {
        Tenant tenant = new Tenant();
        tenant.setDefaultWorklist("http://localhost:700/resourceService/ib#resource");
        tenantRepo.save(tenant);

        Engine engine = new Engine("127.0.0.1", 8080, tenant);
        Engine engine2 = new Engine("127.0.0.1", 8082, tenant);

        engineRepo.save(engine);
        engineRepo.save(engine2);
        tenantRepo.save(tenant);

        System.out.println(tenant);
        System.out.println(engine);
        System.out.println(engine2);

        tenant.addEngine(engine);
        tenant.addEngine(engine2);

        System.out.println(tenant);
        Assert.assertNotNull(tenant.getId());
        Assert.assertNotNull(engine.getId());
        Assert.assertNotNull(tenant.getEngineSet());
    }

    @Test
    public void createuser() {
        Tenant tenant = tenantRepo.findOne("576cf863cae642357c4a3de0");
        User admin = null;
        try {
            admin = new User("admin", PasswordEncryptor.encrypt("YAWL"), tenant);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (admin != null) {
            userRepo.save(admin);
        }
    }

    @Test
    public void link() {
//        Engine engine = engineRepo.findByAddress("127.0.0.1").get(1);
        Tenant tenant = tenantRepo.findOne("5767b5afb20dd21d98abbaef");
//        tenant.addEngine(engine);
//        engine.setTenant(tenant);
//        engineRepo.save(engine);
//        tenantRepo.save(tenant);
        Engine engine = new Engine("127.0.0.1", 2000);
        engineRepo.save(engine);
        tenant.getEngineSet().clear();
        tenant.addEngine(engine);
        tenantRepo.save(tenant);
    }
}
