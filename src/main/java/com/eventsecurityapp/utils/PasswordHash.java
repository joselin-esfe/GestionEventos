package com.eventsecurityapp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase utilitaria para realizar el cifrado hash de contraseñas.
 * Utiliza el algoritmo estándar SHA-256 para mayor seguridad.
 */
public class PasswordHash {

    /**
     * Genera un hash SHA-256 a partir de una cadena de texto plana (contraseña).
     *
     * @param password La contraseña en texto plano a cifrar.
     * @return El hash generado representado como una cadena hexadecimal de 64 caracteres.
     */
    public static String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            
            // Convertir los bytes a representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: No se pudo encontrar el algoritmo SHA-256.", e);
        }
    }
}
