package org.yawlfoundation.cluster.scheduleModule.service;

import org.yawlfoundation.cluster.scheduleModule.service.router.RoutingRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yawlfoundation.cluster.scheduleModule.service.router.strategy.*;

import java.util.HashMap;

@Component
public class RoutingRuleFactory {

    private static RoutingRuleFactory instance = new RoutingRuleFactory();

    public static RoutingRuleFactory getInstance() {
        return instance;
    }

    @Autowired
    protected AllEngineInTenant all;
    @Autowired
    protected AnyEngineInTenant any;
    @Autowired
    protected OneEngineByCaseOrWorkitem one;
    @Autowired
    protected NewAllocationByTenant newAllocate;
    @Autowired
    protected InnerAndAll innerAndAll;

	@Autowired
	protected InnerAndOne innerAndOne;

    protected static final HashMap<String, String> interfacePath = new HashMap<String, String>() {{
        put("a", "ia");
        put("bi", "ib");
        put("ao", "/resourceService/ib");
    }};

    public RoutingRule buildRoutingRule(String action) {
        switch (action) {
            case "all":
                return all;
            case "any":
                return any;
            case "one":
                return one;
            case "new":
                return newAllocate;
            case "inner_all":
                return innerAndAll;
			case "inner_one" :
				return innerAndOne;
            default:
                return null;
        }
    }

    public static String getInterfacePath(String interfaceToken) {
        return interfacePath.get(interfaceToken);
    }
}
