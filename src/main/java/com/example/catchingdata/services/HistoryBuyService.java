package com.example.catchingdata.services;

import com.example.catchingdata.dto.HistoryResponseDTO;
import com.example.catchingdata.dto.OrderDTO.OrderProduct;
import com.example.catchingdata.models.HistoryBuy.HistoryBuy;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.repositories.HistoryBuyRepository;
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
public class HistoryBuyService {
    private final HistoryBuyRepository historyBuyRepository;
    private final MongoTemplate mongoTemplate;
    public boolean createHistoryOfUser(User user) {
        HistoryBuy history = HistoryBuy.builder()
                .user(user)
                .productsBought(new ArrayList<>())
                .build();
        return history.equals(null) ? false : true;
    }
    public boolean addProductToHistory(List<OrderProduct> orderProducts, User user) {
        // Check product is Exist in History of User
        HistoryBuy historyBuy = historyBuyRepository.findByUserId(user.getId()).orElse(null);
        if (historyBuy == null) {
            throw new BadRequest("User not found!");
        }
        for (OrderProduct orderProduct : orderProducts) {
                OrderProduct product = historyBuy.getProductsBought().stream()
                        .filter(tmp -> tmp.getId().equals(orderProduct.getId()))
                        .findFirst()
                        .orElse(null);
            if (product != null) {
                product.setQuantity(product.getQuantity() + orderProduct.getQuantity());
            } else {
                historyBuy.getProductsBought().add(orderProduct);
            }
        }
        historyBuyRepository.save(historyBuy);
        return true;
    }
    public ResponseEntity<?> getAllHistoryBuyProductOfUser(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        HistoryBuy history = mongoTemplate.findOne(query, HistoryBuy.class);
        if (history.equals(null)) {
            throw new NotFound("User not found!");
        } else {
            HistoryResponseDTO historyResponseDTO = new HistoryResponseDTO(
                    history.getUser().getId(),
                    history.getProductsBought()
            );
            return new Ok<>(historyResponseDTO).sender();
        }
    }
}
