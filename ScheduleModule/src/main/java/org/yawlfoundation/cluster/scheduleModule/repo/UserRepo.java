package org.yawlfoundation.cluster.scheduleModule.repo;

import org.yawlfoundation.cluster.scheduleModule.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by fantasy on 2016/5/24.
 */
public interface UserRepo extends MongoRepository<User, String> {
    User findByUserName(String userid);
}
