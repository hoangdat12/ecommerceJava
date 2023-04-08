package com.example.catchingdata.repositories;

import com.example.catchingdata.models.HistoryBuy.HistoryBuy;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface HistoryBuyRepository extends MongoRepository<HistoryBuy, String> {
    Optional<HistoryBuy> findByUserId(String s);
}
