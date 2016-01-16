package cluster.ditribute.strategy;
import cluster.entity.EngineRole;
import cluster.entity.Host;
import cluster.entity.Tenant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fantasy on 2016/1/6.
 */
public class Distribution extends HashMap<Host, HashMap<Tenant, ArrayList<EngineRole>>>{}
