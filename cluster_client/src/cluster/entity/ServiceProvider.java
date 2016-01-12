package cluster.entity;

import cluster.ditribute.strategy.RRScheduler;
import cluster.ditribute.strategy.Scheduler;
import cluster.event.exceptions.MigrationException;
import cluster.iaasClient.Adapter;
import cluster.iaasClient.OSAdapter;

import java.lang.invoke.SerializedLambda;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by fantasy on 2016/1/5.
 */
public class ServiceProvider {
    private List<Host> hostList;
    private List<Tenant> tenantList;
    private List<EngineRole> engineRoleList;
    private Adapter adapter = OSAdapter.getInstance();
    private Scheduler scheduler = new RRScheduler(this);

    public ServiceProvider(){


        hostList = adapter.getHosts();
        initMoniter();
    }

    public ServiceProvider(List<Host> hosts, List<Tenant> tenants, List<EngineRole> engines) {
        this.hostList = hosts;
        this.tenantList = tenants;
        this.engineRoleList = engines;
        initMoniter();
    }

    private void initMoniter(){
        Timer speedMonitor = new Timer();
        speedMonitor.schedule(new TimerTask() {
            @Override
            public void run() {
                hostList.forEach(Host::updateSpeed);
                tenantList.forEach(Tenant::updateSpeed);
            }
        }, 0, 5000);
        Timer SLOMonitor = new Timer();
        SLOMonitor.schedule(new TimerTask() {
            @Override
            public void run() {
                EngineRole[][] solution = scheduler.schedule(codeDistribution());
                HashMap<EngineRole, Host> ins = decodeDistribution(solution);
                ins.forEach((EngineRole e, Host h) -> {
                    try {
                        adapter.Migrate(e, h);
                    } catch (MigrationException e1) {
                        e1.printStackTrace();
                    }
                });
            }
        }, 0, 20 * 60 * 1000);
    }
    public EngineStatistics gatherStatistics(){
        return new EngineStatistics(this);
    }
    private void loadDataCenterSnapshot(){}
    private EngineRole[][] codeDistribution(){
        EngineRole[][] result = new EngineRole[hostList.size()][tenantList.size()];
        for (EngineRole e: engineRoleList){
            result[hostList.indexOf(e.getHost())][tenantList.indexOf(e.getTenant())] = e;
        }
        return result;
    }
    private HashMap<EngineRole, Host> decodeDistribution(EngineRole[][] newSolution){
        HashMap<EngineRole, Host> instrutions = new HashMap<>();
        for (int i = 0; i < newSolution.length; i++){
            for (int j = 0; j < newSolution[i].length; j++){
                EngineRole e = newSolution[i][j];
                Host h = hostList.get(i);
                if (!e.getHost().equals(h)){
                    instrutions.put(e, h);
                }
            }
        }
        return instrutions;
    }
}
