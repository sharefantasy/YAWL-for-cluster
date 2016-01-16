package cluster.simulation;

import cluster.entity.EngineRole;
import cluster.entity.Host;
import cluster.entity.ServiceProvider;
import cluster.entity.Tenant;
import cluster.iaasClient.OSAdapter;

import java.util.*;

/**
 * Created by fantasy on 2016/1/14.
 */
public class testMain {


    public static final int tenantNum = 10;

    public static void main(String[] args){

        // virtual scheduler test
        final int hostNum = 40;
        List<Host> lh = new ArrayList<>();
        for (int i = 0; i < hostNum; i++) {
            Host h = new Host("Host_"+UUID.randomUUID().toString(), new Random().nextInt(70) + 10);
            lh.add(h);
        }
        List<Tenant> lt = new ArrayList<>();
        for (int i = 0; i < tenantNum; i++){
            Tenant t = new Tenant(i, new Random().nextDouble() % 200 + 60);
            lt.add(t);
        }
        double avgS = 14;
        double totalS = lt.stream()
                .mapToDouble(Tenant::getSLOspeed)
                .summaryStatistics().getSum();

        int sqdif = 2;
        int eNum = (int) Math.ceil(totalS / avgS);
        List<EngineRole> le = new ArrayList<>(eNum);

        // f: tenant, host -> engines
        int j = 0;
        for (Tenant t : lt) {
            double curS = 0;
            while (curS < t.getSLOspeed()){
                EngineRole e = new EngineRole("Engine_" + UUID.randomUUID().toString());
                e.updateSpeed(new Date(200 * (j++) + new Date().getTime()),
                        Math.sqrt(sqdif)* new Random().nextGaussian() + avgS);
                e.setTenant(t);
                le.add(e);
                curS+=e.getCurrentSpeed();
                t.getEngineList().add(e);
            }
        }
        for (int i = 0; i < le.size(); i++) {
            Host h = lh.get(i % hostNum);
            h.getEngineList().add(le.get(i));
            le.get(i).setHost(h);
        }

//        ServiceProvider sp = new ServiceProvider(lh,lt,le, OSAdapter.getInstance());
        EngineDataGenerator dg = new EngineDataGenerator(le, 200);
        ServiceProvider sp = new ServiceProvider(lh,lt,le, dg);
        sp.setStatisticInterval(200);
        dg.addObserver(sp);
        dg.startGenerating(30 * 1000);

    }
}
