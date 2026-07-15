package com.krushna.commercecore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "delivery_slots")
public class DeliverySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String slotDate;

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;

    @Column(nullable = false)
    private boolean available;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int bookedCount;

    @Column(nullable = false)
    private double price;

    @PrePersist
    @PreUpdate
    protected void updateAvailability() {
        this.available = this.bookedCount < this.capacity;
    }

    public DeliverySlot() {}

    public DeliverySlot(String slotDate, String startTime, String endTime, int capacity, double price) {
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.price = price;
        this.bookedCount = 0;
        this.available = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlotDate() { return slotDate; }
    public void setSlotDate(String slotDate) { this.slotDate = slotDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getBookedCount() { return bookedCount; }
    public void setBookedCount(int bookedCount) { this.bookedCount = bookedCount; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
