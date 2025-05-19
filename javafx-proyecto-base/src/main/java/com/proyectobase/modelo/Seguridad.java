package com.proyectobase.modelo;

import org.mindrot.jbcrypt.BCrypt;

public class Seguridad {
    
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verificarPassword(String passwordIngresada, String hashAlmacenado) {
        return BCrypt.checkpw(passwordIngresada, hashAlmacenado);
    }

}
