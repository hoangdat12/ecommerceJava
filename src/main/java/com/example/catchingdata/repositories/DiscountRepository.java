package com.example.catchingdata.repositories;

import com.example.catchingdata.models.DiscountModel.Discount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DiscountRepository extends MongoRepository<Discount, String> {
    Optional<Discount> findByDiscountCode(String s);
}
