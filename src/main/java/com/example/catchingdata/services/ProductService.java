package com.example.catchingdata.services;

import com.example.catchingdata.dto.InventoryDTO.RequestCreateInventory;
import com.example.catchingdata.dto.OrderDTO.OrderProduct;
import com.example.catchingdata.dto.ProductDTO.PaginationDTO;
import com.example.catchingdata.dto.ProductDTO.ProductRequest;
import com.example.catchingdata.models.ProductModel.Product;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.repositories.ProductRepository;
import com.example.catchingdata.response.errorResponse.Forbbiden;
import com.example.catchingdata.response.errorResponse.InternalServerError;
import com.example.catchingdata.response.successResponse.Created;
import com.example.catchingdata.response.successResponse.Ok;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisTemplate<String, Object> redisTemplate;
    private final InventoryService inventoryService;
//    private final BucketService bucketService;
    public ResponseEntity<?> createProduct(ProductRequest request, User user) {
        if (!user.getRole().equals("ADMIN") && !user.getRole().equals("STAFF")) {
            throw new Forbbiden("You do not have permission!");
        }
        Product newProduct = Product.builder()
                .code(request.getCode())
                .name(request.getName())
                .author(request.getAuthor())
                .description(request.getDescription())
                .image_url(request.getImage_url())
                .price(request.getPrice())
                .type(request.getType())
                .active(true)
                .purchases(0)
                .specs(request.getSpecs())
                .build();
        // Create new Product
        Product productCreated = productRepository.save(newProduct);
        // Create Inventory for Product
        RequestCreateInventory requestCreateInventory =
                new RequestCreateInventory(productCreated, 0);
        inventoryService.createInventoryForProduct(requestCreateInventory);
        // Add in pagination
//        bucketService.insert(productCreated, "product");
        return new Created<>(productCreated).sender();
    }
    public ResponseEntity<?> updateProduct(String productId, ProductRequest request, User user) {
        if (!user.getRole().equals("ADMIN") && !user.getRole().equals("STAFF")) {
            throw new Forbbiden("You do not have permission!");
        }
        Query query = new Query(Criteria.where("_id").is(productId));
        Update update = new Update()
                .set("code", request.getCode())
                .set("name", request.getName())
                .set("author", request.getAuthor())
                .set("description", request.getDescription())
                .set("image_url", request.getImage_url())
                .set("price", request.getPrice())
                .set("specs", request.getSpecs());
        UpdateResult result = mongoTemplate.updateFirst(query, update, Product.class);
        Ok<UpdateResult> response = new Ok<>(result);
        return response.sender();
    }
    public ResponseEntity<?> deleteProduct(String productId, User user) {
        if (!user.getRole().equals("ADMIN") && !user.getRole().equals("STAFF")) {
            throw new Forbbiden("You do not have permission!");
        }
        Query query = new Query(Criteria.where("_id").is(productId));
        mongoTemplate.remove(query, Product.class);
        Ok<String> response = new Ok<>("Deleted!");
        return response.sender();
    }
    public ResponseEntity<?> getProduct(String productId) throws JsonProcessingException {
        Product product;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(productId))) {
            String data = (String) redisTemplate.opsForValue().get(productId);
            product = objectMapper.readValue(data, Product.class);
            log.info("Catch:::: {}", product);
        } else {
            log.info("Catch empty");
            Query query = new Query(Criteria.where("_id").is(productId));
            product = mongoTemplate.findOne(query, Product.class);
            if (product == null) {
                Ok<String> response = new Ok<>("Product not found!");
                return response.sender();
            }
            redisTemplate.opsForValue().set(
                    productId,
                    objectMapper.writeValueAsString(product),
                    Duration.ofSeconds(15)
            );
        }
        Ok<Product> response = new Ok<>(product);
        return response.sender();
    }
    public ResponseEntity<?> getAllProduct() {
        List<Product> products = mongoTemplate.findAll(Product.class);
        Ok<List<Product>> response = new Ok<>(products);
        return response.sender();
    }
    public List<Product> getAllProductInProductIds(List<String> productIds) {
        Query query = new Query(Criteria.where("_id").in(productIds));
        List<Product> products = mongoTemplate.find(query, Product.class);
        if (products.isEmpty()) {
            return null;
        } else {
            return products;
        }
    }
    public boolean incrementPurchaseOfProduct(List<OrderProduct> orderProducts) {
        List<String> productIds = orderProducts.stream()
                .map(OrderProduct::getId)
                .collect(Collectors.toList());
        List<Product> products = getAllProductInProductIds(productIds);
        if (products == null) {
            throw new InternalServerError("Product not found!");
        }
        for (Product product: products) {
            OrderProduct productOrder = orderProducts.stream()
                    .filter(orderProduct -> orderProduct.getId().equals(product.getId()))
                    .findFirst()
                    .orElse(null);
            if (productOrder == null) {
                throw new InternalServerError("Product not found!");
            }
            product.setPurchases(product.getPurchases() + productOrder.getQuantity());
        }
        productRepository.saveAll(products);
        return true;
    }
    public ResponseEntity<?> pagination(
            Integer page,
            Integer limit,
           String sortedBy,
           String type
    ) throws JsonProcessingException {
        String id = page.toString() + limit.toString() + sortedBy + type;
        List<Product> products = null;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(id))) {
            // If has catch
            log.info("Pagination with catch");
            String data = (String) redisTemplate.opsForValue().get(id);
                products = objectMapper.readValue(
                        data,
                        new TypeReference<List<Product>>() {}
                );
        } else {
            // If has catch
            log.info("Pagination with not catch");
            Query query;
            if (type.equals("asc")) {
                query = new Query()
                        .with(Sort.by(sortedBy))
                        .limit(limit)
                        .skip((long) (page - 1) * limit);
            } else {
                query = new Query()
                        .with(Sort.by(Sort.Direction.DESC, sortedBy))
                        .limit(limit)
                        .skip((long) (page - 1) * limit);
            }
            // Get Product From DB
            products = mongoTemplate.find(query, Product.class);
            // Save List Product in Redis with time expires
            int expirationTime = ThreadLocalRandom.current().nextInt(1, 100);
            redisTemplate.opsForValue().set(
                    id,
                    objectMapper.writeValueAsString(products),
                    Duration.ofSeconds(900 + expirationTime)
            );
        }
        PaginationDTO paginationDTO = new PaginationDTO(
                products, page, limit, sortedBy, type, products.size()
        );
        return new Ok<>(paginationDTO).sender();
    }
    public ResponseEntity<?> searchProduct(String keyword) {
        String keywordNormalize = Normalizer.normalize(keyword, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        Query query = new Query(Criteria.where("name")
                .regex(keywordNormalize, "i")
                .regex(keywordNormalize, "m")
        ).limit(20);
        List<Product> products = mongoTemplate.find(query, Product.class);
        return new Ok<>(products).sender();
    }
//    public ResponseEntity<?> getProducts(
//            int page,
//            int limit,
//            String sortedBy,
//            String type
//    ) throws JsonProcessingException {
//        log.info("Query::: {}, {}, {}, {}", page, limit, sortedBy, type);
//        List<Product> products = bucketService.pagination("product", page, limit, sortedBy, type);
//        return new Ok<>(products).sender();
//    }
//    public ResponseEntity<?> getTrendProduct() {
//
//    }
}
