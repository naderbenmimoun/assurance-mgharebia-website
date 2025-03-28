package com.example.Assurance.Service;

import com.example.Assurance.Entity.User;
import com.example.Assurance.Repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));
    }

  public User getUserProfile(String username) {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

    return user; // Returning the full user object
  }


  // Method to find user by username
    public User findUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        return user.orElseThrow(() -> new RuntimeException("User with username " + username + " does not exist in the database."));
    }





    // Method to save the 2FA secret in the database for a user

  public String generateAndSaveSecretForUser(String username) {
    GoogleAuthenticator gAuth = new GoogleAuthenticator();
    GoogleAuthenticatorKey credentials = gAuth.createCredentials();
    String secret = credentials.getKey();

    Optional<User> optionalUser = userRepository.findByUsername(username);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      user.setTwoFactorSecret(secret);
      userRepository.save(user);
      return secret;
    }
    return null; // Retourne null si l'utilisateur n'existe pas
  }
  private boolean verify2FA(String username, String code) {
    // Récupère le secret 2FA de l'utilisateur
    String secret = get2FASecret(username);
    if (secret == null) {
      return false;  // Si aucun secret 2FA n'est trouvé, retourne false
    }

    GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
    return googleAuthenticator.authorize(secret, Integer.parseInt(code));  // Vérifie le code 2FA
  }

  public String get2FASecret(String username) {
    Optional<User> user = userRepository.findByUsername(username);

    if (user.isPresent()) {
      System.out.println("Utilisateur trouvé : " + username);
      System.out.println("Secret 2FA : " + user.get().getTwoFactorSecret());
      return user.get().getTwoFactorSecret();
    } else {
      System.out.println("Utilisateur non trouvé !");
      return null;
    }
  }


}
