package com.printer.myprinter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fgcode") // ชื่อตารางในฐานข้อมูล
public class FgcodeEntity {
    
    @Id
    private String id;
    private String name;
    private String exp;
    
    // Constructor
    public FgcodeEntity() {}
    
    public FgcodeEntity(String id, String name, String exp) {
        this.id = id;
        this.name = name;
        this.exp = exp;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getExp() {
        return exp;
    }
    
    public void setExp(String exp) {
        this.exp = exp;
    }
    
}