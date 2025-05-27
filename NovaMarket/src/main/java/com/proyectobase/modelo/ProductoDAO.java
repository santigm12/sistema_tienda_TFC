package com.proyectobase.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.Random;
import org.controlsfx.control.Notifications;

public class ProductoDAO {
    private final Connection connection;

    // Constructor que recibe una conexión a la base de datos
    public ProductoDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para actualizar un producto en la base de datos
    /*public boolean eliminarProducto(Producto producto){
        
    }*/
    public Producto obtenerProductoPorId(int id) {
        String sql = "SELECT * FROM productos WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Crear y retornar el objeto Producto con todos los campos
                    return new Producto(
                        rs.getInt("id"),
                        rs.getString("codigo_barras"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getDouble("precio_con_iva"),
                        rs.getInt("stock"),
                        rs.getString("descripcion"),
                        rs.getString("imagenB64"),
                        rs.getString("categoria"),
                        rs.getDate("fecha_creacion"),
                        rs.getDate("fecha_actualizacion")
                    );
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener producto con ID " + id);
            System.err.println("SQL: " + sql);
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }

        return null; // Retorna null si no se encuentra el producto o hay error
    }
    public boolean actualizarProducto(Producto producto) {
        if (producto == null) {
            System.err.println("Error: El producto a actualizar es null");
            return false;
        }

        String sql = "UPDATE productos SET "
                   + "codigo_barras = ?, "
                   + "nombre = ?, "
                   + "precio = ?, "
                   + "precio_con_iva = ?, "
                   + "stock = ?, "
                   + "descripcion = ?, "
                   + "imagenB64 = ?, "
                   + "categoria = ?, "
                   + "fecha_actualizacion = CURRENT_TIMESTAMP "
                   + "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Configurar parámetros en el mismo orden que en la consulta SQL
            stmt.setString(1, producto.getCodigo_barras());
            stmt.setString(2, producto.getNombre());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setDouble(4, producto.getPrecio_con_iva());
            stmt.setInt(5, producto.getStock());
            stmt.setString(6, producto.getDescripcion());
            
            // Manejo especial para imagenB64 que puede ser null
            if (producto.getImagenB64() != null && !producto.getImagenB64().isEmpty()) {
                stmt.setString(7, producto.getImagenB64());
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }
            
            stmt.setString(8, producto.getCategoria());
            stmt.setInt(9, producto.getId());

            // Ejecutar la actualización y verificar si se afectó alguna fila
            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error al actualizar producto con ID " + producto.getId());
            System.err.println("SQL: " + sql);
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean eliminarProducto(Producto producto) {
        if (producto == null || producto.getId() == 0) {
            System.err.println("Error: Producto no válido para eliminar");
            Notifications.create()
                .title("Error")
                .text("Producto no válido para eliminar")
                .showError();
            return false;
        }

        Connection conn = null;
        PreparedStatement stmtProducto = null;
        PreparedStatement stmtCodigoBarras = null;
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

            // 1. Verificar si el producto está en detalles de venta
            if (tieneDetallesVenta(conn, producto.getId())) {
                throw new SQLException("No se puede eliminar: el producto está incluido en ventas");
            }

            // 2. Eliminar el código de barras asociado si existe
            String sqlCodigoBarras = "DELETE FROM codigos_barras WHERE producto_asignado = ?";
            stmtCodigoBarras = conn.prepareStatement(sqlCodigoBarras);
            stmtCodigoBarras.setInt(1, producto.getId());
            stmtCodigoBarras.executeUpdate();

            // 3. Eliminar el producto
            String sqlProducto = "DELETE FROM productos WHERE id = ?";
            stmtProducto = conn.prepareStatement(sqlProducto);
            stmtProducto.setInt(1, producto.getId());
            int filasAfectadas = stmtProducto.executeUpdate();

            if (filasAfectadas > 0) {
                // Confirmar transacción
                conn.commit();
                exito = true;
                Notifications.create()
                    .title("Éxito")
                    .text("Producto y su código de barras asociado eliminados correctamente")
                    .showInformation();
            } else {
                conn.rollback();
                Notifications.create()
                    .title("Advertencia")
                    .text("No se encontró el producto para eliminar")
                    .showWarning();
            }
        } catch (SQLException ex) {
            System.err.println("Error al eliminar producto: " + ex.getMessage());
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
                if (stmtProducto != null) stmtProducto.close();
                if (stmtCodigoBarras != null) stmtCodigoBarras.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return exito;
    }

    // Método auxiliar para verificar si el producto tiene ventas asociadas
    private boolean tieneDetallesVenta(Connection conn, int productoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM detalle_venta WHERE producto_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Métodos auxiliares para verificar relaciones
    private boolean tieneDetallesVenta(int productoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM detalle_venta WHERE producto_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private boolean tieneCodigoBarrasAsignado(int productoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM codigos_barras WHERE producto_asignado = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    
    public boolean insertarProducto(Producto producto) {
        if (producto == null) {
            System.err.println("Error: Producto no puede ser nulo");
            Notifications.create()
                .title("Error")
                .text("Producto no válido para insertar")
                .showError();
            return false;
        }

        Connection conn = null;
        PreparedStatement stmtProducto = null;
        PreparedStatement stmtCodigoBarras = null;
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

            // 1. Insertar el producto
            String sqlProducto = "INSERT INTO productos (codigo_barras, nombre, precio, precio_con_iva, stock, descripcion, imagenB64, categoria) "
                              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmtProducto = conn.prepareStatement(sqlProducto, Statement.RETURN_GENERATED_KEYS);

            // Generar código de barras si no existe
            String codigoBarras = producto.getCodigo_barras();
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                codigoBarras = generarCodigoBarrasUnico(conn);
                producto.setCodigo_barras(codigoBarras);
            }

            // Setear parámetros
            stmtProducto.setString(1, codigoBarras);
            stmtProducto.setString(2, producto.getNombre());
            stmtProducto.setDouble(3, producto.getPrecio());
            stmtProducto.setDouble(4, producto.getPrecio_con_iva());
            stmtProducto.setInt(5, producto.getStock());
            stmtProducto.setString(6, producto.getDescripcion());

            if (producto.getImagenB64() != null && !producto.getImagenB64().isEmpty()) {
                stmtProducto.setString(7, producto.getImagenB64());
            } else {
                stmtProducto.setNull(7, Types.VARCHAR);
            }

            stmtProducto.setString(8, producto.getCategoria());

            int filas = stmtProducto.executeUpdate();

            if (filas == 0) {
                throw new SQLException("La inserción del producto falló, no se insertaron filas");
            }

            // Obtener ID generado
            generatedKeys = stmtProducto.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("No se pudo obtener el ID del producto insertado");
            }
            int productoId = generatedKeys.getInt(1);
            producto.setId(productoId);

            // 2. Insertar código de barras
            String sqlCodigoBarras = "INSERT INTO codigos_barras (codigo, producto_asignado, fecha_generacion, usuario_generador) "
                                  + "VALUES (?, ?, CURRENT_TIMESTAMP, ?)";
            stmtCodigoBarras = conn.prepareStatement(sqlCodigoBarras);

            stmtCodigoBarras.setString(1, codigoBarras);
            stmtCodigoBarras.setInt(2, productoId);
            stmtCodigoBarras.setInt(3, obtenerUsuarioActual());

            stmtCodigoBarras.executeUpdate();

            // Confirmar transacción
            conn.commit();
            exito = true;

            Notifications.create()
                .title("Éxito")
                .text("Producto registrado correctamente")
                .showInformation();

        } catch (SQLException ex) {
            System.err.println("Error al insertar producto: " + ex.getMessage());
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
                if (stmtProducto != null) stmtProducto.close();
                if (stmtCodigoBarras != null) stmtCodigoBarras.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return exito;
    }

   
    private int obtenerUsuarioActual() {
        // Implementación según tu sistema de autenticación
        return 1; // Ejemplo: ID del usuario admin
    }

    // Método auxiliar para generar código de barras único
    private String generarCodigoBarrasUnico(Connection conn) throws SQLException {
        Random random = new Random();
        String codigo;
        do {
            // Generar un código de 13 dígitos (formato EAN-13)
            long num = 1000000000000L + random.nextInt(900000000);
            codigo = String.valueOf(num);

            // Verificar si ya existe
            String sql = "SELECT COUNT(*) FROM codigos_barras WHERE codigo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, codigo);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    break; // Código único encontrado
                }
            }
        } while (true);

        return codigo;
    }


}