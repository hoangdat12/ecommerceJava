package com.example.catchingdata.controllers;

import com.example.catchingdata.dto.InventoryDTO.RequestInventory;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.response.successResponse.Ok;
import com.example.catchingdata.services.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    @PostMapping("/increase")
    public ResponseEntity<?> increaseQuantityProduct(
            HttpServletRequest request,
            @RequestBody RequestInventory requestInventory
    ) {
        User user = (User) request.getAttribute("user");
        boolean status = inventoryService.increaseQuantityProduct(requestInventory, user);
        if (status) {
            return new Ok<>("Increase quantity of Product successfully!").sender();
        } else {
            throw new IllegalArgumentException("Increase quantity of Product successfully!");
        }
    }
}
