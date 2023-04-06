package com.example.catchingdata.controllers;

import com.example.catchingdata.dto.InformationDiscount;
import com.example.catchingdata.models.DiscountModel.Discount;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.response.successResponse.Ok;
import com.example.catchingdata.services.DiscountService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/discount")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;
    @PostMapping("/create")
    private ResponseEntity<?> createDiscount(
            @RequestBody InformationDiscount informationDiscount,
            HttpServletRequest request
    ) {
        User user = (User) request.getAttribute("user");
        Discount discount = discountService.createDiscountCode(user, informationDiscount);
        return new Ok(discount).sender();
    }
}
