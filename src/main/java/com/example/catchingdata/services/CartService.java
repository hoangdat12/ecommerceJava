package com.example.catchingdata.services;

import com.example.catchingdata.dto.CartDTO.AddMultiProductToCartDto;
import com.example.catchingdata.dto.CartDTO.AddProductToCartDto;
import com.example.catchingdata.dto.CartDTO.DetailCartDTO;
import com.example.catchingdata.dto.ProductDTO.DeleteMultipleProductToCart;
import com.example.catchingdata.dto.ProductDTO.DeleteProductToCart;
import com.example.catchingdata.dto.CartDTO.DetailCartResponse;
import com.example.catchingdata.models.CartModel.Cart;
import com.example.catchingdata.models.ProductModel.Product;
import com.example.catchingdata.models.CartModel.ProductCart;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.repositories.CartRepository;
import com.example.catchingdata.response.errorResponse.InternalServerError;
import com.example.catchingdata.response.errorResponse.NotFound;
import com.example.catchingdata.response.successResponse.Created;
import com.example.catchingdata.response.successResponse.Ok;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    public ResponseEntity<?> createCart(User user) {
        Cart isExistCart = cartRepository.findByUserId(user.getId()).orElse(null);
        if (isExistCart != null) {
            throw new InternalException("Cart is Exits!");
        }
        Cart cart = Cart.builder()
                .user(user)
                .products(new ArrayList<>())
                .build();
        Cart newCart = cartRepository.save(cart);
        DetailCartDTO detailCart = new DetailCartDTO(
                newCart.getId(),
                newCart.getUser().getId(),
                newCart.getProducts()
        );
        return new Created<>(detailCart).sender();
    }
    public ResponseEntity<?> deleteCart(String cartId, User user) {
        Query query = new Query(Criteria.where("_id").is(cartId)
                .and("userId").is(user.getId()));
        mongoTemplate.remove(query, Cart.class);
        Ok<String> response = new Ok<>("Deleted!");
        return response.sender();
    }
    public ResponseEntity<?> addProductToCart(User user, AddProductToCartDto request) {
        final String userId = user.getId();
        Query query = new Query(Criteria.where("_id").is(request.getCartId())
                .and("userId").is(userId));

        Cart cart = mongoTemplate.findOne(query, Cart.class);
        if (cart == null) {
            throw new NotFound("Cart not found!");
        }
        boolean productIsExist = cart.getProducts().stream()
                .anyMatch(
                        product -> product.getProductId()
                                .equals(request.getProductCart().getProductId())
                );
        Update update;
        // Check product is Exist or not
        // If exist then increment quantity
        ProductCart productCart = request.getProductCart();
        if (productIsExist) {
            log.info("data::: {} {} {}", userId, productCart.getProductId(), productCart.getQuantity());
            Document result = updateQuantity(
                    cart.getId(),
                    productCart.getProductId(),
                    productCart.getQuantity()
            );

            log.info("Result increment quantity::: {}", result);
            if (result == null) {
                throw new InternalServerError("DB Error!");
            } else {
                redisTemplate.delete(cart.getId());
                Ok<Document> response = new Ok<>(result);
                return response.sender();
            }
        }
        // Else then add product
        else {
            update = new Update()
                    .push("products", productCart);
            redisTemplate.delete(cart.getId());
            return getResponseEntityWithFindAndModify(query, update);
        }
    }
    public ResponseEntity<?> addMultiProductToCart(User user, AddMultiProductToCartDto request) {
        final String userId = user.getId();
        // Get cart
        Query query = new Query(Criteria.where("_id").is(request.getCartId())
                .and("userId").is(userId));

        Cart cart = mongoTemplate.findOne(query, Cart.class);
        if (cart == null) {
            throw new NotFound("Cart not found!");
        }
        List<ProductCart> productCarts =  request.getProductCarts();

        // Get list product Id
        List<String> productIds = productCarts.stream()
                .map(ProductCart::getProductId)
                .toList();
        // Check Product is Exist in Cart
        List<ProductCart> productsExist = cart.getProducts().stream()
                .filter(productCart -> productIds.contains(productCart.getProductId()))
                .toList();
        // Update Cart
        for (ProductCart productCart : productCarts) {
            if (productsExist.stream().anyMatch(p -> p.getProductId().equals(productCart.getProductId()))) {
                // Increment quantity
                ProductCart existingProduct = productsExist.stream()
                        .filter(p -> p.getProductId().equals(productCart.getProductId()))
                        .findFirst().orElse(null);
                assert existingProduct != null;
                updateQuantity(cart.getId(), existingProduct.getProductId(), productCart.getQuantity());
            } else {
                // Add Product To Cart
                addProductToCart(productCart, query);
            }
        }

        redisTemplate.delete(request.getCartId());
        return new Ok<String>("Add Product to Cart successfully!").sender();
    }

    public ResponseEntity<?> removeProductToCart(User user, DeleteProductToCart request) {
        Query query = new Query(Criteria.where("_id").is(request.getCartId())
                .and("userId").is(user.getId() ));
        Update update = new Update()
                .pull("products",  Query.query(Criteria.where("productId").is(request.getProductId())));
        FindAndModifyOptions options = getOptionsMongoUpdate();
        Cart result = mongoTemplate.findAndModify(query, update, options, Cart.class);
        if (result == null) {
            throw new IllegalArgumentException("Product not found in Cart!");
        } else {
            Ok<String> response = new Ok<>("Deleted!");
            redisTemplate.delete(request.getCartId());
            return response.sender();
        }
    }
    public ResponseEntity<?> removeMultipleProductToCart(DeleteMultipleProductToCart request) {
        Query query = new Query(Criteria.where("_id").is(request.getCartId())
                .and("userId").is(request.getUserId()));
        Cart cart = mongoTemplate.findOne(query, Cart.class);
        if (cart == null) {
            throw new NotFound("Cart not found!");
        }
        List<ProductCart> updatedCartItems = cart.getProducts().stream()
                .filter(cartItem -> !request.getProductIds().contains(cartItem.getProductId()))
                .collect(Collectors.toList());
        cart.setProducts(updatedCartItems);
        mongoTemplate.save(cart);
        return new Ok<Cart>(cart).sender();
    }
public ResponseEntity<?> getDetailCartOfUserV2(String userId) throws IOException {
        Query query = new Query(Criteria.where("userId").is(userId));
        Cart cart = mongoTemplate.findOne(query, Cart.class);
        if (cart == null) {
            throw new NotFound("User not found!");
        }

        List<Product> products = new ArrayList<>();
        double price = 0;
        int quantityAllProduct = 0;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cart.getId()))) {
            Map<Object, Object> dataJson = redisTemplate.opsForHash().entries(cart.getId());
            log.info("Catch::: {}", dataJson);
            String quantityAllProductString = (String) dataJson.get("quantityAllProduct");
            quantityAllProduct = Integer.parseInt(quantityAllProductString);
            String priceString = (String) dataJson.get("price");
            price = Double.parseDouble(priceString);
            dataJson.remove("price");
            dataJson.remove("quantityAllProduct");

            for (Object productJson : dataJson.values()) {
                Product product = objectMapper.readValue((String) productJson, Product.class);
                products.add(product);
            }
        } else {
            List<String> productIds = cart.getProducts().stream()
                    .map(ProductCart::getProductId)
                    .collect(Collectors.toList());

            if (!productIds.isEmpty()) {
                Query queryProduct = Query.query(Criteria.where("_id").in(productIds));
                products = mongoTemplate.find(queryProduct, Product.class);
                Map<String, String> productMap = new HashMap<>();
                for (Product product : products) {
                    int quantity = cart.getProducts().stream()
                            .filter(productCart -> productCart.getProductId().equals(product.getId()))
                            .findFirst()
                            .map(ProductCart::getQuantity)
                            .orElse(0);
                    price += product.getPrice() * quantity;
                    quantityAllProduct += quantity;
                    productMap.put(product.getId(), objectMapper.writeValueAsString(product));
                }
                productMap.put("price", objectMapper.writeValueAsString(price));
                productMap.put("quantityAllProduct", objectMapper.writeValueAsString(quantityAllProduct));
                redisTemplate.opsForHash().putAll(cart.getId(), productMap);
                redisTemplate.expire(cart.getId(), 15, TimeUnit.SECONDS);
            }
        }

        DetailCartResponse response = DetailCartResponse.builder()
                .userId(cart.getUser().getId())
                .id(cart.getId())
                .products(products)
                .quantityAllProduct(quantityAllProduct)
                .price(price)
                .build();
        return new Ok<>(response).sender();
    }

    // PRIVATE
    private FindAndModifyOptions getOptionsMongoUpdate() {
        return new FindAndModifyOptions()
                .upsert(true)
                .returnNew(true);
    }
    private ResponseEntity<?> getResponseEntityWithFindAndModify(Query query, Update update) {
        FindAndModifyOptions options = getOptionsMongoUpdate();
        Cart result = mongoTemplate.findAndModify(query, update, options, Cart.class);
        if (result == null) {
            throw new InternalServerError("DB Error!");
        } else {
            Ok<Cart> response = new Ok<>(result);
            return response.sender();
        }
    }
    private Document updateQuantity(String cartId, String productId, int quantity) {
        Bson filter = Filters.and(
                Filters.eq("_id", new ObjectId(cartId)),
                Filters.elemMatch("products", Filters.eq("productId", productId))
        );
        Bson updateQuantity = Updates.inc("products.$.quantity", quantity);
        return mongoTemplate.getCollection("carts")
                .findOneAndUpdate(filter, updateQuantity, new FindOneAndUpdateOptions()
                        .upsert(true)
                        .returnDocument(ReturnDocument.AFTER)
                );
    }
    private void addProductToCart(ProductCart productCart, Query query) {
        Update update = new Update()
                .push("products", productCart);
        FindAndModifyOptions options = new FindAndModifyOptions()
                .upsert(true)
                .returnNew(true);
        mongoTemplate.findAndModify(query, update, options, Cart.class);
    }
}
