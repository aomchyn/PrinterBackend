package com.printer.myprinter.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.printer.myprinter.entity.FgcodeEntity;
import com.printer.myprinter.repository.FgcodeRepository;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/fgcode")
@CrossOrigin(origins = "*")
public class FgcodeController {
    
    private final FgcodeRepository fgcodeRepository;
    
    public FgcodeController(FgcodeRepository fgcodeRepository){
        this.fgcodeRepository = fgcodeRepository;
    }
    
    // ✅ GET ทั้งหมด
    @GetMapping
    public ResponseEntity<List<FgcodeEntity>> getAllFgcode(){
        try {
            List<FgcodeEntity> fgcodes = fgcodeRepository.findAll();
            return ResponseEntity.ok(fgcodes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✅ GET ตาม ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id){
        try {
            Optional<FgcodeEntity> fgcode = fgcodeRepository.findById(id);
            
            if (fgcode.isPresent()) {
                return ResponseEntity.ok(fgcode.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "ไม่พบรหัสสินค้า");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✅ สร้างใหม่
    @PostMapping("/create")
    public ResponseEntity<?> fgcodeCreate(@RequestBody FgcodeEntity fgcode){
        try {
            System.out.println("Creating fgcode: " + fgcode.getId()); // Debug
            
            // ตรวจสอบว่ามี ID ซ้ำหรือไม่
            if (fgcodeRepository.existsById(fgcode.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "รหัสสินค้า " + fgcode.getId() + " มีอยู่แล้ว");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            FgcodeEntity saved = fgcodeRepository.save(fgcode);
            System.out.println("Created successfully: " + saved.getId()); // Debug
            
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "ไม่สามารถสร้างรหัสสินค้าได้: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // ✅ แก้ไข - ใช้ Optional แทน orElseThrow
    @PostMapping("/update-profile")
    public ResponseEntity<?> fgcodeUpdate(@RequestBody FgcodeEntity fgcode){
        try {
            System.out.println("Updating fgcode: " + fgcode.getId()); // Debug
            
            // ✅ ใช้ Optional แทน orElseThrow
            Optional<FgcodeEntity> existingFgcode = fgcodeRepository.findById(fgcode.getId());
            
            if (!existingFgcode.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "ไม่พบรหัสสินค้า " + fgcode.getId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            FgcodeEntity fgcodeToUpdate = existingFgcode.get();
            
            // อัพเดทข้อมูล
            fgcodeToUpdate.setName(fgcode.getName());
            fgcodeToUpdate.setExp(fgcode.getExp());
            
            FgcodeEntity updated = fgcodeRepository.save(fgcodeToUpdate);
            
            System.out.println("Updated successfully: " + updated.getId()); // Debug
            return ResponseEntity.ok(updated);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "ไม่สามารถแก้ไขรหัสสินค้าได้: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // ✅ แก้ไขแบบใช้ PUT method (ทางเลือก)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFgcode(@PathVariable String id, @RequestBody FgcodeEntity fgcode){
        try {
            Optional<FgcodeEntity> existingFgcode = fgcodeRepository.findById(id);
            
            if (!existingFgcode.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "ไม่พบรหัสสินค้า");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            FgcodeEntity fgcodeToUpdate = existingFgcode.get();
            fgcodeToUpdate.setName(fgcode.getName());
            fgcodeToUpdate.setExp(fgcode.getExp());
            
            FgcodeEntity updated = fgcodeRepository.save(fgcodeToUpdate);
            return ResponseEntity.ok(updated);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "ไม่สามารถแก้ไขรหัสสินค้าได้");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // ✅ ลบ
    @DeleteMapping("/{id}")
    public ResponseEntity<?> dropFgcode(@PathVariable String id){
        try {
            if (!fgcodeRepository.existsById(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "ไม่พบรหัสสินค้า");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            fgcodeRepository.deleteById(id);
            
            Map<String, String> success = new HashMap<>();
            success.put("message", "ลบสำเร็จ");
            return ResponseEntity.ok(success);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "ไม่สามารถลบรหัสสินค้าได้");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}