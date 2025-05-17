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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.controlsfx.control.Notifications;

public class UsuarioDAO {
    private final Connection connection;

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertarUsuario(Usuario usuario) {
    if (usuario == null) {
        System.err.println("Error: Usuario no puede ser nulo");
        Notifications.create()
            .title("Error")
            .text("Usuario no válido para insertar")
            .showError();
        return false;
    }

    Connection conn = null;
    PreparedStatement stmtUsuario = null;
    PreparedStatement stmtSesion = null;
    ResultSet generatedKeys = null;
    boolean exito = false;

    try {
        // Obtener conexión
        conn = ConexionSingleton.obtenerConexion();
        if (conn == null || conn.isClosed()) {
            System.err.println("Conexión cerrada. Reconectando...");
            conn = ConexionSingleton.obtenerConexion();
            if (conn == null) {
                System.err.println("No se pudo reestablecer la conexión");
                return false;
            }
        }

        // Iniciar transacción
        conn.setAutoCommit(false);

        // 1. Insertar el usuario
        String sqlUsuario = "INSERT INTO usuarios (correo, password_hash, rol, nombre, apellido, telefono, direccion, fecha_registro, activo) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);

        // Setear parámetros del usuario
        stmtUsuario.setString(1, usuario.getCorreo());
        stmtUsuario.setString(2, usuario.getPassword_hash());
        stmtUsuario.setString(3, usuario.getRol());
        stmtUsuario.setString(4, usuario.getNombre());
        stmtUsuario.setString(5, usuario.getApellido());
        
        if (usuario.getTelefono() == null) {
            stmtUsuario.setNull(6, Types.VARCHAR);
        } else {
            stmtUsuario.setString(6, usuario.getTelefono());
        }
        
        if (usuario.getDireccion() == null) {
            stmtUsuario.setNull(7, Types.VARCHAR);
        } else {
            stmtUsuario.setString(7, usuario.getDireccion());
        }
        
        stmtUsuario.setTimestamp(8, new Timestamp(usuario.getFecha_registro().getTime()));
        stmtUsuario.setInt(9, usuario.getActivo());

        int filas = stmtUsuario.executeUpdate();

        if (filas == 0) {
            throw new SQLException("La inserción del usuario falló, no se insertaron filas");
        }

        // Obtener el ID generado
        generatedKeys = stmtUsuario.getGeneratedKeys();
        if (!generatedKeys.next()) {
            throw new SQLException("No se pudo obtener el ID del usuario insertado");
        }
        int usuarioId = generatedKeys.getInt(1);
        usuario.setId(usuarioId);

        // 2. Crear sesión inicial para el usuario
        String sqlSesion = "INSERT INTO sesiones (usuario_id, dispositivo, fecha_inicio, fecha_ultima_actividad, activa) " +
                         "VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)";
        stmtSesion = conn.prepareStatement(sqlSesion);
        
        stmtSesion.setInt(1, usuarioId);
        stmtSesion.setString(2, "Sistema"); // Dispositivo por defecto
        
        stmtSesion.executeUpdate();

        // Confirmar transacción
        conn.commit();
        exito = true;

        Notifications.create()
            .title("Éxito")
            .text("Usuario registrado y sesión iniciada correctamente")
            .showInformation();

    } catch (SQLException ex) {
        System.err.println("Error al insertar usuario: " + ex.getMessage());
        ex.printStackTrace();
        
        // Rollback en caso de error
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("Error al hacer rollback: " + e.getMessage());
            }
        }
        
        Notifications.create()
            .title("Error al registrar")
            .text("Error: " + ex.getMessage())
            .showError();
            
        // Intenta reconectar si hay error de conexión
        if (ex.getMessage().contains("Connection is closed")) {
            System.err.println("Intentando reconectar...");
            ConexionSingleton.reconectar();
        }
    } finally {
        // Restaurar auto-commit y cerrar recursos
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
            if (generatedKeys != null) generatedKeys.close();
            if (stmtUsuario != null) stmtUsuario.close();
            if (stmtSesion != null) stmtSesion.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
    
    return exito;
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

    public boolean eliminarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() == 0) {
            System.err.println("Error: Usuario no válido para eliminar");
            Notifications.create()
                .title("Error")
                .text("Usuario no válido para eliminar")
                .showError();
            return false;
        }

        Connection conn = null;
        PreparedStatement stmtUsuario = null;
        PreparedStatement stmtSesiones = null;
        boolean exito = false;

        try {
            conn = ConexionSingleton.obtenerConexion();
            if (conn == null || conn.isClosed()) {
                System.err.println("Conexión cerrada. Reconectando...");
                conn = ConexionSingleton.obtenerConexion();
                if (conn == null) {
                    System.err.println("No se pudo reestablecer la conexión");
                    return false;
                }
            }

            // Iniciar transacción
            conn.setAutoCommit(false);

            // 1. Verificar relaciones que impiden la eliminación
            if (tieneVentasComoCliente(usuario.getId())) {
                throw new SQLException("No se puede eliminar: el usuario tiene ventas asociadas como cliente");
            }

            if (tieneVentasComoEmpleado(usuario.getId())) {
                throw new SQLException("No se puede eliminar: el usuario tiene ventas asociadas como empleado");
            }

            if (tieneRelacionesEnTabla(usuario.getId(), "codigos_barras", "usuario_generador")) {
                throw new SQLException("No se puede eliminar: el usuario generó códigos de barras");
            }

            // 2. Eliminar sesiones del usuario
            String sqlSesiones = "DELETE FROM sesiones WHERE usuario_id = ?";
            stmtSesiones = conn.prepareStatement(sqlSesiones);
            stmtSesiones.setInt(1, usuario.getId());
            stmtSesiones.executeUpdate();

            // 3. Eliminar el usuario
            String sqlUsuario = "DELETE FROM usuarios WHERE id = ?";
            stmtUsuario = conn.prepareStatement(sqlUsuario);
            stmtUsuario.setInt(1, usuario.getId());
            int filasAfectadas = stmtUsuario.executeUpdate();

            if (filasAfectadas > 0) {
                // Confirmar transacción
                conn.commit();
                exito = true;
                Notifications.create()
                    .title("Éxito")
                    .text("Usuario y sus sesiones eliminados correctamente")
                    .showInformation();
            } else {
                conn.rollback();
            }
        } catch (SQLException ex) {
            System.err.println("Error al eliminar usuario: " + ex.getMessage());
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("Error al hacer rollback: " + e.getMessage());
                }
            }
            Notifications.create()
                .title("Error al eliminar")
                .text(ex.getMessage())
                .showError();
        } finally {
            // Restaurar auto-commit y cerrar recursos
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
                if (stmtUsuario != null) stmtUsuario.close();
                if (stmtSesiones != null) stmtSesiones.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return exito;
    }

    // Métodos auxiliares específicos para ventas
    private boolean tieneVentasComoCliente(int usuarioId) throws SQLException {
        return tieneRelacionesEnTabla(usuarioId, "ventas", "cliente_id");
    }

    private boolean tieneVentasComoEmpleado(int usuarioId) throws SQLException {
        return tieneRelacionesEnTabla(usuarioId, "ventas", "empleado_id");
    }

    // Método auxiliar genérico
    private boolean tieneRelacionesEnTabla(int usuarioId, String tabla, String columna) throws SQLException {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", tabla, columna);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
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