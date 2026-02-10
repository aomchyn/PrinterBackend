package com.printer.myprinter.repository;

import com.printer.myprinter.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // หา order ตามวันที่
    List<OrderEntity> findByOrderDate(LocalDate orderDate);
    
    // หา order ตาม product id
    List<OrderEntity> findByProductId(String productId);
    
    // หา order ที่หมดอายุแล้ว
    List<OrderEntity> findByExpiryDateBefore(LocalDate date);
    
    // หา order ที่ยังไม่หมดอายุ
    List<OrderEntity> findByExpiryDateAfter(LocalDate date);
}