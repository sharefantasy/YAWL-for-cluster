package cluster.entity;

import cluster.ditribute.strategy.RRScheduler;
import cluster.ditribute.strategy.Scheduler;
import cluster.event.exceptions.MigrationException;
import cluster.iaasClient.Adapter;
import cluster.iaasClient.envObserver;
import org.apache.log4j.Logger;

import java.util.*;


public class ServiceProvider implements envObserver {
    private static final Logger _logger = Logger.getLogger(ServiceProvider.class);

    public void setStatisticInterval(int statisticInterval) {
        this.statisticInterval = statisticInterval;
    }

    private int statisticInterval = 20 * 60 * 1000; // in millisecond
    private List<Host> hostList;
    private List<Tenant> tenantList;
    private List<EngineRole> engineRoleList;
    private Adapter adapter;
    private Scheduler scheduler = new RRScheduler(this);
    private final Timer sloMonitor = new Timer();
    private final Timer speedMonitor = new Timer();

    public ServiceProvider(){
        hostList = adapter.getHosts();
    }

    public ServiceProvider(List<Host> hosts, List<Tenant> tenants, List<EngineRole> engines, Adapter ad) {
        this.hostList = hosts;
        this.tenantList = tenants;
        this.engineRoleList = engines;
        this.adapter = ad;
    }

    public void startService(){
        if (!adapter.isStarted()){
            _logger.error("engines not ready");
            return;
        }
        speedMonitor.schedule(new TimerTask() {
            @Override
            public void run() {
                hostList.forEach(Host::updateSpeed);
                tenantList.forEach(Tenant::updateSpeed);
            }
        }, 0, 5000);
        sloMonitor.schedule(new TimerTask() {
            @Override
            public void run() {
                ArrayList<EngineRole>[][] solution = scheduler.schedule(codeDistribution());
                HashMap<EngineRole, Host> ins = decodeDistribution(solution);
                ins.forEach((EngineRole e, Host h) -> {
                    try {
                        adapter.Migrate(e, h);
                    } catch (MigrationException e1) {
                        e1.printStackTrace();
                    }
                });
            }
        }, 0, statisticInterval);
        _logger.info("start monitoring...");
    }

    public EngineStatistics gatherStatistics(){
        return new EngineStatistics(this);
    }
    private void loadDataCenterSnapshot(){}
    private ArrayList<EngineRole>[][] codeDistribution(){
        ArrayList[][] result = new ArrayList[hostList.size()][tenantList.size()];
        for (EngineRole e: engineRoleList){
            if (result[hostList.indexOf(e.getHost())][tenantList.indexOf(e.getTenant())] == null) {
                result[hostList.indexOf(e.getHost())][tenantList.indexOf(e.getTenant())] = new ArrayList<EngineRole>();
            }
            result[hostList.indexOf(e.getHost())][tenantList.indexOf(e.getTenant())].add(e);
        }
        return (ArrayList<EngineRole>[][])result;
    }
    private HashMap<EngineRole, Host> decodeDistribution(ArrayList<EngineRole>[][] newSolution){
        HashMap<EngineRole, Host> instrutions = new HashMap<>();
        for (int i = 0; i < newSolution.length; i++){
            for (int j = 0; j < newSolution[i].length; j++){
                if (newSolution[i][j] == null) {
                    continue;
                }
                for(EngineRole e : newSolution[i][j]){
                    Host h = hostList.get(i);
                    if (!e.getHost().equals(h)){
                        instrutions.put(e, h);
                    }
                }

            }
        }
        return instrutions;
    }

    @Override
    public void notifyEnvShutdown() {
        sloMonitor.cancel();
        speedMonitor.cancel();
        _logger.info("Stop Monitoring...");
    }

    @Override
    public void notifyEnvStart() {
        startService();
    }
}
