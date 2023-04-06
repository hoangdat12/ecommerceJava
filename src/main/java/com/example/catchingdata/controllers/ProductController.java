package com.example.catchingdata.controllers;

import com.example.catchingdata.dto.ProductDTO.ProductIds;
import com.example.catchingdata.dto.ProductDTO.ProductRequest;
import com.example.catchingdata.models.ProductModel.Product;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.response.errorResponse.InternalServerError;
import com.example.catchingdata.response.successResponse.Ok;
import com.example.catchingdata.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @PostMapping
    public ResponseEntity<?> createProduct(
            HttpServletRequest request,
            @RequestBody ProductRequest productRequest
    ) {
            User user = (User) request.getAttribute("user");
            return productService.createProduct(productRequest, user);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody ProductRequest productRequest
    ) {
           User user = (User) request.getAttribute("user");
           return productService.updateProduct(id, productRequest, user);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            HttpServletRequest request,
            @PathVariable String id
    ) {
           User user = (User) request.getAttribute("user");
           return productService.deleteProduct(id, user);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(
            @PathVariable String id
    ) throws JsonProcessingException {
        try {
            return productService.getProduct(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerError("Server Error!");
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllProduct() {
        try {
            return productService.getAllProduct();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerError("Server Error!");
        }
    }
    @GetMapping("")
    public ResponseEntity<?> getProducts(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
            @RequestParam(value = "sortedBy", required = false, defaultValue = "name") String sortedBy,
            @RequestParam(value = "type", required = false, defaultValue = "asc") String type
    ) {
        try {
            return productService.pagination(page, limit, sortedBy, type);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerError("Server Error!");
        }
    }
    @PostMapping("/productIds")
    public ResponseEntity<?> getAllProductInProductIds(@RequestBody ProductIds productIds) {
        try {
            return new Ok<>(productService.getAllProductInProductIds(productIds.getProductIds()))
                    .sender();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerError("Server Error!");
        }
    }
//    @GetMapping("/trend")
//    public ResponseEntity<?> getTrendProduct() {
//        try {
//            return productService.getTrendProduct();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new InternalServerError("Server Error!");
//        }
//    }
}
