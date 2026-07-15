package com.krushna.commercecore.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.krushna.commercecore.dto.AddressRequestDTO;
import com.krushna.commercecore.dto.AddressResponseDTO;
import com.krushna.commercecore.model.Address;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.AddressRepository;
import com.krushna.commercecore.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Unit Tests")
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
    }

    @Test
    @DisplayName("createAddress() - should save a new address and clear existing defaults")
    void createAddress_shouldSaveAddressAndUnsetOtherDefaults() {
        AddressRequestDTO request = new AddressRequestDTO();
        request.setStreet("123 Main St");
        request.setCity("Mumbai");
        request.setZipCode("400001");
        request.setPhone("9876543210");
        request.setDefault(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        AddressResponseDTO response = addressService.createAddress("testuser", request);

        assertThat(response.getStreet()).isEqualTo("123 Main St");
        assertThat(response.isDefault()).isTrue();
        verify(addressRepository).unsetOtherDefaultAddresses(1L, null);
        verify(addressRepository).save(any(Address.class));
    }
}
