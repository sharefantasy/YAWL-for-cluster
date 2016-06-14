import com.mongodb.assertions.Assertions;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.repo.CaseRepo;
import org.scheduleModule.repo.EngineRepo;
import org.scheduleModule.repo.TenantRepo;
import org.scheduleModule.repo.UserRepo;
import org.scheduleModule.service.SemanticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

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
    private SemanticService semanticService;

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

}
