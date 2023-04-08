package com.example.catchingdata.services;

import com.example.catchingdata.dto.InventoryDTO.InventoryCheckIsStockMultipleProduct;
import com.example.catchingdata.dto.InventoryDTO.ProductSaleOut;
import com.example.catchingdata.dto.InventoryDTO.RequestInventory;
import com.example.catchingdata.dto.OrderDTO.OrderProduct;
import com.example.catchingdata.dto.OrderDTO.OrderRequest;
import com.example.catchingdata.models.OrderModel.Order;
import com.example.catchingdata.repositories.OrderRepository;
import com.example.catchingdata.response.errorResponse.BadRequest;
import com.example.catchingdata.response.errorResponse.NotFound;
import com.example.catchingdata.response.successResponse.Ok;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final DiscountService discountService;
    private final ProductService productService;
    private final MongoTemplate mongoTemplate;
    public ResponseEntity<?> order(OrderRequest request, String userId)  {
        List<RequestInventory> requestInventories = new ArrayList<>();
        for (OrderProduct product : request.getProducts()) {
            RequestInventory requestInventory = new
                    RequestInventory(product.getId(), product.getQuantity());
            requestInventories.add(requestInventory);
        }
        ProductSaleOut productSaleOut =
                inventoryService.checkIsStockMultipleProduct(
                        new InventoryCheckIsStockMultipleProduct(requestInventories)
                );
        log.info("productSaleOut::: {}", productSaleOut);
        if (!productSaleOut.isStock()) {
            throw new IllegalArgumentException("Product sale out");
        }
        else {
            double price = request.getProducts()
                    .stream()
                    .map(OrderProduct::getPrice)
                    .mapToDouble(Double::doubleValue)
                    .sum();
            if (request.getCodeDiscount() != null) {
                price = discountService.getPriceWithDiscount(
                        request.getCodeDiscount(),
                        price,
                        request.getUserId()
                );
            }
            Order newOrder = Order.builder()
                    .userId(userId)
                    .products(request.getProducts())
                    .price(price)
                    .build();
            Order orderSaved = orderRepository.save(newOrder);
            return new Ok<>(orderSaved).sender();
        }
    }
    public ResponseEntity<?> confirmReceivedOrder(String userId, String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new NotFound("Order not found!");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BadRequest("Invalid information required!");
        }
        // Increment purchase of Product;
        productService.incrementPurchaseOfProduct(order.getProducts());
        // Delete order
        orderRepository.delete(order);
        return new Ok<>("Thank you for purchasing from us!").sender();
    }
    public ResponseEntity<?> getAllOrderOfUser(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        List<Order> ordersOfUser = mongoTemplate.find(query, Order.class);
        return new Ok<>(ordersOfUser).sender();
    }
    public ResponseEntity<?> getDetailOrder(String orderId) {
        Query query = new Query(Criteria.where("_id").is(orderId));
        Order order = mongoTemplate.findOne(query, Order.class);
        return new Ok<>(order).sender();
    }
}
