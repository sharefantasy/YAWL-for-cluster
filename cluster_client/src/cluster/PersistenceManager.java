package cluster;

import cluster.data.EngineInfo;
import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.authentication.YExternalClient;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.elements.state.YIdentifier;
import org.yawlfoundation.yawl.engine.YCaseNbrStore;
import org.yawlfoundation.yawl.engine.YNetData;
import org.yawlfoundation.yawl.engine.YNetRunner;
import org.yawlfoundation.yawl.engine.YWorkItem;
import org.yawlfoundation.yawl.engine.time.YLaunchDelayer;
import org.yawlfoundation.yawl.engine.time.YWorkItemTimer;
import org.yawlfoundation.yawl.exceptions.Problem;
import org.yawlfoundation.yawl.logging.table.*;
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
            EngineInfo.class
    };
    private static Logger _logger = Logger.getLogger(PersistenceManager.class);
    private static PersistenceManager _pm ;
    public PersistenceManager(boolean persistOn) {
        super(persistOn, new HashSet<>(Arrays.asList(persistclasses)));
    }

}
