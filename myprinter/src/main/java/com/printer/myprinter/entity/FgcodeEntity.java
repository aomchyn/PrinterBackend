package com.printer.myprinter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "fgcode")
public class FgcodeEntity {

    @Id
    @NotBlank(message = "FG Code ID is required")
    @Size(max = 50)
    private String id;

    @NotBlank(message = "Name is required")
    @Size(max = 200)
    private String name;

    @Size(max = 100)
    private String exp;

    // Constructor
    public FgcodeEntity() {
    }

    public FgcodeEntity(String id, String name, String exp) {
        this.id = id;
        this.name = name;
        this.exp = exp;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getExp() { return exp; }
    public void setExp(String exp) { this.exp = exp; }
}