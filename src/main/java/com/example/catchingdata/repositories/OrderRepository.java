package com.example.catchingdata.repositories;

import com.example.catchingdata.models.OrderModel.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByUserId(String o);
}
