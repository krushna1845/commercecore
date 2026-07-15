package com.krushna.commercecore.dto;

public class WarehouseDTO {
    private Long id;
    private String name;
    private String location;
    private String contactNumber;

    public WarehouseDTO() {}

    public WarehouseDTO(Long id, String name, String location, String contactNumber) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.contactNumber = contactNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}
