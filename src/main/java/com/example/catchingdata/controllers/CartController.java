package com.example.catchingdata.controllers;

import com.example.catchingdata.dto.CartDTO.AddMultiProductToCartDto;
import com.example.catchingdata.dto.CartDTO.AddProductToCartDto;
import com.example.catchingdata.dto.ProductDTO.DeleteMultipleProductToCart;
import com.example.catchingdata.dto.ProductDTO.DeleteProductToCart;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.services.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;
    @PostMapping("/create")
    public ResponseEntity<?> createCart(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return cartService.createCart(user);
    }
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCart(
            HttpServletRequest request,
            @PathVariable String cartId
    ) {
        User user = (User) request.getAttribute("user");
        return cartService.deleteCart(cartId, user);
    }
    @PostMapping("/add")
    public ResponseEntity<?> addProductToCart(
            HttpServletRequest request,
            @Valid @RequestBody AddProductToCartDto requestAdd
    ) {
        User user = (User) request.getAttribute("user");
        return cartService.addProductToCart(user, requestAdd);
    }
    @PostMapping("/adds")
    public ResponseEntity<?> addProductToCart(
            HttpServletRequest request,
            @Valid @RequestBody AddMultiProductToCartDto requestAdd
    ) {
        User user = (User) request.getAttribute("user");
        return cartService.addMultiProductToCart(user, requestAdd);
    }
    @PostMapping("/delete")
    public ResponseEntity<?> deleteProductToCart(
            HttpServletRequest request,
            @Valid @RequestBody DeleteProductToCart requestDelete
    ) {
        User user = (User) request.getAttribute("user");
        return cartService.removeProductToCart(user, requestDelete);
    }
    @PostMapping("/deletes")
    public ResponseEntity<?> deleteMultipleProductToCart(
            @Valid @RequestBody DeleteMultipleProductToCart request) {
        return cartService.removeMultipleProductToCart(request);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getDetailCartOfUser(@PathVariable String userId) throws IOException {
        return cartService.getDetailCartOfUserV2(userId);
    }
}
