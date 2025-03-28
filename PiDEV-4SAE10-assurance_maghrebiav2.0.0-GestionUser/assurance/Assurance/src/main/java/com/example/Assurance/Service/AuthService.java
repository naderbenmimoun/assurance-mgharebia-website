package com.example.Assurance.Service;


import com.example.Assurance.Entity.AuthRequest;
import com.example.Assurance.Entity.Role;
import com.example.Assurance.Entity.User;
import com.example.Assurance.Repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                       UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public String login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails user = userDetailsService.loadUserByUsername(request.username());
        return jwtUtil.generateToken(user.getUsername());
    }

  public String register(AuthRequest request) {
    System.out.println("=== Début de l'inscription ===");
    System.out.println("Username: " + request.username());
    System.out.println("Email: " + request.email());
    System.out.println("Role: " + request.role());
    System.out.println("Password: " + (request.password() != null ? "[PRESENT]" : "[NULL]"));
    System.out.println("TwoFactorCode: " + request.twoFactorCode());
    System.out.println("=====================================");

    // Validation des champs
    if (request.username() == null || request.username().trim().isEmpty()) {
        throw new IllegalArgumentException("Le nom d'utilisateur est requis");
    }
    if (request.password() == null || request.password().trim().isEmpty()) {
        throw new IllegalArgumentException("Le mot de passe est requis");
    }
    if (request.email() == null || request.email().trim().isEmpty()) {
        throw new IllegalArgumentException("L'email est requis");
    }
    if (request.role() == null) {
        throw new IllegalArgumentException("Le rôle est requis");
    }

    // Validation du rôle
    Role role;
    try {
        role = Role.valueOf(request.role().toString().toUpperCase());
        System.out.println("Rôle validé: " + role);
    } catch (IllegalArgumentException e) {
        System.err.println("Rôle invalide: " + request.role());
        throw new IllegalArgumentException("Le rôle doit être l'une des valeurs suivantes: ADMIN, AGENT, CLIENT_PHYSIQUE, CLIENT_MORAL");
    }

    // Vérifier si l'utilisateur existe déjà
    User existingUser = userRepository.findByUsernameOrEmail(request.username(), request.email());
    if (existingUser != null) {
        System.out.println("Utilisateur existant trouvé: " + existingUser.getUsername());
        throw new IllegalArgumentException("Un utilisateur avec ce nom d'utilisateur ou cet email existe déjà");
    }

    try {
        // Créer un nouvel utilisateur
        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setEmail(request.email());
        newUser.setRole(role);
        newUser.setIs2fa_enabled(false);
        newUser.setBackup_codes_generated(false);
        newUser.setFailed2faattempts(0);
        newUser.setEnabled(true);

        System.out.println("Nouvel utilisateur créé avec succès");
        System.out.println("Tentative de sauvegarde dans la base de données...");

        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(newUser);
        System.out.println("Utilisateur sauvegardé avec succès. ID: " + savedUser.getIdUser());

        // Générer le token JWT
        String token = jwtUtil.generateToken(savedUser.getUsername());
        System.out.println("Token JWT généré avec succès");

        return token;
    } catch (Exception e) {
        System.err.println("Erreur lors de l'inscription: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Erreur lors de l'inscription: " + e.getMessage());
    }
  }
    public void updatePassword(String token, String newPassword) {

        User user = userRepository.findByResetToken(token);
        if (user != null) {
          user.setPassword(passwordEncoder.encode(newPassword));

            userRepository.save(user);
        }
    }

  private String generateToken() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[24];
    random.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);  // Generate URL-safe token
  }

  public void createPasswordResetToken(String email, String token) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec cet email: " + email));

    if (user == null) {
      throw new UserNotFoundException("Utilisateur non trouvé avec cet email" +email);
    }

    // If the user already has a valid reset token, consider notifying or not overwriting
    if (user.getResetTokenExpiry() != null && user.getResetTokenExpiry().isAfter(LocalDateTime.now())) {
      throw new RuntimeException("Un token de réinitialisation valide existe déjà.");
    }

    // Generate a token if not passed
    if (token == null || token.isEmpty()) {
      token = generateToken();
    }

    LocalDateTime expiryDate = LocalDateTime.now().plus(24, ChronoUnit.HOURS);

    user.setResetToken(token);
    user.setResetTokenExpiry(expiryDate);

    userRepository.save(user);
  }
}
