package com.krushna.commercecore.dto;

public class AddressResponseDTO {

    private Long id;
    private String street;
    private String city;
    private String zipCode;
    private String phone;
    private boolean isDefault;
    private String recipientName;
    private String type;

    public AddressResponseDTO() {}

    public AddressResponseDTO(Long id, String street, String city, String zipCode, 
                             String phone, boolean isDefault) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.phone = phone;
        this.isDefault = isDefault;
    }

    public AddressResponseDTO(Long id, String street, String city, String zipCode, 
                             String phone, boolean isDefault, String recipientName, String type) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.phone = phone;
        this.isDefault = isDefault;
        this.recipientName = recipientName;
        this.type = type;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getFullAddress() {
        return street + ", " + city + ", " + zipCode;
    }
}
