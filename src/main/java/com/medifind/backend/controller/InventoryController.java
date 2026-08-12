package com.medifind.backend.controller;

import com.medifind.backend.dto.request.InventoryRequest;
import com.medifind.backend.dto.response.InventoryResponse;
import com.medifind.backend.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(
            InventoryService inventoryService
    ) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> addInventory(
            @Valid @RequestBody InventoryRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventoryService.addInventory(request));
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public ResponseEntity<List<InventoryResponse>>
    getPharmacyInventory(
            @PathVariable Long pharmacyId
    ) {
        return ResponseEntity.ok(
                inventoryService.getPharmacyInventory(pharmacyId)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<InventoryResponse>>
    searchAvailableMedicine(
            @RequestParam String query,
            @RequestParam(defaultValue = "") String city
    ) {
        return ResponseEntity.ok(
                inventoryService.searchAvailableMedicine(query, city)
        );
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long inventoryId,
            @Valid @RequestBody InventoryRequest request
    ) {
        return ResponseEntity.ok(
                inventoryService.updateInventory(
                        inventoryId,
                        request
                )
        );
    }
}