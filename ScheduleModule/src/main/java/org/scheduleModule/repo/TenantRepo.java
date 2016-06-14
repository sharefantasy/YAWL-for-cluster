package org.scheduleModule.repo;

import org.scheduleModule.entity.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by fantasy on 2016/5/11.
 */
public interface TenantRepo extends MongoRepository<Tenant, String> {
}
