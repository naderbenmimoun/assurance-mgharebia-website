package com.example.Assurance.Entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;
    private String twoFactorSecret;  // Field for storing the 2FA secret
    private boolean is2fa_enabled = false;  // Default value is false
    private boolean backup_codes_generated = false;  // Default value is false
    private int failed2faattempts = 0;  // Default value is 0
    private boolean enabled = true;  // Default value is true for Spring Security

    @Enumerated(EnumType.STRING) // Pour stocker le rôle sous forme de chaîne
    @Column(nullable = false)
    private Role role;

    private String resetToken; // Token de réinitialisation
    private LocalDateTime resetTokenExpiry; // Date d'expiration du token

    // Getters et setters

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }

    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }

    public boolean isIs2fa_enabled() {
        return is2fa_enabled;
    }

    public void setIs2fa_enabled(boolean is2fa_enabled) {
        this.is2fa_enabled = is2fa_enabled;
    }

    public boolean isBackup_codes_generated() {
        return backup_codes_generated;
    }

    public void setBackup_codes_generated(boolean backup_codes_generated) {
        this.backup_codes_generated = backup_codes_generated;
    }

    public int getFailed2faattempts() {
        return failed2faattempts;
    }

    public void setFailed2faattempts(int failed2faattempts) {
        this.failed2faattempts = failed2faattempts;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
