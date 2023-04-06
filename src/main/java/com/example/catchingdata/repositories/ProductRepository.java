package com.example.catchingdata.repositories;

import com.example.catchingdata.models.ProductModel.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}

