import junit.framework.Assert;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.repo.EngineRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.SpecRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.TenantRepo;
import org.yawlfoundation.cluster.scheduleModule.service.ConnectionService;
import org.yawlfoundation.cluster.scheduleModule.service.MergeService;
import org.yawlfoundation.cluster.scheduleModule.service.translate.ResponseTranslator;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;
import org.yawlfoundation.yawl.authentication.YExternalClient;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EnvironmentBasedClient;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.io.IOException;
import java.util.*;

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

	@Autowired
	private ConnectionService connectionService;
    Tenant tenant;
    Engine engine;
    Engine engine2;
	//
	// @Before
	// public void SetDBEnvironment() {
	// tenant = new Tenant("http://localhost:8080/resourceService/ib#resource");
	//
	// engine = new Engine("127.0.0.1", 8080, tenant);
	//// engine2 = new Engine("127.0.0.1",8082,tenant);
	//
	// engineRepo.save(engine);
	//// engineRepo.save(engine2);
	//
	// tenant.addEngine(engine);
	//// tenant.addEngine(engine2);
	// tenantRepo.save(tenant);
	//
	// System.out.println(tenant);
	// System.out.println(engine);
	//// System.out.println(engine2);
	// }

	// @After
	// public void clearDB() {
	// System.out.println("tenants: " + tenantRepo.count());
	// System.out.println("engines: " + engineRepo.count());
	// engineRepo.deleteAll();
	// tenantRepo.deleteAll();
	// specRepo.deleteAll();
	// }

    @Test
    public void MergeTestWithTheSame() {
        long t = System.currentTimeMillis();
        System.out.println();
        List<String> results = new ArrayList<>();
        String result1 = "<AllRunningCases><specificationID identifier=\"UID_8c36d244-2282-4063-a13c-ef04dd36f5d8\" version=\"0.5\" uri=\"hw\"><caseID>1</caseID><caseID>2</caseID><caseID>3</caseID><caseID>4</caseID></specificationID></AllRunningCases>";
        String result2 = "<AllRunningCases><specificationID identifier=\"UID_8c36d244-2282-4063-a13c-ef04dd36f5d8\" version=\"0.5\" uri=\"hw\"><caseID>5</caseID><caseID>6</caseID><caseID>7</caseID><caseID>8</caseID></specificationID></AllRunningCases>";
        results.add(result1);
        results.add(result2);
		Document doc = mergeService.merge(results, "getAllRunningCases");
		System.out.println(SchedulerUtils.documentToString(doc));
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
		Document doc = mergeService.merge(results, "getAllRunningCases");
		System.out.println(SchedulerUtils.documentToString(doc));
        System.out.println(System.currentTimeMillis() - t);
    }
    @Test
	public void domtess() {
		String result1 = "<AllRunningCases><caseID>1</caseID><caseID>2</caseID><caseID>3</caseID><caseID>4</caseID></AllRunningCases>";
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(result1);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Iterator<Element> eiter = (Iterator<Element>) doc.getRootElement().elementIterator();
		List<Element> elements = new ArrayList<>(10);
		// while (eiter.hasNext()){
		// elements.add(eiter.next());
		// eiter.remove();
		// }
		System.out.println(elements.size());
		System.out.println(SchedulerUtils.documentToString(doc));
    }

	// @Test
	// public void domtest() {
	// String res = null;
	// Engine engine = engineRepo.findAll().get(0);
	// Map<String,String> params = new HashMap<>();
	// params.put("action","getAllRunningCases");
	// params.put("sessionHandle",connectionService.getSession(engine));
	// try {
	// res = connectionService.forward(engine,params,"ib");
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// System.out.println(res);
	// InterfaceB_EnvironmentBasedClient ib = new
	// InterfaceB_EnvironmentBasedClient("http://127.0.0.1:8080/yawl/ib/");
	// try {
	// String rest = ib.getAllRunningCases(ib.connect("admin","YAWL"));
	// System.out.println(rest);
	// Assert.assertEquals(rest,res);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// }
	// private Set<YExternalClient> getClientAccounts(String result){
	// Set<YExternalClient> accounts = new HashSet<YExternalClient>();
	// Document doc = JDOMUtil.stringToDocument(result);
	// if (doc != null) {
	// for (Element e : doc.getRootElement().getChildren()) {
	// accounts.add(new YExternalClient(e));
	// }
	// }
	// return accounts ;
	// }

	@Test
	public void dd() {
		String a = "<response>1</response>";
		System.out.println(SchedulerUtils.unwrap(a));
	}
}
