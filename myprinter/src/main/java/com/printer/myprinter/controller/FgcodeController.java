package com.printer.myprinter.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.printer.myprinter.annotation.RequireAuth;
import com.printer.myprinter.entity.FgcodeEntity;
import com.printer.myprinter.repository.FgcodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/fgcode")
@RequireAuth // ✅ ทุก endpoint ต้อง login
public class FgcodeController {

    private static final Logger log = LoggerFactory.getLogger(FgcodeController.class);
    private final FgcodeRepository fgcodeRepository;

    public FgcodeController(FgcodeRepository fgcodeRepository) {
        this.fgcodeRepository = fgcodeRepository;
    }

    // ✅ GET ทั้งหมด
    @GetMapping
    public ResponseEntity<List<FgcodeEntity>> getAllFgcode() {
        try {
            List<FgcodeEntity> fgcodes = fgcodeRepository.findAll();
            return ResponseEntity.ok(fgcodes);
        } catch (Exception e) {
            log.error("Error fetching all fgcodes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ GET ตาม ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
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
            log.error("Error fetching fgcode by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ สร้างใหม่
    @PostMapping("/create")
    @RequireAuth(roles = { "admin" })
    public ResponseEntity<?> fgcodeCreate(@Valid @RequestBody FgcodeEntity fgcode) {
        try {
            // ตรวจสอบว่ามี ID ซ้ำหรือไม่
            if (fgcodeRepository.existsById(fgcode.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "รหัสสินค้า " + fgcode.getId() + " มีอยู่แล้ว");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            FgcodeEntity saved = fgcodeRepository.save(fgcode);

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            log.error("Error creating fgcode", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "ไม่สามารถสร้างรหัสสินค้าได้");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ✅ แก้ไข
    @PostMapping("/update-profile")
    @RequireAuth(roles = { "admin" })
    public ResponseEntity<?> fgcodeUpdate(@Valid @RequestBody FgcodeEntity fgcode) {
        try {
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

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            log.error("Error updating fgcode", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "ไม่สามารถแก้ไขรหัสสินค้าได้");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ✅ แก้ไขแบบ PUT method
    @PutMapping("/{id}")
    @RequireAuth(roles = { "admin" })
    public ResponseEntity<?> updateFgcode(@PathVariable String id, @Valid @RequestBody FgcodeEntity fgcode) {
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
            log.error("Error updating fgcode id: {}", id, e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "ไม่สามารถแก้ไขรหัสสินค้าได้");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ✅ ลบ
    @DeleteMapping("/{id}")
    @RequireAuth(roles = { "admin" })
    public ResponseEntity<?> dropFgcode(@PathVariable String id) {
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
            log.error("Error deleting fgcode id: {}", id, e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "ไม่สามารถลบรหัสสินค้าได้");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}