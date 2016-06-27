package org.yawlfoundation.cluster.scheduleModule.repo;

import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by fantasy on 2016/5/11.
 */
public interface TenantRepo extends MongoRepository<Tenant, String> {
}
