package com.proyectobase.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.Date;

public class UsuarioDAO {
    private final Connection connection;

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (correo, password_hash, rol, nombre, apellido, telefono, direccion, fecha_registro, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            // Obtener conexión verificando si está cerrada
            conn = ConexionSingleton.obtenerConexion();
            if (conn == null || conn.isClosed()) {
                System.err.println("Conexión cerrada. Reconectando...");
                conn = ConexionSingleton.obtenerConexion();
                if (conn == null) {
                    System.err.println("No se pudo reestablecer la conexión");
                    return false;
                }
            }

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Setear parámetros
            stmt.setString(1, usuario.getCorreo());
            stmt.setString(2, usuario.getPassword_hash());
            stmt.setString(3, usuario.getRol());
            stmt.setString(4, usuario.getNombre());
            stmt.setString(5, usuario.getApellido());
            
            // Manejar teléfono nulo
            if (usuario.getTelefono() == null) {
                stmt.setNull(6, Types.VARCHAR);
            } else {
                stmt.setString(6, usuario.getTelefono());
            }
            
            // Manejar dirección nula
            if (usuario.getDireccion() == null) {
                stmt.setNull(7, Types.VARCHAR);
            } else {
                stmt.setString(7, usuario.getDireccion());
            }
            
            stmt.setTimestamp(8, new Timestamp(usuario.getFecha_registro().getTime()));
            stmt.setInt(9, usuario.getActivo());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys != null && generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("Error al insertar usuario: " + ex.getMessage());
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

    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        Usuario usuario = null;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("correo"),
                        rs.getString("password_hash"),
                        rs.getString("rol"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getTimestamp("fecha_registro"),
                        rs.getInt("activo")
                    );
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener usuario: " + ex.getMessage());
            ex.printStackTrace();
        }
        return usuario;
    }

    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET correo = ?, password_hash = ?, rol = ?, nombre = ?, " +
                     "apellido = ?, telefono = ?, direccion = ?, activo = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getCorreo());
            stmt.setString(2, usuario.getPassword_hash());
            stmt.setString(3, usuario.getRol());
            stmt.setString(4, usuario.getNombre());
            stmt.setString(5, usuario.getApellido());
            
            if (usuario.getTelefono() == null) {
                stmt.setNull(6, Types.VARCHAR);
            } else {
                stmt.setString(6, usuario.getTelefono());
            }
            
            if (usuario.getDireccion() == null) {
                stmt.setNull(7, Types.VARCHAR);
            } else {
                stmt.setString(7, usuario.getDireccion());
            }
            
            stmt.setInt(8, usuario.getActivo());
            stmt.setInt(9, usuario.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al actualizar usuario: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al eliminar usuario: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    public String generarCorreoUnico(String nombre, String apellido) throws SQLException {
        // Validación básica de parámetros
        if (nombre == null || nombre.trim().isEmpty() || apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre y apellido son requeridos");
        }

        // Normalizar nombres (quitar acentos, espacios y convertir a minúsculas)
        String nombreBase = normalizarTexto(nombre.trim().split(" ")[0]); // Toma solo el primer nombre
        String apellidoBase = normalizarTexto(apellido.trim().split(" ")[0]); // Toma solo el primer apellido

        // Dominio de la organización (configurable)
        String dominio = "empresa.com"; // Puedes cambiarlo o hacerlo configurable

        String correoBase = nombreBase.toLowerCase() + "." + apellidoBase.toLowerCase();
        String correoPropuesto = correoBase + "@" + dominio;

        int contador = 1;
        boolean correoExiste = true;
        String correoFinal = correoPropuesto;

        // Verificar duplicados y añadir número si es necesario
        while (correoExiste && contador < 1000) { // Límite para evitar bucles infinitos
            if (contador > 1) {
                correoFinal = correoBase + contador + "@" + dominio;
            }

            correoExiste = verificarCorreoExistente(correoFinal);

            if (!correoExiste) {
                break;
            }

            contador++;
        }

        return correoFinal;
    }

    /**
     * Método auxiliar para normalizar texto (quitar acentos y caracteres especiales)
     */
    private String normalizarTexto(String texto) {
        // Eliminar acentos y caracteres especiales
        String normalized = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-zA-Z0-9]", "");

        return normalized.isEmpty() ? "user" : normalized; // Valor por defecto si queda vacío
    }

    /**
     * Verifica si un correo ya existe en la base de datos
     */
    private boolean verificarCorreoExistente(String correo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE correo = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, correo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }
}