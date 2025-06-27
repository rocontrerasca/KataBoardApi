package com.kataboard.controllers;

import com.kataboard.dtos.user.UpdateUserRolesRequest;
import com.kataboard.dtos.user.UpdateUserStatusRequest;
import com.kataboard.dtos.user.UserProfileDto;
import com.kataboard.security.JwtUtil;
import com.kataboard.services.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateUserStatusRequest request) {

        userService.updateStatus(id, request.getActive());
        return ResponseEntity.ok("Usuario " + (request.getActive() ? "activado" : "desactivado"));
    }

    @GetMapping("/emails/distinct")
    public ResponseEntity<List<String>> getAllEmailsDistinct(HttpServletRequest request) {
        String currentEmail = jwtUtil.extractUsername(jwtUtil.resolveToken(request));
        return ResponseEntity.ok(userService.getAllUserEmailsExcept(currentEmail));
    }

    @GetMapping("/emails")
    public ResponseEntity<List<String>> getAllEmails() {
        return ResponseEntity.ok(userService.getAllUserEmails());
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserRoles(
            @PathVariable Long id,
            @RequestBody UpdateUserRolesRequest request) {
        userService.updateRoles(id, request.getRoles());
        return ResponseEntity.ok("Roles actualizados para el usuario con ID " + id);
    }
}
