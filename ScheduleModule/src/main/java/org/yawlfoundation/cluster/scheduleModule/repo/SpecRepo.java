package org.yawlfoundation.cluster.scheduleModule.repo;

import org.yawlfoundation.cluster.scheduleModule.entity.Spec;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by fantasy on 2016/5/21.
 */
public interface SpecRepo extends MongoRepository<Spec, String> {
}

