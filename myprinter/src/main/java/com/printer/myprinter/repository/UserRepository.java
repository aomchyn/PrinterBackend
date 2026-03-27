package com.printer.myprinter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.printer.myprinter.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
 
    UserEntity findByNameAndEmail (String name, String email);
    UserEntity findByName(String name);
}
