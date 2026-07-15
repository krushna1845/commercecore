package com.krushna.commercecore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address must be less than 255 characters")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be less than 100 characters")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Zip code is required")
    @Size(max = 20, message = "Zip code must be less than 20 characters")
    @Pattern(regexp = "^[0-9]{5,6}(-[0-9]{4})?$", message = "Invalid zip code format")
    @Column(nullable = false)
    private String zipCode;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[1-9][0-9]{9,14}$", message = "Invalid phone number format")
    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private boolean isDefault = false;

    private String recipientName;

    @Column(nullable = false)
    private String type = "SHIPPING"; // SHIPPING, BILLING

    public Address() {}

    public Address(User user, String street, String city, String zipCode, String phone) {
        this.user = user;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.phone = phone;
    }

    public Address(User user, String street, String city, String zipCode, String phone, String recipientName, String type) {
        this.user = user;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.phone = phone;
        this.recipientName = recipientName;
        this.type = type != null ? type : "SHIPPING";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    @Override
    public String toString() {
        return street + ", " + city + ", " + zipCode;
    }
}
