package cluster;

import org.yawlfoundation.yawl.engine.interfce.interfaceA.InterfaceA_EnvironmentBasedClient;

import java.io.IOException;
import java.util.Map;

/**
 * Created by fantasy on 2015/8/2.
 */
public class interfaceA_reader extends InterfaceA_EnvironmentBasedClient{
    private String username = "cluster";
    private String password = "cluster";
    public final static String url = "http://127.0.0.1:8080/yawl/ia";
    private String sessionHandle = "false";
    public interfaceA_reader() {
        super(url);
    }
    public void init(){
        try {
            System.out.println(this.checkConnection(sessionHandle));
            sessionHandle = this.connect(username,password);
//            System.out.println(sessionHandle);
//            System.out.println(this.isHibernateStatisticsEnabled(sessionHandle));
            System.out.println(this.getHibernateStatistics(sessionHandle));
            System.out.println(this.restore());
//            System.out.println(this.getRegisteredYAWLServices(sessionHandle));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("connection error");
        }
    }

    public String restore() throws IOException {
        Map params = this.prepareParamMap("restore", sessionHandle);
        return this.executeGet(url,params);
    }
    public static void main(String[] args){
        interfaceA_reader ia = new interfaceA_reader();
        ia.init();
    }
}
