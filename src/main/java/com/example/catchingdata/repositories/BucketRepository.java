package com.example.catchingdata.repositories;

import com.example.catchingdata.models.ProductModel.BucketModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BucketRepository extends MongoRepository<BucketModel, String> {
    Slice<BucketModel> findByBucketIdStartingWith(String bucketIdPrefix, Pageable pageable);
}
