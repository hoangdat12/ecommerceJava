package com.example.catchingdata.controllers;

import com.example.catchingdata.services.HistoryBuyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/history/buy")
@RequiredArgsConstructor
@Slf4j
public class HistoryBuyController {
    private final HistoryBuyService historyBuyService;
    @GetMapping("/{userId}")
    public ResponseEntity<?> getHistoryBuyOfUser(
            @PathVariable String userId
    ) {
        log.info("Run!!!!");
        return historyBuyService.getAllHistoryBuyProductOfUser(userId);
    }
}
