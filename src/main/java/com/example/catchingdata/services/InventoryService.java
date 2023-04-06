package com.example.catchingdata.services;

import com.example.catchingdata.dto.InventoryDTO.InventoryCheckIsStockMultipleProduct;
import com.example.catchingdata.dto.InventoryDTO.ProductSaleOut;
import com.example.catchingdata.dto.InventoryDTO.RequestCreateInventory;
import com.example.catchingdata.dto.InventoryDTO.RequestInventory;
import com.example.catchingdata.models.InventoryModel.Inventory;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.repositories.InventoryRepository;
import com.example.catchingdata.response.errorResponse.BadRequest;
import com.example.catchingdata.response.errorResponse.NotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final MongoTemplate mongoTemplate;

    public void createInventoryForProduct(RequestCreateInventory request) {
        Inventory inventory = Inventory.builder()
                .product(request.getProduct())
                .quantity(request.getQuantity())
                .build();
        inventoryRepository.save(inventory);
    }
    public boolean isStock(RequestInventory request) {
        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElse(null);
        if (inventory == null) {
            return false;
        }
        return inventory.getQuantity() > request.getQuantity();
    }
    public ProductSaleOut checkIsStockMultipleProduct(InventoryCheckIsStockMultipleProduct request) {
        List<String> productIds = request.getProducts().stream()
                .map(product -> {
                    if (product.getQuantity() <= 0) {
                        throw new BadRequest("Quantity must be greater 0");
                    }
                    return product.getProductId();
                })
                .collect(Collectors.toList());
        Query query = new Query(Criteria.where("product").in(productIds));
        List<Inventory> inventories = mongoTemplate.find(query, Inventory.class);
        ProductSaleOut productSaleOut = new ProductSaleOut();
        for (RequestInventory requestInventory : request.getProducts()) {
            Inventory inventory = inventories.stream()
                    .filter(ivt -> Objects.equals(ivt.getProduct().getId(), requestInventory.getProductId()))
                    .findFirst()
                    .orElse(null);
            if (inventory == null) {
                throw new NotFound("Product not found");
            }
            if (requestInventory.getQuantity() > inventory.getQuantity()) {
                productSaleOut.getProductSaleOuts().add(inventory.getProduct());
            }
        }
        productSaleOut.setStock(productSaleOut.getProductSaleOuts().isEmpty());
        return productSaleOut;
    }
    public boolean increaseQuantityProduct(RequestInventory request, User user) {
        if (!user.getRole().equals("ADMIN") && !user.getRole().equals("STAFF")) {
            throw new BadRequest("You not have permission!");
        }
        // Get inventory of product
        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElse(null);
        if (inventory == null) {
            return false;
        }
        // Set again quantity for product
        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        inventoryRepository.save(inventory);
        return true;
    }
    public boolean decreaseQuantityProduct(RequestInventory request) {
        // Get inventory of product
        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElse(null);
        if (inventory == null) {
            return false;
        }
        // Check product isStock or not
        boolean isStock = isStock(request);
        if (!isStock) {
            return false;
        }
        // Set again quantity for product
        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventoryRepository.save(inventory);
        return true;
    }
}
