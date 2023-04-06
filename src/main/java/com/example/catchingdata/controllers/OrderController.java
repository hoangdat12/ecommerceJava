package com.example.catchingdata.controllers;

import com.example.catchingdata.dto.OrderDTO.ConfirmOrder;
import com.example.catchingdata.dto.OrderDTO.OrderRequest;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.services.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<?> order(
            HttpServletRequest request,
            @RequestBody OrderRequest orderRequest
    ) {
        final User user = (User) request.getAttribute("user");
        return orderService.order(orderRequest, user.getId());
    }
    // Test
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmOrder(
            HttpServletRequest request,
            @RequestBody ConfirmOrder confirmOrder
    ) {
        final User user = (User) request.getAttribute("user");
        return orderService.confirmReceivedOrder(user.getId(), confirmOrder.getOrderId());
    }
    // Get All Order Of User
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllOrderOfUser(
            @PathVariable String userId
    ) {
        return orderService.getAllOrderOfUser(userId);
    }
    // Get Detail Order
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getDetailOrder(
            @PathVariable String orderId
    ) {
        return orderService.getDetailOrder(orderId);
    }
}
