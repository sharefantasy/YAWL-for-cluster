package org.yawlfoundation.cluster.scheduleModule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Snapshot;
import org.yawlfoundation.cluster.scheduleModule.repo.EngineRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.SnapshotRepo;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by fantasy on 2016/7/9.
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController {

	@Autowired
	private EngineRepo engineRepo;
	@Autowired
	private SnapshotRepo snapshotRepo;
	@RequestMapping(value = "/casespeed/{eid}", method = RequestMethod.POST)
	public @ResponseBody String caseSpeedCount(@PathVariable String eid, @RequestParam("casenum") long casenum,
			@RequestParam("datetime") long timestamp) {
		Engine engine = engineRepo.findOne(eid);
		if (engine == null) {
			return SchedulerUtils.failure("no such engine");
		}
		Date recordTime = new Date(timestamp);
		if (snapshotRepo.findByRecordTimeAndEngine(recordTime, engine) == null) {
			return SchedulerUtils.failure("Record already exists");
		}
		// Snapshot snapshot = new Snapshot(recordTime,casenum,);
		// snapshotRepo.save(snapshot);
		return SchedulerUtils.WRAP_SUCCESS;
	}

}
