package com.example.catchingdata.dto.InventoryDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCheckIsStockMultipleProduct {
    List<RequestInventory> products;
}
