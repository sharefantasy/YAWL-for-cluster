package cluster.workflowService.schedule;

import cluster.general.entity.EngineRole;
import cluster.general.entity.Host;
import cluster.general.entity.Tenant;
import cluster.general.entity.data.HostCapability;
import cluster.workflowService.schedule.strategy.BrutalScheduler;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fantasy on 2016/2/20.
 */

@Component("simpleConfigurationAdapter")
public class SimpleConfigurationAdapter {
    private List<Tenant> tenants;
    private List<Host> hosts;
    private List<EngineRole> engineRoleList;
    private double[][] capabilitySet;

    private int[][] oldConfig;
    private Scheduler scheduler;

    public void loadonfig(Collection<Tenant> tenants, Collection<Host> hosts) {
        if (!(tenants instanceof List)) {
            this.tenants = tenants.stream().collect(Collectors.toList());
        } else {
            this.tenants = (List<Tenant>) tenants;
        }
        if (!(hosts instanceof List)) {
            this.hosts = hosts.stream().collect(Collectors.toList());
        } else {
            this.hosts = (List<Host>) hosts;
        }
        oldConfig = codeDistribution();
        engineRoleList = tenants.stream()
                .map(Tenant::getEngineList)
                .reduce(new ArrayList<>(),
                        (e1, e2) -> {
                            e1.addAll(e2);
                            return e1;
                        });
        capabilitySet = getCapabilityset();
        scheduler = new BrutalScheduler(this.tenants, this.hosts,
                engineRoleList, capabilitySet);
    }


    public HashMap<EngineRole, Host> genConfig() {
        int[][] newConfig = scheduler.schedule(oldConfig);
        oldConfig = newConfig;
        return decodeDistribution(newConfig);
    }

    private int[][] codeDistribution() {
        int[][] result = new int[tenants.size()][hosts.size()];
        int count = 0;
        for (Tenant t : tenants) {
            for (EngineRole e : t.getEngineList()) {
                result[count++][hosts.indexOf(e.getHost())] += 1;
            }
        }
        return result;
    }

    public double[][] getCapabilityset() {
        if (capabilitySet == null) {
            int size = 0;
            //获取最大的引擎数
            for (Host h : hosts) {
                int c = 0;
                for (HostCapability hc : h.getCapabilitySet()) {
                    if (hc.geteNum() > c) {
                        c = hc.geteNum();
                    }
                }
                if (c > size) {
                    size = c;
                }
            }
            capabilitySet = new double[hosts.size()][size];
            int count = 0;
            for (Host h : hosts) {
                for (HostCapability hc : h.getCapabilitySet()) {
                    capabilitySet[count++][hc.geteNum()] = hc.getCapability();
                }
            }
        }
        return capabilitySet;

    }

    private HashMap<EngineRole, Host> decodeDistribution(int[][] configuration) {
        HashMap<EngineRole, Host> instructions = new HashMap<>();
        int[][] diff = oldConfig.clone();
        for (int i = 0; i < tenants.size(); i++) {
            for (int j = 0; j < hosts.size(); j++) {
                diff[i][j] = configuration[i][j] - diff[i][j];
            }
        }
        for (int i = 0; i < tenants.size(); i++) {
            Queue<EngineRole> roles = new ArrayDeque<>();
            for (int j = 0; j < hosts.size(); j++) {
                if (diff[i][j] > 0) {
                    final int tid = i, hid = j;
                    engineRoleList.stream()
                            .filter(e -> e.getTenant().getId() == tid)
                            .filter(e -> e.getHost().getId() == hid)
                            .limit(diff[i][j])
                            .forEach(roles::add);
                }
            }
            Host[] hostA = (Host[]) hosts.stream()
                    .sorted((o1, o2) -> (int) Math.signum(o1.getId() - o2.getId()))
                    .toArray();
            for (int j = 0; j < hosts.size(); j++) {
                if (diff[i][j] < 0) {
                    instructions.put(roles.poll(), hostA[j]);
                }
            }
        }
        return instructions;
    }
}
