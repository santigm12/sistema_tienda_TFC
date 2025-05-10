package com.proyectobase.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

public class ProductoDAO {
    private final Connection connection;

    // Constructor que recibe una conexión a la base de datos
    public ProductoDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para actualizar un producto en la base de datos
    /*public boolean eliminarProducto(Producto producto){
        
    }*/
    
    public boolean insertarProducto(Producto producto) {
    String sql = "INSERT INTO productos (codigo_barras, nombre, precio, stock, descripcion, imagenB64, categoria, fecha_actualizacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    // No usar try-with-resources para la Connection (la maneja el Singleton)
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet generatedKeys = null;
    
    try {
        // Obtener conexión verificando si está cerrada
        conn = ConexionSingleton.obtenerConexion();
        if (conn == null || conn.isClosed()) {
            System.err.println("Conexión cerrada. Reconectando...");
            conn = ConexionSingleton.obtenerConexion(); // El Singleton debería manejar la reconexión
            if (conn == null) {
                System.err.println("No se pudo reestablecer la conexión");
                return false;
            }
        }

        stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Setear parámetros
        stmt.setString(1, producto.getCodigo_barras());
        stmt.setString(2, producto.getNombre());
        stmt.setDouble(3, producto.getPrecio());
        stmt.setInt(4, producto.getStock());
        stmt.setString(5, producto.getDescripcion());
        
        // Manejar imagen nula correctamente
        if (producto.getImagenB64() == null) {
            stmt.setNull(6, Types.VARCHAR);
        } else {
            stmt.setString(6, producto.getImagenB64());
        }
        
        stmt.setString(7, producto.getCategoria());
        stmt.setDate(8, new java.sql.Date(producto.getFecha_actualizacion().getTime()));

        int filas = stmt.executeUpdate();

        if (filas > 0) {
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys != null && generatedKeys.next()) {
                producto.setId(generatedKeys.getInt(1));
            }
            return true;
        }
    } catch (SQLException ex) {
        System.err.println("Error al insertar producto: " + ex.getMessage());
        ex.printStackTrace();
        
        // Intenta reconectar si hay error de conexión
        if (ex.getMessage().contains("Connection is closed")) {
            System.err.println("Intentando reconectar...");
            ConexionSingleton.reconectar();
        }
    } finally {
        // Cerrar solo los recursos temporales
        try {
            if (generatedKeys != null) generatedKeys.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
    return false;
}
    
    // Puedes agregar más métodos de operaciones CRUD si es necesario, como eliminar o insertar productos.
}
