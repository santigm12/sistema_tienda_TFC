package com.proyectobase.modelo;

import javafx.scene.control.Alert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static java.lang.System.exit;

public class ConexionSingleton {

    private static Connection conexion;

    public static Connection obtenerConexion() {
        if(conexion==null){
            try{
                conexion = getConnection();
                System.out.println("Conexión establecida");
            }catch (IOException e){
                System.out.println("No se pudo conectar");
            }
        }
        return conexion;
    }

    public static synchronized Connection reconectar() {
        cerrarConexionExistente();
        
        try {
            conexion = DriverManager.getConnection("jdbc:mariadb://..."); 
            System.out.println("Conexión (re)establecida");
            return conexion;
        } catch (SQLException e) {
            System.err.println("Error al reconectar: " + e.getMessage());
            return null;
        }
    }
    
    private static void cerrarConexionExistente() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
    
    private static Connection getConnection() throws IOException {
        Properties properties = new Properties();
        String IP, PORT, BBDD, USER, PWD;
        try {
            InputStream input_ip = new FileInputStream("ip.properties");
            properties.load(input_ip);
            IP = (String) properties.get("IP");
        } catch (FileNotFoundException e) {
            System.out.println("No se pudo encontrar el archivo de propiedades para IP, se establece localhost por defecto");
            IP = "54.173.46.205";
        }

        InputStream input = ConexionSingleton.class.getClassLoader().getResourceAsStream("bbdd.properties");
        if (input == null) {
            System.out.println("No se pudo encontrar el archivo de propiedades");
            return null;
        } else {
            properties.load(input);
            PORT = properties.getProperty("PORT", "3306");
            BBDD = properties.getProperty("BBDD", "sistema_tienda");
            USER = properties.getProperty("USER", "admin");
            PWD = properties.getProperty("PWD", "WXzU4jdi9wWy");

            
            Connection conn;
            try {
                String cadconex = "jdbc:mariadb://" + IP + ":" + PORT + "/" + BBDD + " USER:" + USER + "PWD:" + PWD;
                System.out.println(cadconex);
                conn = DriverManager.getConnection("jdbc:mariadb://" + IP + ":" + PORT + "/" + BBDD, USER, PWD);
                return conn;
            } catch (SQLException e) {
                System.out.println("Error SQL: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Ha ocurrido un error de conexión");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                exit(0);
                return null;
            }
        }
    }

}