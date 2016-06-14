package org.scheduleModule.repo;

import org.scheduleModule.entity.Case;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Created by fantasy on 2016/5/14.
 */
public interface CaseRepo extends MongoRepository<Case, String> {
    //    Case findById(String id);
    List<Case> findByInternalId(String internalId);
}
