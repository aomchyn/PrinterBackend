package com.printer.myprinter.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class OrderEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_date")
    private LocalDate orderDate;
    
    @Column(name = "lot_number")
    private String lotNumber;
    
    @Column(name = "product_id")
    private String productId;
    
    @Column(name = "product_name")
    private String productName;
    
    @Column(name = "product_exp")
    private String productExp;
    
    @Column(name = "production_date")
    private LocalDate productionDate;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    private Integer quantity;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;  
    
    
    public OrderEntity() {}
    
    public OrderEntity(LocalDate orderDate, String lotNumber, String productId, 
                      String productName, String productExp, LocalDate productionDate, 
                      LocalDate expiryDate, Integer quantity, String notes) {
        this.orderDate = orderDate;
        this.lotNumber = lotNumber;
        this.productId = productId;
        this.productName = productName;
        this.productExp = productExp;
        this.productionDate = productionDate;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.notes = notes;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getLotNumber() {
        return lotNumber;
    }
    
    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductExp() {
        return productExp;
    }
    
    public void setProductExp(String productExp) {
        this.productExp = productExp;
    }
    
    public LocalDate getProductionDate() {
        return productionDate;
    }
    
    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNotes(){
        return notes;
    }

    public void setNotes(String notes){
        this.notes = notes;
    }

    
}