package com.printer.myprinter.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.printer.myprinter.entity.OrderEntity;
import com.printer.myprinter.repository.OrderRepository;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*")
public class OrderController {
    
    private final OrderRepository orderRepository;
    
    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    // ✅ GET ทั้งหมด
    @GetMapping
    public ResponseEntity<List<OrderEntity>> getAllOrders() {
        try {
            List<OrderEntity> orders = orderRepository.findAll();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✅ GET ตาม ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<OrderEntity> order = orderRepository.findById(id);
            
            if (order.isPresent()) {
                return ResponseEntity.ok(order.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "ไม่พบคำสั่งซื้อ");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✅ GET ตามวันที่
    @GetMapping("/date/{date}")
    public ResponseEntity<List<OrderEntity>> getByDate(@PathVariable String date) {
        try {
            LocalDate orderDate = LocalDate.parse(date);
            List<OrderEntity> orders = orderRepository.findByOrderDate(orderDate);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✅ สร้างใหม่
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderEntity order) {
        try {
            System.out.println("Creating order for product: " + order.getProductId());
            
            OrderEntity saved = orderRepository.save(order);
            System.out.println("Created successfully with ID: " + saved.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "ไม่สามารถสร้างคำสั่งซื้อได้: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // ✅ แก้ไข
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody OrderEntity order) {
        try {
            Optional<OrderEntity> existingOrder = orderRepository.findById(id);
            
            if (!existingOrder.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "ไม่พบคำสั่งซื้อ");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            OrderEntity orderToUpdate = existingOrder.get();
            orderToUpdate.setOrderDate(order.getOrderDate());
            orderToUpdate.setLotNumber(order.getLotNumber());
            orderToUpdate.setProductId(order.getProductId());
            orderToUpdate.setProductName(order.getProductName());
            orderToUpdate.setProductExp(order.getProductExp());
            orderToUpdate.setProductionDate(order.getProductionDate());
            orderToUpdate.setExpiryDate(order.getExpiryDate());
            orderToUpdate.setQuantity(order.getQuantity());
            orderToUpdate.setNotes(order.getNotes());
            orderToUpdate.setCreated_By(order.getCreated_By());
            
            OrderEntity updated = orderRepository.save(orderToUpdate);
            return ResponseEntity.ok(updated);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "ไม่สามารถแก้ไขคำสั่งซื้อได้");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // ✅ ลบ
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            if (!orderRepository.existsById(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "ไม่พบคำสั่งซื้อ");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            orderRepository.deleteById(id);
            
            Map<String, String> success = new HashMap<>();
            success.put("message", "ลบสำเร็จ");
            return ResponseEntity.ok(success);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "ไม่สามารถลบคำสั่งซื้อได้");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // ✅ หา order ที่หมดอายุแล้ว
    @GetMapping("/expired")
    public ResponseEntity<List<OrderEntity>> getExpiredOrders() {
        try {
            LocalDate today = LocalDate.now();
            List<OrderEntity> orders = orderRepository.findByExpiryDateBefore(today);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✅ หา order ที่ยังไม่หมดอายุ
    @GetMapping("/active")
    public ResponseEntity<List<OrderEntity>> getActiveOrders() {
        try {
            LocalDate today = LocalDate.now();
            List<OrderEntity> orders = orderRepository.findByExpiryDateAfter(today);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}