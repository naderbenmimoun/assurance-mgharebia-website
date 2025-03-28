package com.example.Assurance.Service;


import com.example.Assurance.Entity.User;
import com.example.Assurance.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    private UserRepository userRepository;

    // Méthode pour générer un token unique
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    // Méthode pour vérifier si le token est valide
    public boolean isValidToken(String token) {
        // Rechercher l'utilisateur associé à ce token
        User user = userRepository.findByResetToken(token);

        // Si l'utilisateur existe et que le token n'est pas expiré
        if (user != null && user.getResetTokenExpiry().isAfter(LocalDateTime.now())) {
            return true;
        }
        return false;
    }

    // Méthode pour invalider un token après utilisation
    public void invalidateToken(String token) {
        User user = userRepository.findByResetToken(token);

        if (user != null) {
            // Supprimer ou invalider le token de l'utilisateur
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
        }
    }
}
