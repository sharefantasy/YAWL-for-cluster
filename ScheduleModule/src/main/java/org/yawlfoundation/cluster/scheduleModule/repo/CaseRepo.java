package org.yawlfoundation.cluster.scheduleModule.repo;

import org.yawlfoundation.cluster.scheduleModule.entity.Case;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by fantasy on 2016/5/14.
 */
public interface CaseRepo extends MongoRepository<Case, String> {
    //    Case findById(String id);
    List<Case> findByInternalId(String internalId);
}
