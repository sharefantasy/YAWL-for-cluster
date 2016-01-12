package cluster.gateway;

import cluster.entity.EngineRole;
import cluster.entity.Host;
import cluster.entity.ServiceProvider;
import cluster.entity.Tenant;
import org.yawlfoundation.yawl.elements.e2wfoj.E2WFOJNet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fantasy on 2016/1/7.
 */
public class DataCenterGateway extends HttpServlet {
    private ServiceProvider provider;

    @Override
    public void init(ServletConfig config) throws ServletException {
        List<Host> hosts = new ArrayList<>();
        List<Tenant> tenants = new ArrayList<>();
        List<EngineRole> engineRoles = new ArrayList<>();
        provider = new ServiceProvider();
    }
}
