package com.example.catchingdata.services;

import com.example.catchingdata.dto.InformationDiscount;
import com.example.catchingdata.models.DiscountModel.Discount;
import com.example.catchingdata.models.DiscountModel.UserUsedDiscount;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.repositories.DiscountRepository;
import com.example.catchingdata.response.errorResponse.BadRequest;
import com.example.catchingdata.response.errorResponse.Forbbiden;
import com.example.catchingdata.ultils.discount.DiscountCore;
import com.example.catchingdata.ultils.discount.DiscountFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountService {
    private final DiscountRepository discountRepository;
    public double getPriceWithDiscount(String codeDiscount, double price, String userId) {
        DiscountFactory discountFactory = new DiscountFactory();
        // Check discount code in database
        Discount discountExist = discountRepository.findByDiscountCode(codeDiscount).orElse(null);
        if (discountExist == null) {
            throw new IllegalArgumentException("Discount not found!");
        }
        // Check user used discount
        boolean isUsed = discountExist.getUserUsed().stream()
                .anyMatch(userUsed -> userUsed.getUserId() == userId);
        if (isUsed) {
            throw new IllegalArgumentException("Discount only used 1!");
        }
        DiscountCore discountCreated = discountFactory.createDiscount(
                discountExist.getType(),
                discountExist.getDiscountCode(),
                discountExist.getDiscount(),
                discountExist.getKey(),
                discountExist.getDescription(),
                discountExist.getCreatedAt(),
                discountExist.getEffectedFrom(),
                discountExist.getExpiresIn(),
                discountExist.getCondition()
        );

        if (!discountCreated.isEffective()) {
            throw new BadRequest("The discount is currently not valid or has expired!");
        }

        if (!discountCreated.isValidDiscount(codeDiscount, price)) {
            throw new BadRequest("The discount not valid with condition to use it!");
        }

        // Save data of user used Discount (Each person can only use it once)
        UserUsedDiscount userUsedDiscount = new UserUsedDiscount(userId);
        discountExist.getUserUsed().add(userUsedDiscount);
        discountRepository.save(discountExist);

        return discountCreated.getPrice(price);
    }
    public Discount createDiscountCode(User user, InformationDiscount informationDiscount) {
        if (!user.getRole().equals("ADMIN") && !user.getRole().equals("STAFF")) {
            throw new Forbbiden("You do not have permission!");
        }
        Discount newDiscount = Discount.builder()
                .type(informationDiscount.getType())
                .discountCode(informationDiscount.getDiscountCode())
                .discount(informationDiscount.getDiscount())
                .quantity(informationDiscount.getQuantity())
                .key(informationDiscount.getKey())
                .description(informationDiscount.getDescription())
                .condition(informationDiscount.getCondition())
                .createdAt(LocalDateTime.now())
                .effectedFrom(informationDiscount.getEffectedFrom())
                .expiresIn(informationDiscount.getExpiresIn())
                .userUsed(new ArrayList<>())
                .build();
        Discount discount = discountRepository.save(newDiscount);
        return discount;
    }
}
