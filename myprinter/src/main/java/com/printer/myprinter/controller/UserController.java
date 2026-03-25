package com.printer.myprinter.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.printer.myprinter.annotation.RequireAuth;
import com.printer.myprinter.entity.UserEntity;
import com.printer.myprinter.repository.UserRepository;

import io.github.cdimascio.dotenv.Dotenv;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/printer/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    private static final long EXPIRATION_TIME = 60 * 60 * 1000 * 24 * 7;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ ต้องมี auth + admin เท่านั้น
    @GetMapping
    @RequireAuth(roles = { "admin" })
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ สร้าง user ต้อง admin (ใช้ admin-create แทน)
    @PostMapping
    @RequireAuth(roles = { "admin" })
    public UserEntity createUser(@Valid @RequestBody UserEntity user) {
        // Hash password ก่อน save
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }
        return userRepository.save(user);
    }

    @GetMapping("/{id}")
    @RequireAuth
    public UserEntity getUserById(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    @RequireAuth(roles = { "admin" })
    public UserEntity updateUser(@PathVariable Long id, @Valid @RequestBody UserEntity user) {
        UserEntity userToUpdate = userRepository.findById(id).orElse(null);

        if (userToUpdate == null) {
            throw new IllegalArgumentException("Not found");
        }

        userToUpdate.setName(user.getName());
        userToUpdate.setEmail(user.getEmail());

        return userRepository.save(userToUpdate);
    }

    @DeleteMapping("/{id}")
    @RequireAuth(roles = { "admin" })
    public void dropUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody UserEntity user) {
        String name = user.getName();
        String email = user.getEmail();

        UserEntity userToSignin = userRepository.findByNameAndEmail(name, email);

        if (userToSignin == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        return ResponseEntity.ok(userToSignin);
    }

    private String getSecret() {
        return com.printer.myprinter.WebConfig.getSecret();
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(getSecret());
    }

    @PostMapping("/admin-signin")
    public ResponseEntity<?> adminSignin(@RequestBody UserEntity user) {
        try {
            String u = user.getName();
            String p = user.getPassword();

            // ค้นหา user จาก name ก่อน (ไม่ใช้ password ตรงๆ)
            List<UserEntity> users = userRepository.findAll();
            UserEntity userForCreateToken = null;

            for (UserEntity dbUser : users) {
                if (dbUser.getName() != null && dbUser.getName().equals(u)) {
                    // ตรวจสอบ password ด้วย BCrypt
                    if (dbUser.getPassword() != null) {
                        try {
                            // ลอง BCrypt check ก่อน
                            if (BCrypt.checkpw(p, dbUser.getPassword())) {
                                userForCreateToken = dbUser;
                                break;
                            }
                        } catch (IllegalArgumentException e) {
                            // ถ้า password ใน DB ไม่ใช่ BCrypt format → ลอง plaintext match
                            // (migration strategy สำหรับ password เก่าที่ยังไม่ได้ hash)
                            if (dbUser.getPassword().equals(p)) {
                                // พบ plaintext match → hash แล้ว save ใหม่
                                log.info("Migrating plaintext password to BCrypt for user: {}", u);
                                dbUser.setPassword(BCrypt.hashpw(p, BCrypt.gensalt()));
                                userRepository.save(dbUser);
                                userForCreateToken = dbUser;
                                break;
                            }
                        }
                    }
                }
            }

            if (userForCreateToken == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // ✅ เพิ่ม role claim ใน JWT
            String token = JWT.create()
                    .withSubject(String.valueOf(userForCreateToken.getId()))
                    .withClaim("role", userForCreateToken.getRole())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .withIssuedAt(new Date())
                    .sign(getAlgorithm());

            String role = userForCreateToken.getRole();

            record UserResponse(String token, String role) {
            }
            return ResponseEntity.ok(new UserResponse(token, role));
        } catch (Exception e) {
            log.error("Admin signin error", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Login failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/admin-info")
    @RequireAuth
    public ResponseEntity<?> adminInfo(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Invalid Token Format with 'Bearer '");
            }

            String tokenWithoutBearer = token.replace("Bearer ", "");
            if (tokenWithoutBearer.trim().isEmpty()) {
                throw new IllegalArgumentException("Token is Empty");
            }

            String subject = JWT.require(getAlgorithm())
                    .build()
                    .verify(tokenWithoutBearer)
                    .getSubject();

            Long userId = Long.valueOf(subject);
            UserEntity dbUser = userRepository.findById(userId).orElse(null);

            if (dbUser == null) {
                throw new IllegalArgumentException("User not found");
            }

            String role = dbUser.getRole();

            record UserResponse(Long id, String name, String email, String role) {
            }

            return ResponseEntity.ok(new UserResponse(dbUser.getId(), dbUser.getName(), dbUser.getEmail(), role));

        } catch (Exception e) {
            log.error("Admin info error", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    private Long getUserIdFromToken(String token) {
        String tokenWithoutBearer = token.replace("Bearer ", "");

        if (tokenWithoutBearer.trim().isEmpty()) {
            throw new IllegalArgumentException("Token is null");
        }

        return Long.valueOf(JWT.require(getAlgorithm())
                .build()
                .verify(tokenWithoutBearer)
                .getSubject());
    }

    @PostMapping("/admin-edit-profile")
    @RequireAuth
    public ResponseEntity<?> adminEditProfile(@RequestHeader("Authorization") String token,
            @RequestBody UserEntity user) {
        Long userId = getUserIdFromToken(token);
        UserEntity userToupdate = userRepository.findById(userId).orElse(null);

        if (userToupdate == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // ✅ Hash password ก่อน save
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            userToupdate.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(userToupdate);
        return ResponseEntity.ok(userToupdate);
    }

    @PostMapping("/admin-create")
    @RequireAuth(roles = { "admin" })
    public ResponseEntity<?> adminCreate(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserEntity user) {
        try {
            // ✅ Hash password ก่อน save
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            }
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            log.error("Admin create user error", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to create user");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/admin-delete/{id}")
    @RequireAuth(roles = { "admin" })
    public ResponseEntity<?> adminDelete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            UserEntity userToDelete = userRepository.findById(id).orElse(null);

            if (userToDelete == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            userRepository.deleteById(id);

            Map<String, String> success = new HashMap<>();
            success.put("message", "ลบสำเร็จ");
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            log.error("Admin delete user error", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to delete user");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/admin-update-profile")
    @RequireAuth(roles = { "admin" })
    public ResponseEntity<?> adminUpdateProfile(@RequestHeader("Authorization") String token,
            @Valid @RequestBody UserEntity user) {
        try {
            UserEntity usertoupdate = userRepository.findById(user.getId()).orElse(null);
            if (usertoupdate == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            usertoupdate.setName(user.getName());
            usertoupdate.setEmail(user.getEmail());

            // ✅ Hash password ก่อน save
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                usertoupdate.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            }

            usertoupdate.setRole(user.getRole());

            userRepository.save(usertoupdate);
            return ResponseEntity.ok(usertoupdate);

        } catch (Exception e) {
            log.error("Admin update profile error", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to update user");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
