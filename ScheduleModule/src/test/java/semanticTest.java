import junit.framework.Assert;
import org.jdom2.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Spec;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.repo.EngineRepo;
import org.scheduleModule.repo.SpecRepo;
import org.scheduleModule.repo.TenantRepo;
import org.scheduleModule.service.ConnectionService;
import org.scheduleModule.service.SemanticService;
import org.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.util.HashSet;

/**
 * Created by fantasy on 2016/6/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class semanticTest {
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private SemanticService semanticService;
    @Autowired
    private EngineRepo engineRepo;

    @Autowired
    private TenantRepo tenantRepo;

    @Autowired
    private SpecRepo specRepo;

    Tenant tenant;
    Engine engine;
    Engine engine2;

    @Before
    public void SetDBEnvironment() {
        tenant = new Tenant("http://localhost:8080/resourceService/ib#resource");

        engine = new Engine("127.0.0.1", 8080, tenant);
//        engine2 = new Engine("127.0.0.1",8082,tenant);

        engineRepo.save(engine);
//        engineRepo.save(engine2);

        tenant.addEngine(engine);
//        tenant.addEngine(engine2);
        tenantRepo.save(tenant);

        System.out.println(tenant);
        System.out.println(engine);
//        System.out.println(engine2);
    }

    @After
    public void clearDB() {
        System.out.println("tenants: " + tenantRepo.count());
        System.out.println("engines: " + engineRepo.count());
        engineRepo.deleteAll();
        tenantRepo.deleteAll();
        specRepo.deleteAll();
    }

    @Test
    public void TestConnection() {
        String session = connectionService.getSession(engine);
        String session2 = connectionService.getSession(engine2);
        System.out.println(String.format("%s: %s", engine, session));
        System.out.println(String.format("%s: %s", engine2, session2));
        Assert.assertTrue(!SchedulerUtils.INVALID_USER.equals(session));
    }

    @Test
    public void TestCommonIa() {
        String session = connectionService.getSession(engine);
        String result = semanticService.getList(tenant);
        System.out.println(result);
        Assert.assertTrue(!result.equals(SchedulerUtils.failure("Invalid action or exception was thrown.")));

        result = semanticService.getLiveItems(tenant);
        System.out.println(result);
        Assert.assertTrue(!result.equals(SchedulerUtils.failure("Invalid action or exception was thrown.")));
    }

    @Test
    public void TestMerge() {
        String result = semanticService.getLiveItems(tenant);
        System.out.println(result);

    }

    private String uploadSpec() {
        File file = new File("src/test/java/hw.xml");
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream io = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(io));
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return semanticService.upload(tenant, sb.toString());
    }

    private String unloadSpec() {
        String result;
        result = semanticService.unload(tenant, "UID_8c36d244-2282-4063-a13c-ef04dd36f5d8", "0.5", "hw");
        return result;
    }

    @Test
    public void TestAllEngine() {
        String result = uploadSpec();
        System.out.println("upload specs");
        System.out.println(result);
        Assert.assertEquals(SchedulerUtils.SUCCESS, result);

        System.out.println("unload spec");
        result = unloadSpec();
        Assert.assertEquals(SchedulerUtils.SUCCESS, result);
    }


    @Test
    public void TestIbPost() {
        uploadSpec();
        Spec spec = specRepo.findAll().get(0);
        String result = semanticService.launchCase(tenant, spec.getSpecid(), spec.getVersion(), spec.getUri(), "", "", null, null, null);
        System.out.println(result);
        Assert.assertEquals(SchedulerUtils.SUCCESS, result);
        unloadSpec();
    }

    @Test
    public void TestIBGET() {
        String result = semanticService.getAllRunningCases(tenant);
        System.out.println(result);
    }

    @Test
    public void TestIaAll() {
        System.out.println(semanticService.getAccounts(tenant));
    }
}
