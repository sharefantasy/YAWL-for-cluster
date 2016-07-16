package org.yawlfoundation.cluster.scheduleModule.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Snapshot;

import java.util.Date;
import java.util.List;

/**
 * Created by fantasy on 2016/7/9.
 */
public interface SnapshotRepo extends MongoRepository<Snapshot, String> {
	List<Snapshot> findByEngine(Engine engine);
	@Query(value = "{ 'recordTime' : ?0, 'engine': ?1}")
	Snapshot findByRecordTimeAndEngine(Date RecordTime, Engine engine);
}
