package com.example.Assurance.Controlleur;

import com.example.Assurance.Entity.AuthRequest;
import com.example.Assurance.Entity.ResetPasswordRequest;
import com.example.Assurance.Entity.User;
import com.example.Assurance.Entity.Role;
import com.example.Assurance.Repository.UserRepository;
import com.example.Assurance.Service.AuthService;
import com.example.Assurance.Service.QRCodeGenerator;
import com.example.Assurance.Service.TokenService;
import com.example.Assurance.Service.UserService;
import com.example.Assurance.Service.JwtUtil;
import com.example.Assurance.Service.EmailService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

  private final AuthService authService;
  private final TokenService tokenService;
  private final JavaMailSender mailSender;
  private final UserService userService;
  private final QRCodeGenerator qrCodeGenerator;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

  // Constructor Injection for all dependencies
  @Autowired
  public AuthController(AuthService authService,
                        TokenService tokenService,
                        JavaMailSender mailSender,
                        UserService userService,
                        QRCodeGenerator qrCodeGenerator,
                        JwtUtil jwtUtil,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        EmailService emailService) {
    this.authService = authService;
    this.tokenService = tokenService;
    this.mailSender = mailSender;
    this.userService = userService;
    this.qrCodeGenerator = qrCodeGenerator;
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
  }
/*
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }*/
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody AuthRequest request) {
  System.out.println("=== Début de la tentative de connexion ===");
  System.out.println("Username: " + request.username());
  System.out.println("Code 2FA reçu: " + (request.twoFactorCode() != null ? request.twoFactorCode() : "non fourni"));

  // Vérifier si l'utilisateur existe et si la 2FA est activée
  User user = userService.findUserByUsername(request.username());
  if (user == null) {
    System.out.println("Utilisateur non trouvé: " + request.username());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
      .body(Map.of("error", "Identifiants incorrects"));
  }

  System.out.println("Utilisateur trouvé. 2FA activée: " + user.isIs2fa_enabled());
  System.out.println("Secret 2FA: " + (user.getTwoFactorSecret() != null ? "présent" : "absent"));

  // Si la 2FA est activée, le code 2FA est requis
  if (user.isIs2fa_enabled()) {
    if (request.twoFactorCode() == null || request.twoFactorCode().isEmpty()) {
      System.out.println("2FA activée mais code non fourni");
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(Map.of("error", "Code 2FA requis", "requires2FA", true));
    }

    boolean is2FAVerified = verify2FA(request.username(), request.twoFactorCode());
    System.out.println("Résultat de la vérification 2FA: " + is2FAVerified);

    if (!is2FAVerified) {
      // Incrémenter le compteur de tentatives échouées
      user.setFailed2faattempts(user.getFailed2faattempts() + 1);
      userRepository.save(user);
      System.out.println("Code 2FA invalide. Tentatives échouées: " + user.getFailed2faattempts());
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(Map.of("error", "Code 2FA invalide", "requires2FA", true));
    }
  }

  // Vérification des informations de connexion de l'utilisateur
  String token = authService.login(request);
  if (token == null) {
    System.out.println("Échec de l'authentification");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
      .body(Map.of("error", "Identifiants incorrects"));
  }

  System.out.println("Connexion réussie");
  return ResponseEntity.ok(Map.of("token", token));
}
/*
  public boolean verify2FA(String username, String code) {
    String secret = userService.get2FASecret(username);
    if (secret == null) {
      return false;  // Secret 2FA non trouvé
    }

    GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    // Vérifie le code dans une fenêtre de +30 et -30 secondes
    boolean isCodeValid = googleAuthenticator.authorize(secret, Integer.parseInt(code));
    if (!isCodeValid) {
      // Vérifier le code dans une fenêtre de +/- 30 secondes
      int previousCode = googleAuthenticator.getTotpPassword(secret, System.currentTimeMillis() - 30000);
      int nextCode = googleAuthenticator.getTotpPassword(secret, System.currentTimeMillis() + 30000);
      return code.equals(Integer.toString(previousCode)) || code.equals(Integer.toString(nextCode));
    }
    return isCodeValid;
  }

*/

  private boolean verify2FA(String username, String code) {
    String secret = userService.get2FASecret(username);

    if (secret == null) {
      System.out.println("Erreur : Secret 2FA introuvable pour l'utilisateur " + username);
      return false;
    }

    GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    // 🔍 DEBUG: Afficher les codes TOTP valides actuellement
    int currentCode = googleAuthenticator.getTotpPassword(secret);
    int prevCode = googleAuthenticator.getTotpPassword(secret, -1);
    int nextCode = googleAuthenticator.getTotpPassword(secret, 1);

    System.out.println("=====================================");
    System.out.println("Code entré par l'utilisateur : " + code);
    System.out.println("Code actuel généré par le serveur : " + currentCode);
    System.out.println("Code précédent (-30s) : " + prevCode);
    System.out.println("Code suivant (+30s) : " + nextCode);
    System.out.println("=====================================");

    // Vérifier avec une tolérance de -30s et +30s
    boolean isValid = code.equals(String.valueOf(currentCode)) ||
      code.equals(String.valueOf(prevCode)) ||
      code.equals(String.valueOf(nextCode));

    System.out.println("Résultat de la vérification 2FA : " + isValid);
    return isValid;
  }



  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody AuthRequest request) {
    System.out.println("=== Début de la requête d'inscription ===");
    System.out.println("Username: " + request.username());
    System.out.println("Email: " + request.email());
    System.out.println("Role: " + request.role());
    System.out.println("Password: " + (request.password() != null ? "[PRESENT]" : "[NULL]"));
    System.out.println("TwoFactorCode: " + request.twoFactorCode());
    System.out.println("=====================================");

    try {
        // Validation des champs
        if (request.username() == null || request.username().trim().isEmpty()) {
            System.out.println("Erreur: Le nom d'utilisateur est requis");
            return ResponseEntity.badRequest().body(Map.of("error", "Le nom d'utilisateur est requis"));
        }
        if (request.password() == null || request.password().trim().isEmpty()) {
            System.out.println("Erreur: Le mot de passe est requis");
            return ResponseEntity.badRequest().body(Map.of("error", "Le mot de passe est requis"));
        }
        if (request.email() == null || request.email().trim().isEmpty()) {
            System.out.println("Erreur: L'email est requis");
            return ResponseEntity.badRequest().body(Map.of("error", "L'email est requis"));
        }
        if (request.role() == null) {
            System.out.println("Erreur: Le rôle est requis");
            return ResponseEntity.badRequest().body(Map.of("error", "Le rôle est requis"));
        }

        // Validation du rôle
        try {
            Role.valueOf(request.role().toString().toUpperCase());
            System.out.println("Rôle validé: " + request.role());
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur: Rôle invalide: " + request.role());
            return ResponseEntity.badRequest().body(Map.of("error", "Le rôle doit être l'une des valeurs suivantes: ADMIN, AGENT, CLIENT_PHYSIQUE, CLIENT_MORAL"));
        }

        String token = authService.register(request);
        System.out.println("Inscription réussie");
        return ResponseEntity.ok(Map.of("token", token));
    } catch (IllegalArgumentException e) {
        System.err.println("Erreur de validation: " + e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        System.err.println("Erreur lors de l'inscription: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Erreur lors de l'inscription: " + e.getMessage()));
    }
  }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody String email) {
        String token = UUID.randomUUID().toString();
        System.out.println("hedha"+email);// Générer un token unique
        authService.createPasswordResetToken(email, token);  // Créer et enregistrer le token pour l'utilisateur

        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Réinitialisation de votre mot de passe");
        message.setText("Cliquez sur le lien suivant pour réinitialiser votre mot de passe : " + resetLink);

        mailSender.send(message);

        return "Un e-mail avec le lien de réinitialisation a été envoyé.";
    }

@PostMapping("/reset-password")
public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest request) {
  if (tokenService.isValidToken(request.getToken())) {
    authService.updatePassword(request.getToken(), request.getPassword());
    tokenService.invalidateToken(request.getToken());
    return ResponseEntity.ok(new MessageResponse("Le mot de passe a été réinitialisé avec succès."));
  } else {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Token invalide ou expiré."));
  }
}
  @PostMapping("/double/{username}")
  public ResponseEntity<Map<String, String>> generate2FA(@PathVariable String username) throws IOException, WriterException {
    // Vérifie si l'utilisateur existe
    var user = userService.findUserByUsername(username);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Collections.singletonMap("error", "Utilisateur non trouvé."));
    }

    // Génère le secret et l'URL du QR Code
    String secret = userService.generateAndSaveSecretForUser(username);
    String issuer = "MyApp";
    String userEmail = user.getEmail();
    String qrUrl = "otpauth://totp/" + issuer + ":" + userEmail + "?secret=" + secret + "&issuer=" + issuer;

    // 🔥 Retourner un JSON valide au lieu d'une chaîne brute
    Map<String, String> response = new HashMap<>();
    response.put("qrCodeUrl", qrUrl);

    return ResponseEntity.ok(response);
  }

  public class MessageResponse {
    private String message;

    // constructor, getter, setter
    public MessageResponse(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }
  @GetMapping("/username/{username}")
  public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
    try {
      User user = userService.findUserByUsername(username);
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    Cookie cookie = new Cookie("JWT", null);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);  // Assurez-vous d'activer ce paramètre en production
    cookie.setPath("/");
    cookie.setMaxAge(0);  // Supprimer le cookie en définissant son âge à 0
    response.addCookie(cookie);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/enable-2fa")
  public ResponseEntity<?> enable2FA(@RequestBody Map<String, String> request) {
    try {
        String username = request.get("username");
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Le nom d'utilisateur est requis"));
        }

        User user = userService.findUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Utilisateur non trouvé"));
        }

        if (user.isIs2fa_enabled()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "La 2FA est déjà activée pour cet utilisateur"));
        }

        // Générer une nouvelle clé secrète
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        user.setTwoFactorSecret(key.getKey());
        user.setIs2fa_enabled(true);
        userRepository.save(user);

        try {
            // Envoyer un email de confirmation
            emailService.send2FAEnabledEmail(user.getEmail(), user.getUsername());
        } catch (Exception e) {
            // Si l'envoi d'email échoue, on désactive la 2FA
            user.setIs2fa_enabled(false);
            user.setTwoFactorSecret(null);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur lors de l'envoi de l'email de confirmation. La 2FA n'a pas été activée."));
        }

        // Générer l'URL QR
        String qrCodeUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL(
            "AssuranceMaghrebia",
            user.getEmail(),
            key
        );

        return ResponseEntity.ok(Map.of(
            "secret", key.getKey(),
            "qrCodeUrl", qrCodeUrl,
            "message", "2FA activée avec succès. Veuillez configurer votre application d'authentification."
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Erreur lors de l'activation de la 2FA : " + e.getMessage()));
    }
  }

  @PostMapping("/verify-2fa")
  public ResponseEntity<?> verify2FA(@RequestBody Map<String, String> request) {
    try {
      String code = request.get("code");
      String username = request.get("username");
      
      if (code == null || code.trim().isEmpty()) {
        return ResponseEntity.badRequest()
          .body(Map.of("error", "Le code de vérification est requis"));
      }
      
      if (username == null || username.trim().isEmpty()) {
        return ResponseEntity.badRequest()
          .body(Map.of("error", "Le nom d'utilisateur est requis"));
      }

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



