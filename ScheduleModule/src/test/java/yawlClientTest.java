import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.yawlfoundation.yawl.engine.interfce.interfaceA.InterfaceA_EnvironmentBasedClient;
import org.yawlfoundation.yawl.util.StringUtil;

import java.io.IOException;

/**
 * Created by fantasy on 2016/6/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class yawlClientTest {

    @Test
    public void Test() {
        String a = "<response><yawlService id=\"http://localhost:8080/yawlWSInvoker\"><documentation>web service invoker</documentation><servicename>wsInvokerService</servicename><servicepassword>8phRh2xst/DA3WrZHhuyU4Hzle8=</servicepassword><assignable>true</assignable></yawlService><yawlService id=\"http://localhost:8080/workletService/ib\"><documentation>the worklet service</documentation><servicename>workletService</servicename><servicepassword>UMmCkMt6LZo2d0/aThXvTWrTiKA=</servicepassword><assignable>true</assignable></yawlService><yawlService id=\"http://localhost:8000/scheduleModule/alter/5767b5afb20dd21d98abbaef/resourceService/127.0.0.1/2000/ib\"><documentation></documentation><servicename>DefaultWorklist</servicename><servicepassword>ehBHOJc1c7Y/E73HodgW4JtgFq0=</servicepassword><assignable>false</assignable></yawlService><yawlService id=\"http://localhost:8080/mailService/ib\"><documentation>mail service</documentation><servicename>mailService</servicename><servicepassword>iH2HgTZHFwlTk3SFQVQNZykH13g=</servicepassword><assignable>true</assignable></yawlService></response>";
        String b = "";
        System.out.println(StringUtil.unwrap(a));
//        Document doc = null;
//        try {
//            doc = DocumentHelper.parseText(a);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        Element res = DocumentHelper.createElement("response");
//        for (Object e : doc.getRootElement().elements()) {
//            res.add(((Element) e).createCopy());
//        }
//        Document newDoc = DocumentHelper.createDocument(res);
//        System.out.println(newDoc.asXML());
    }

    @Test
    public void Tests2() {
        InterfaceA_EnvironmentBasedClient ia = new InterfaceA_EnvironmentBasedClient("http://localhost:8080/yawl/ia");
        try {
            String session = ia.connect("admin", "YAWL");
            System.out.println(ia.getHibernateStatistics(session));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
