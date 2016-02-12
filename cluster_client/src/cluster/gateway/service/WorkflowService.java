package cluster.gateway.service;

import cluster.PersistenceManager;
import cluster.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by fantasy on 2016/2/7.
 */
@Service("workflowService")
@Transactional
public class WorkflowService {
    @Autowired
    private PersistenceManager _pm;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private HostService hostService;

    @Autowired
    private ServiceProvider serviceProvider;


    public EngineStatistics gatherSPinfo() {
        return serviceProvider.gatherStatistics();
    }

    public void startService() {
        serviceProvider.startService();
    }

    public void shutdownService() {
        serviceProvider.notifyEnvShutdown();
    }

    public ServiceEntity getCurrentService() {
        return null;
    }

    public void renewService(ServiceEntity entity) {

    }
}
