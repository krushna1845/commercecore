package com.krushna.commercecore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressRequestDTO {

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address must be less than 255 characters")
    private String street;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    @NotBlank(message = "Zip code is required")
    @Size(max = 20, message = "Zip code must be less than 20 characters")
    @Pattern(regexp = "^[0-9]{5,6}(-[0-9]{4})?$", message = "Invalid zip code format")
    private String zipCode;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[1-9][0-9]{9,14}$", message = "Invalid phone number format")
    private String phone;

    private boolean isDefault = false;

    private String recipientName;

    private String type = "SHIPPING"; // SHIPPING, BILLING

    public AddressRequestDTO() {}

    public AddressRequestDTO(String street, String city, String zipCode, String phone, boolean isDefault) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.phone = phone;
        this.isDefault = isDefault;
    }

    public AddressRequestDTO(String street, String city, String zipCode, String phone, boolean isDefault, String recipientName, String type) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.phone = phone;
        this.isDefault = isDefault;
        this.recipientName = recipientName;
        this.type = type;
    }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
