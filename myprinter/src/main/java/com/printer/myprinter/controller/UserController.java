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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

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
            HttpServletRequest httpRequest) {

        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(userService.authenticate(request, ip));
    }

    @GetMapping
    @RequireAuth(roles = {"ADMIN"}) // ✅ uppercase ตรงกับ enum
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @RequireAuth(roles = {"ADMIN"})
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("/{id:[0-9]+}")
    @RequireAuth
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable Long id,
            @RequestAttribute("userId") String requestUserId,
            @RequestAttribute("userRole") String requestUserRole) {

        if (!"ADMIN".equals(requestUserRole) && !id.toString().equals(requestUserId)) { // ✅ uppercase
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id:[0-9]+}")
    @RequireAuth(roles = {"ADMIN"})
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id:[0-9]+}")
    @RequireAuth(roles = {"ADMIN"})
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
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

        UserResponseDTO currentUser = userService.getUserById(Long.valueOf(requestUserId));
        request.setRole(currentUser.getRole());

        return ResponseEntity.ok(userService.updateUser(Long.valueOf(requestUserId), request));
    }
}