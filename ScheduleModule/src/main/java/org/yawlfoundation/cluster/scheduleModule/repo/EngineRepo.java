package org.yawlfoundation.cluster.scheduleModule.repo;

import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by fantasy on 2016/5/16.
 */
public interface EngineRepo extends MongoRepository<Engine, String> {
    List<Engine> findByAddress(String address);
}
