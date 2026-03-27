package com.printer.myprinter.controller;

import com.printer.myprinter.annotation.RequireAuth;
import com.printer.myprinter.dto.LoginRequestDTO;
import com.printer.myprinter.dto.LoginResponseDTO;
import com.printer.myprinter.dto.UserRequestDTO;
import com.printer.myprinter.dto.UserResponseDTO;
import com.printer.myprinter.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/printer/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDTO> signin(
            @Valid @RequestBody LoginRequestDTO request,
            @RequestParam(defaultValue = "signin") String loginType) {
        
        LoginResponseDTO response = userService.authenticate(request, loginType);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @RequireAuth(roles = { "admin" })
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @RequireAuth(roles = { "admin" })
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("/{id:[0-9]+}")
    @RequireAuth
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable Long id,
            @RequestAttribute("userId") String requestUserId,
            @RequestAttribute("userRole") String requestUserRole) {

        if (!"admin".equals(requestUserRole) && !id.toString().equals(requestUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @RequireAuth(roles = { "admin" })
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @RequireAuth(roles = { "admin" })
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "ลบสำเร็จ");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @RequireAuth
    public ResponseEntity<UserResponseDTO> getMe(@RequestAttribute("userId") String requestUserId) {
        return ResponseEntity.ok(userService.getUserById(Long.valueOf(requestUserId)));
    }

    @PutMapping("/me")
    @RequireAuth
    public ResponseEntity<UserResponseDTO> updateMe(
            @RequestAttribute("userId") String requestUserId,
            @Valid @RequestBody UserRequestDTO request) {

        // Prevent privilege escalation by ensuring the role is preserved
        UserResponseDTO currentUser = userService.getUserById(Long.valueOf(requestUserId));
        request.setRole(currentUser.getRole());

        return ResponseEntity.ok(userService.updateUser(Long.valueOf(requestUserId), request));
    }
}
