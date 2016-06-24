import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.repo.EngineRepo;
import org.scheduleModule.repo.SpecRepo;
import org.scheduleModule.repo.TenantRepo;
import org.scheduleModule.service.MergeService;
import org.scheduleModule.service.Rules;
import org.scheduleModule.service.translate.ResponseTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fantasy on 2016/6/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class IndependentFunctionTest {

    @Autowired
    private MergeService mergeService;
    @Autowired
    private ResponseTranslator responseTranslator;
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
    public void MergeTestWithTheSame() {
        long t = System.currentTimeMillis();
        System.out.println();
        List<String> results = new ArrayList<>();
        String result1 = "<AllRunningCases><specificationID identifier=\"UID_8c36d244-2282-4063-a13c-ef04dd36f5d8\" version=\"0.5\" uri=\"hw\"><caseID>1</caseID><caseID>2</caseID><caseID>3</caseID><caseID>4</caseID></specificationID></AllRunningCases>";
        String result2 = "<AllRunningCases><specificationID identifier=\"UID_8c36d244-2282-4063-a13c-ef04dd36f5d8\" version=\"0.5\" uri=\"hw\"><caseID>5</caseID><caseID>6</caseID><caseID>7</caseID><caseID>8</caseID></specificationID></AllRunningCases>";
        results.add(result1);
        results.add(result2);
        System.out.println(mergeService.merge(results, "getAllRunningCases"));
        System.out.println(System.currentTimeMillis() - t);
    }

    @Test
    public void MergeTestWithDiffrentAttr() {
        long t = System.currentTimeMillis();
        List<String> results = new ArrayList<>();
        String result1 = "<AllRunningCases><specificationID identifier=\"UID_8c36d244-2282-4063-a13c-ef04dd36f5d8\" version=\"0.5\" uri=\"hw\"><caseID>1</caseID><caseID>2</caseID><caseID>3</caseID><caseID>4</caseID></specificationID></AllRunningCases>";
        String result2 = "<AllRunningCases><specificationID identifier=\"UID_8c36d244-2282-4063-a13c-ef04dd36f5d2\" version=\"0.5\" uri=\"hw\"><caseID>5</caseID><caseID>6</caseID><caseID>7</caseID><caseID>8</caseID></specificationID></AllRunningCases>";
        results.add(result1);
        results.add(result2);
        System.out.println(mergeService.merge(results, "getAllRunningCases"));
        System.out.println(System.currentTimeMillis() - t);
    }

    @Test
    public void TranslateTest() {
    }

}
