package com.example.Assurance.Controlleur;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @PostMapping("/verify-2fa")
  public ResponseEntity<?> verify2FA(@RequestBody Map<String, String> request) {
    try {
      String code = request.get("code");
      if (code == null || code.trim().isEmpty()) {
        return ResponseEntity.badRequest()
          .body(Map.of("error", "Le code de vérification est requis"));
      }

      // Get the current user from the security context
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Utilisateur non authentifié"));
      }

      String username = authentication.getName();
      boolean isValid = verify2FA(username, code);

      if (isValid) {
        // Reset failed attempts on successful verification
        User user = userService.findUserByUsername(username);
        user.setFailed2faattempts(0);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
          "message", "Code 2FA vérifié avec succès",
          "verified", true
        ));
      } else {
        // Increment failed attempts
        User user = userService.findUserByUsername(username);
        user.setFailed2faattempts(user.getFailed2faattempts() + 1);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(Map.of(
            "error", "Code 2FA invalide",
            "verified", false
          ));
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Erreur lors de la vérification du code 2FA : " + e.getMessage()));
    }
  }
} 