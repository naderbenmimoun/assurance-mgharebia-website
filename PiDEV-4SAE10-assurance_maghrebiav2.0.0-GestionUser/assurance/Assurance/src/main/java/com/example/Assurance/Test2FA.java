package com.example.Assurance;

import com.warrenstrange.googleauth.GoogleAuthenticator;

public class Test2FA {
  public static void main(String[] args) {
    String secret = "DBIH36QCC5O3MMV7NYVL6GFCCNEHGWVL";  // Remplace par ton secret réel
    GoogleAuthenticator gAuth = new GoogleAuthenticator();
    int code = gAuth.getTotpPassword(secret);
    System.out.println("Code généré avec le secret : " + code);
  }
}


