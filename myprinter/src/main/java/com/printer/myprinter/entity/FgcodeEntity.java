package com.printer.myprinter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class FgcodeEntity{

    @Id
    private String id;
    
    private String name;
    private String exp;

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getExp(){
        return exp;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setExp(String exp){
        this.exp = exp;
    }
    
}