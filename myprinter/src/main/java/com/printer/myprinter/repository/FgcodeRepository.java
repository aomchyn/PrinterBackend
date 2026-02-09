package com.printer.myprinter.repository;

import com.printer.myprinter.entity.FgcodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ✅ ต้อง extend JpaRepository<FgcodeEntity, String>
// String คือ type ของ Primary Key (id)
@Repository
public interface FgcodeRepository extends JpaRepository<FgcodeEntity, String> {
    // ไม่ต้องเขียน method อะไรเพิ่ม
    // JpaRepository มี findById() ให้อยู่แล้ว
}