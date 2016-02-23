package cluster.workflowService.service;

import cluster.util.PersistenceManager;
import cluster.util.event.EventCenter;
import cluster.util.exceptions.GeneralException;
import cluster.workflowService.ServiceProvider;
import cluster.workflowService.entity.WorkflowPlan;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fantasy on 2016/2/20.
 */
@Service("WorkflowPlanService")
@Transactional
public class WorkflowPlanService {
    @Autowired
    private PersistenceManager _pm;

    @Autowired
    private ServiceProvider _sp;

    @Autowired
    private EventCenter _ec;

    private static final Logger _logger = Logger.getLogger(WorkflowPlan.class);
    private Timer starter;
    private Timer stopper;

    public List<WorkflowPlan> getAllWorkflowPlans() {
        return (List<WorkflowPlan>) _pm.getObjectsForClass("WorkflowPlan");
    }

    public WorkflowPlan getWorkflowPlanById(long id) {
        return (WorkflowPlan) _pm.get(WorkflowPlan.class, id);
    }

    public WorkflowPlan getCurrentWorkingPlan() {
        return (WorkflowPlan) _pm.getObjectsForClassWhere("WorkflowPlan", "isWorking = true").get(0);
    }

    public void startPlan(long id) {
        WorkflowPlan plan = getWorkflowPlanById(id);
        if (plan.isWorking()) return;
        starter = new Timer();
        starter.schedule(new TimerTask() {
            @Override
            public void run() {
                _sp.startService();
                plan.setWorking(true);
                _ec.trigger("planStarted", plan);
            }
        }, new Date().getTime() - plan.getStartTime().getTime());
        stopper = new Timer();
        stopper.schedule(new TimerTask() {
            @Override
            public void run() {
                _sp.shutdownService();
                plan.setWorking(false);
                _ec.trigger("planShutdown", plan);
            }
        }, plan.getEndTime().getTime() - new Date().getTime());
    }

    public void save(WorkflowPlan plan) {
        WorkflowPlan w = (WorkflowPlan) _pm.get(WorkflowPlan.class, plan.getId());
        if (w == null) {
            _pm.exec(plan, HibernateEngine.DB_INSERT, true);
        } else {
            _pm.exec(plan, HibernateEngine.DB_UPDATE, true);
        }
    }

    public void shutdown(long wid) {
        starter.cancel();
        stopper.cancel();
        WorkflowPlan w = getWorkflowPlanById(wid);
        w.setWorking(false);
        _sp.shutdownService();
    }
}
