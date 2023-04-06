package com.example.catchingdata.repositories;

import com.example.catchingdata.models.UserModel.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String s);
}
