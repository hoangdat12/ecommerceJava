package com.example.catchingdata.ultils.discount;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Data
@Slf4j
public abstract class DiscountCore {
    private String discountCode;
    private int quantity;
    private double discount;
    private String type;
    private String key;
    private String description;
    private Condition condition;
    private LocalDateTime createdAt;
    private LocalDateTime effectedFrom;
    private LocalDateTime expiresIn;
    public abstract double getPrice(double price);
    public DiscountCore(
            String discountCode,
            double discount,
            String type,
            String key,
            String description,
            LocalDateTime createdAt,
            int effectedFrom,
            int expiresIn,
            Condition condition
    ) {
        this.discountCode = discountCode;
        this.discount = discount;
        this.type = type;
        this.key = key;
        this.description = description;
        this.condition = condition;
        this.createdAt = createdAt;
        this.effectedFrom = LocalDateTime.now().withHour(effectedFrom).withMinute(0).withSecond(0).withNano(0); ;
        this.expiresIn = LocalDateTime.now().withHour(expiresIn).withMinute(0).withSecond(0).withNano(0);
    }
    public boolean isEffective() {
        LocalDateTime now = LocalDateTime.now();
        long time = now.compareTo(effectedFrom);
        return time >= 0 && time <= 3600;
    }
    public boolean isValidDiscount(String discountCode, double price) {
        if (price < this.condition.getGreater()) {
            return false;
        }
        if (price > this.condition.getLesser()) {
            return false;
        }
        return true;
    }
//    public double applyDiscount(Discount code, double price) {
//        if (!isValidDiscount(code.getDiscountCode())) {
//            log.info("Discount is not valid {}", code.getDiscountCode());
//            return 0.0;
//        }
//        if (!code.isEffective()) {
//            log.info("Discount is not Effective {}", code.getEffectedFrom());
//            return 0.0;
//        }
//        switch (code.type) {
//            case "coin":
//                return price;
//            case "percentage":
//                break;
//            case "amount":
//                break;
//            default:
//                log.info("Type discount is not valid {}", code.getType());
//                return 0.0;
//        }
//
//        return 10;
//    }
}
