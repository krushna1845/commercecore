package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.AddressRequestDTO;
import com.krushna.commercecore.dto.AddressResponseDTO;
import com.krushna.commercecore.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(
            @Valid @RequestBody AddressRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        AddressResponseDTO created = addressService.createAddress(userDetails.getUsername(), request);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getUserAddresses(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<AddressResponseDTO> addresses = addressService.getUserAddresses(userDetails.getUsername());
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/default")
    public ResponseEntity<AddressResponseDTO> getDefaultAddress(
            @AuthenticationPrincipal UserDetails userDetails) {
        AddressResponseDTO defaultAddress = addressService.getDefaultAddress(userDetails.getUsername());
        return ResponseEntity.ok(defaultAddress);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        AddressResponseDTO updated = addressService.updateAddress(addressId, userDetails.getUsername(), request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal UserDetails userDetails) {
        addressService.deleteAddress(addressId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<AddressResponseDTO> setDefaultAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal UserDetails userDetails) {
        AddressResponseDTO updated = addressService.setDefaultAddress(addressId, userDetails.getUsername());
        return ResponseEntity.ok(updated);
    }
}
