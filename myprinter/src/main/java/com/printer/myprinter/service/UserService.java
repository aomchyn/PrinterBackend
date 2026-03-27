package com.printer.myprinter.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.printer.myprinter.WebConfig;
import com.printer.myprinter.dto.LoginRequestDTO;
import com.printer.myprinter.dto.LoginResponseDTO;
import com.printer.myprinter.dto.UserRequestDTO;
import com.printer.myprinter.dto.UserResponseDTO;
import com.printer.myprinter.entity.UserEntity;
import com.printer.myprinter.exception.UserNotFoundException;
import com.printer.myprinter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;

import io.lettuce.core.dynamic.annotation.Value;
import jakarta.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RateLimitService rateLimitService;
    private final String jwtSecret;

    private static final long EXPIRATION_TIME = 60 * 60 * 1000 * 2; // 2 hours

    public UserService(UserRepository userRepository, RateLimitService rateLimitService, @Value("${JWT_SECRET}") String jwtSecret) {
        this.userRepository = userRepository;
        this.rateLimitService = rateLimitService;
        this.jwtSecret = jwtSecret;
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return mapToDTO(user);
    }

    public UserResponseDTO createUser(UserRequestDTO request) {
        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        UserEntity savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

     @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        
        // Ensure role updates are handled if provided
        if (request.getRole() != null ) {
            user.setRole(request.getRole());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        UserEntity updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public LoginResponseDTO authenticate(LoginRequestDTO request, String ipKeyPrefix) {
        String username = request.getName();
        String rateLimitKey = ipKeyPrefix + ":" + username;

        if (!rateLimitService.isAllowed(rateLimitKey)) {
            long retryAfter = rateLimitService.getRemainingSeconds(rateLimitKey);
            throw new IllegalArgumentException("Too many login attempts. Please try again later. Retry after " + retryAfter + " seconds.");
        }

        UserEntity dbUser = userRepository.findByName(username);
        if (dbUser == null || dbUser.getPassword() == null) {
            throw new IllegalArgumentException("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
        }

        try {
    if (!BCrypt.checkpw(request.getPassword(), dbUser.getPassword())) {
        throw new IllegalArgumentException("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
    }
} catch (IllegalArgumentException e) {
    // BCrypt โยน exception = password ที่เก็บไว้ไม่ใช่ bcrypt hash (plaintext เก่า)
    if (dbUser.getPassword() != null && 
        dbUser.getPassword().equals(request.getPassword())) {
        // Migrate plaintext → bcrypt
        dbUser.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        userRepository.save(dbUser);
        // ✅ ไม่ throw — login สำเร็จ ดำเนินต่อได้เลย
    } else {
        throw new IllegalArgumentException("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
    }
}

        rateLimitService.resetKey(rateLimitKey);

        String token = JWT.create()
        .withSubject(String.valueOf(dbUser.getId()))
        .withClaim("role", dbUser.getRole().name()) // ✅ .name() คืนค่า "ADMIN", "USER", "VIEWER"
        .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .withIssuedAt(new Date())
        .sign(Algorithm.HMAC256(jwtSecret));

        return new LoginResponseDTO(token, dbUser.getRole());
    }

    private UserResponseDTO mapToDTO(UserEntity user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
