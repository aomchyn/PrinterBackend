package com.printer.myprinter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.printer.myprinter.entity.FgcodeEntity;

public interface FgcodeRepositoiry extends JpaRepository<FgcodeEntity, String>{
    FgcodeEntity findByid (String id);
    
}