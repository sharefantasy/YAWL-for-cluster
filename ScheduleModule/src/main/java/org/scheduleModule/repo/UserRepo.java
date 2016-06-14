package org.scheduleModule.repo;

import org.scheduleModule.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

/**
 * Created by fantasy on 2016/5/24.
 */
public interface UserRepo extends MongoRepository<User, String> {
}
