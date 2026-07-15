package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.AddressRequestDTO;
import com.krushna.commercecore.dto.AddressResponseDTO;
import com.krushna.commercecore.model.Address;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.AddressRepository;
import com.krushna.commercecore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AddressResponseDTO createAddress(String username, AddressRequestDTO request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Address address = new Address(user, request.getStreet(), request.getCity(), 
                                     request.getZipCode(), request.getPhone(),
                                     request.getRecipientName(), request.getType());
        address.setDefault(request.isDefault());

        // If this is set as default, unset other default addresses
        if (request.isDefault()) {
            addressRepository.unsetOtherDefaultAddresses(user.getId(), null);
        }

        Address saved = addressRepository.save(address);
        return convertToDTO(saved);
    }

    public List<AddressResponseDTO> getUserAddresses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Address> addresses = addressRepository.findByUserId(user.getId());
        return addresses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AddressResponseDTO getDefaultAddress(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Address address = addressRepository.findDefaultAddressByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No default address found"));

        return convertToDTO(address);
    }

    @Transactional
    public AddressResponseDTO updateAddress(Long addressId, String username, AddressRequestDTO request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));

        // Check if address belongs to the user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Address does not belong to user");
        }

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setZipCode(request.getZipCode());
        address.setPhone(request.getPhone());
        address.setRecipientName(request.getRecipientName());
        address.setType(request.getType());

        // If this is set as default, unset other default addresses
        if (request.isDefault()) {
            addressRepository.unsetOtherDefaultAddresses(user.getId(), addressId);
        }
        address.setDefault(request.isDefault());

        Address updated = addressRepository.save(address);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteAddress(Long addressId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));

        // Check if address belongs to the user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Address does not belong to user");
        }

        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponseDTO setDefaultAddress(Long addressId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));

        // Check if address belongs to the user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Address does not belong to user");
        }

        // Unset all other default addresses
        addressRepository.unsetOtherDefaultAddresses(user.getId(), addressId);

        // Set this as default
        address.setDefault(true);
        Address updated = addressRepository.save(address);

        return convertToDTO(updated);
    }

    private AddressResponseDTO convertToDTO(Address address) {
        return new AddressResponseDTO(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getZipCode(),
                address.getPhone(),
                address.isDefault(),
                address.getRecipientName(),
                address.getType()
        );
    }
}
