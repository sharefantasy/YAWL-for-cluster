package org.scheduleModule.repo;

import org.scheduleModule.entity.Spec;
import org.scheduleModule.entity.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Created by fantasy on 2016/5/21.
 */
public interface SpecRepo extends MongoRepository<Spec, String> {
    Spec findBySpecid(String specid);
}

