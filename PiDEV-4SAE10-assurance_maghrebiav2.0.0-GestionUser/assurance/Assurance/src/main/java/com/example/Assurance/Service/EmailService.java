package com.example.Assurance.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void send2FAEnabledEmail(String to, String username) {
        try {
            logger.info("Tentative d'envoi d'email de confirmation 2FA à : {}", to);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Activation de l'authentification à deux facteurs");
            
            String emailContent = String.format(
                "Bonjour %s,\n\n" +
                "L'authentification à deux facteurs (2FA) a été activée pour votre compte.\n\n" +
                "Pour vous connecter, vous devrez maintenant utiliser votre application d'authentification " +
                "pour générer un code à 6 chiffres en plus de votre nom d'utilisateur et mot de passe.\n\n" +
                "Si vous n'avez pas activé la 2FA, veuillez contacter immédiatement le support.\n\n" +
                "Cordialement,\n" +
                "L'équipe Assurance Maghrebia",
                username
            );
            
            message.setText(emailContent);
            mailSender.send(message);
            logger.info("Email de confirmation 2FA envoyé avec succès à : {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de confirmation 2FA à : {}", to, e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email de confirmation 2FA", e);
        }
    }

    public void send2FACodeEmail(String to, String username, String code) {
        try {
            logger.info("Tentative d'envoi d'email avec code 2FA à : {}", to);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Code de vérification 2FA");
            
            String emailContent = String.format(
                "Bonjour %s,\n\n" +
                "Voici votre code de vérification 2FA : %s\n\n" +
                "Ce code est valable pendant 30 secondes.\n" +
                "Si vous n'avez pas demandé ce code, veuillez ignorer cet email.\n\n" +
                "Cordialement,\n" +
                "L'équipe Assurance Maghrebia",
                username,
                code
            );
            
            message.setText(emailContent);
            mailSender.send(message);
            logger.info("Email avec code 2FA envoyé avec succès à : {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email avec code 2FA à : {}", to, e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email avec code 2FA", e);
        }
    }
} 