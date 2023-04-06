//package com.example.catchingdata.services;
//
//import com.example.catchingdata.models.ProductModel.BucketModel;
//import com.example.catchingdata.models.ProductModel.Product;
//import com.example.catchingdata.repositories.BucketRepository;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.FindAndModifyOptions;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.convert.Bucket;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.regex.Pattern;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class BucketService {
//    private final BucketRepository repository;
//    private final MongoTemplate mongoTemplate;
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final ObjectMapper objectMapper;
//    public boolean insert(Product product, String bucketId) {
//        try {
//            Pattern _bucketId = Pattern.compile("^" + bucketId + "_");
//            // Ensure that the bucket does not exceed 20 products.
//            Query query = new Query(Criteria.where("bucketId").regex(_bucketId).and("count").lt(20));
//            Update update = new Update()
//                    .push("products", product)
//                    .inc("count", 1)
//                    .setOnInsert("bucketId", bucketId + "_" + new Date().getTime());
//            FindAndModifyOptions options = new FindAndModifyOptions().upsert(true).returnNew(true);
//            mongoTemplate.findAndModify(query, update, options, BucketModel.class);
//            return true;
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public List<Product> pagination(
//            String bucketId,
//            Integer page,
//            Integer limit,
//            String sortedBy,
//            String type
//    )
//            throws JsonProcessingException
//    {
//        String _bucket = bucketId + page.toString() + limit.toString();
//        String regexBucketId = "^" + _bucket + "_";
//        Pattern _bucketIdQuery = Pattern.compile("^" + bucketId + "_");
//        if (sortedBy.equals("name")) {
//            if (Boolean.TRUE.equals(redisTemplate.hasKey(regexBucketId))) {
//                // Return data to the client
//                String data = (String) redisTemplate.opsForValue().get(regexBucketId);
//                BucketModel bucketProducts = objectMapper.readValue(
//                        data,
//                        new TypeReference<BucketModel>() {}
//                );
//                return bucketProducts.getProducts();
//            }
//            else {
//                BucketModel products = getProductsInBucketModel(type, _bucketIdQuery, sortedBy, page);
//                if (products != null) {
//                    int expirationTime = ThreadLocalRandom.current().nextInt(600, 1000);
//                    redisTemplate.opsForValue().set(
//                            regexBucketId,
//                            objectMapper.writeValueAsString(products),
//                            Duration.ofSeconds(expirationTime)
//                    );
//                    if (!limit.equals(20)) {
//                        return products.getProducts().subList(0, limit);
//                    } else {
//                        return products.getProducts();
//                    }
//
//                } else {
//                    return null;
//                }
//            }
//        }
//        else {
//            BucketModel products = getProductsInBucketModel(type, _bucketIdQuery, sortedBy, page);
//            if (products != null) {
//                if (!limit.equals(20)) {
//                    return products.getProducts().subList(0, limit);
//                } else {
//                    return products.getProducts();
//                }
//
//            } else {
//                return null;
//            }
//        }
//    }
//
//    private BucketModel getProductsInBucketModel(String type, Pattern _bucketIdQuery, String sortedBy, int page) {
//        Query query = new Query()
//                .addCriteria(Criteria.where("bucketId").regex(_bucketIdQuery))
//                .skip(page - 1);
//        BucketModel bucketModel = mongoTemplate.findOne(query, BucketModel.class);
//        bucketModel.getProducts().sort(Comparator.comparing(Product::getName));
//    }
//}
