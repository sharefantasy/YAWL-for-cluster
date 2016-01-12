package cluster;

import cluster.entity.Engine;
import cluster.entity.EngineRole;
import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by fantasy on 2015/8/26.
 */
public class PersistenceManager extends HibernateEngine {

    private static Class[] persistclasses = {
//            YSpecification.class, YNetRunner.class, YWorkItem.class, YIdentifier.class,
//            YNetData.class, YAWLServiceReference.class, YExternalClient.class,
//            YWorkItemTimer.class, YLaunchDelayer.class, YCaseNbrStore.class, Problem.class,
//            YLogSpecification.class, YLogNet.class, YLogTask.class, YLogNetInstance.class,
//            YLogTaskInstance.class, YLogEvent.class, YLogDataItemInstance.class,
//            YLogDataType.class, YLogService.class, YAuditEvent.class,
            Engine.class, EngineRole.class
    };
    private static Logger _logger = Logger.getLogger(PersistenceManager.class);
    private static PersistenceManager _pm ;
    public PersistenceManager(boolean persistOn) {
        super(persistOn, new HashSet<>(Arrays.asList(persistclasses)));
    }

}
