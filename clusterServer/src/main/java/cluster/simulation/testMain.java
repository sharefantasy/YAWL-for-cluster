package cluster.simulation;

import cluster.general.entity.EngineRole;
import cluster.general.entity.Host;
import cluster.general.service.EngineRoleService;
import cluster.workflowService.ServiceProvider;
import cluster.general.entity.Tenant;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

/**
 * Created by fantasy on 2016/2/18.
 */
public class testMain {
	public static void main(String[] args) {
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		ServiceProvider sp;
		EngineDataGenerator dg = (EngineDataGenerator) ctx.getBean("engineDataGenerator");
		EngineRoleService engineRoleService = (EngineRoleService) ctx.getBean("engineRoleService");
		final int hostNum = 40;
		List<Host> lh = new ArrayList<>();
		for (int i = 0; i < hostNum; i++) {
			Host h = new Host();
			h.setName("Host_" + UUID.randomUUID().toString());
			// TODO: 2016/2/21 generate host capability set
			h.setId(i);
			lh.add(h);
		}
		List<Tenant> lt = new ArrayList<>();
		final int tenantNum = 40;
		for (int i = 0; i < tenantNum; i++) {
			Tenant t = new Tenant();
			t.setName("Tenant_" + UUID.randomUUID().toString());
			t.setSLOspeed(new Random().nextDouble() % 200 + 60);
			t.setId(i);
			lt.add(t);
		}
		double avgS = 14;
		double totalS = lt.stream().mapToDouble(Tenant::getSLOspeed).summaryStatistics().getSum();

		int sqdif = 2;
		int eNum = (int) Math.ceil(totalS / avgS);
		List<EngineRole> le = new ArrayList<>(eNum);

		// f: tenant, host -> engines
		int j = 0;
		for (Tenant t : lt) {
			double curS = 0;
			while (curS < t.getSLOspeed()) {

				EngineRole e = new EngineRole();
				e.setRole("Engine_" + UUID.randomUUID().toString());
				engineRoleService.updateSpeed(e, new Date(200 * (j++) + new Date().getTime()),
						Math.sqrt(sqdif) * new Random().nextGaussian() + avgS);
				e.setTenant(t);
				le.add(e);
				curS += e.getCurrentSpeed();
				t.getEngineList().add(e);
			}
		}
		for (int i = 0; i < le.size(); i++) {
			Host h = lh.get(i % hostNum);
			h.getEngineList().add(le.get(i));
			le.get(i).setHost(h);
		}

		// ServiceProvider sp = new ServiceProvider(lh,lt,le,
		// OSAdapter.getInstance());
		dg.init(le, 200);
		sp = new ServiceProvider(lh, lt, le);

		sp.setAdapter(dg);
		// sp.setStatisticInterval(200);
		// dg.addObserver(sp);
		dg.startGenerating(30 * 1000);
		sp.startService();

	}

}
