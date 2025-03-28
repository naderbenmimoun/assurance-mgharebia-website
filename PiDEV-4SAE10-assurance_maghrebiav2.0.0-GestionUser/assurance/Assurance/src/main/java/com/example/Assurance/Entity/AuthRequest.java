package com.example.Assurance.Entity;

public record AuthRequest(Long idUser, String username, String password, String email, Role role, String twoFactorCode) {}
