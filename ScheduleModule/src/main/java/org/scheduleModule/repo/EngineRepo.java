package org.scheduleModule.repo;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by fantasy on 2016/5/16.
 */
public interface EngineRepo extends MongoRepository<Engine, String> {
    Engine findByAddress(String address);
}
