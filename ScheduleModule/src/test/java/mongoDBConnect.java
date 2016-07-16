import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Snapshot;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.entity.User;
import org.yawlfoundation.cluster.scheduleModule.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

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
	private SnapshotRepo snapshotRepo;

    @Test
    public void createuser() {
		Tenant tenant = tenantRepo.findOne("5772216c1c41000fb88eafc2");
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
		Tenant tenant = tenantRepo.findOne("577499847ca26e1914044ea0");
//        tenant.addEngine(engine);
//        engine.setTenant(tenant);
//        engineRepo.save(engine);
//        tenantRepo.save(tenant);

		Engine engine = engineRepo.findOne("5775ea7cf3138a21a4c5c0e2");
        tenant.addEngine(engine);
        tenantRepo.save(tenant);
    }
	@Test
	public void mo() {
		Tenant tenant = tenantRepo.findOne("577499847ca26e1914044ea0");
		tenant.setDefaultWorklist("http://127.0.0.1:8000/resourceService/ib#resource");
		tenantRepo.save(tenant);
	}

}
