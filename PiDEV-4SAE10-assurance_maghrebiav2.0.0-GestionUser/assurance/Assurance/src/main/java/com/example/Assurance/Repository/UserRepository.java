package com.example.Assurance.Repository;


import com.example.Assurance.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //User findByUsername(String username);
    Optional<User> findByEmail(String email);
  User findByUsernameOrEmail(String username, String email);
  Optional<User> findByUsername(String username);
  Optional<User> findByIdUser(Long idUser);
  User findByResetToken(String resetToken); // Assurez-vous que le token est bien stocké pour chaque utilisateur


}
