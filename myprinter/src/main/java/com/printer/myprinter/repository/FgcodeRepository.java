package com.printer.myprinter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.printer.myprinter.entity.FgcodeEntity;

public interface FgcodeRepository extends JpaRepository<FgcodeEntity, String>{
    FgcodeEntity findById (String id);
    
}