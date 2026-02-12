package com.printer.myprinter.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    
    private Long quantity;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;  

    @Column (name = "created_By")
    private String createdBy;

    @Column(name="is_verified")
    private Boolean isVerified;

    @Column(name="verified_by")
    private String verifiedBy;

    @Column(name="verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name="created_at")
    private LocalDateTime createdAt;
    
    public OrderEntity() {}
    
    public OrderEntity(LocalDate orderDate, String lotNumber, String productId, 
                      String productName, String productExp, LocalDate productionDate, 
                      LocalDate expiryDate, Long quantity, String notes, String createdBy,
                      Boolean isVerified, String verifiedBy, LocalDateTime verifiedAt,LocalDateTime createdAt) {
        this.orderDate = orderDate;
        this.lotNumber = lotNumber;
        this.productId = productId;
        this.productName = productName;
        this.productExp = productExp;
        this.productionDate = productionDate;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.notes = notes;
        this.createdBy = createdBy;
        this.isVerified = isVerified;
        this.verifiedBy  = verifiedBy;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
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
    
    public Long getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getNotes(){
        return notes;
    }

    public void setNotes(String notes){
        this.notes = notes;
    }

    public String getCreatedBy(){
        return createdBy;
    }

    public void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public String getVerifiedBy() {
        return verifiedBy;
    }
    
    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }
    
    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }
    
    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }

    
}