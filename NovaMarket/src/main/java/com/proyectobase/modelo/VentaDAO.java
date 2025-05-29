/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobase.modelo;


import com.proyectobase.modelo.Venta;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.controlsfx.control.Notifications;

public class VentaDAO {
    private final Connection connection;

    public VentaDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para insertar una nueva venta
    public boolean crearVentaConDetalles(Venta venta, List<DetalleVenta> detalles) {
        if (venta == null || detalles == null) {
            System.err.println("Error: Venta o detalles no válidos");
            Notifications.create()
                .title("Error")
                .text("Datos de venta incompletos")
                .showError();
            return false;
        }

        Connection conn = null;
        PreparedStatement stmtVenta = null;
        PreparedStatement stmtDetalle = null;
        ResultSet generatedKeys = null;
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

            conn.setAutoCommit(false); // Iniciar transacción

            // Insertar venta
            String sqlVenta = "INSERT INTO ventas (cliente_id, empleado_id, fecha, total, metodo_pago, tipo_venta, estado, descripcion) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);

            stmtVenta.setInt(1, venta.getCliente_id());
            stmtVenta.setInt(2, venta.getEmpleado_id());
            stmtVenta.setTimestamp(3, Timestamp.valueOf(venta.getFecha().atStartOfDay()));
            stmtVenta.setDouble(4, venta.getTotal());
            stmtVenta.setString(5, venta.getMetodo_pago());
            stmtVenta.setString(6, venta.getTipo_venta());
            stmtVenta.setString(7, venta.getEstado());
            stmtVenta.setString(8, venta.getDescripcion());

            int filasAfectadas = stmtVenta.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo crear la venta");
            }

            generatedKeys = stmtVenta.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("No se pudo obtener el ID de la venta creada");
            }
            int ventaId = generatedKeys.getInt(1);
            venta.setId(ventaId);

            // Insertar los detalles de la venta
            String sqlDetalle = "INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_unitario, subtotal) " +
                                "VALUES (?, ?, ?, ?, ?)";
            stmtDetalle = conn.prepareStatement(sqlDetalle);

            for (DetalleVenta detalle : detalles) {
                detalle.setVenta_id(ventaId); // establecer venta_id
                stmtDetalle.setInt(1, detalle.getVenta_id());
                stmtDetalle.setInt(2, detalle.getProducto_id());
                stmtDetalle.setInt(3, detalle.getCantidad());
                stmtDetalle.setDouble(4, detalle.getPrecio_unitario());
                stmtDetalle.setDouble(5, detalle.getSubtotal());
                stmtDetalle.addBatch();
            }

            stmtDetalle.executeBatch(); // ejecutar todos los inserts juntos
            conn.commit(); // confirmar transacción
            exito = true;

            Notifications.create()
                .title("Éxito")
                .text("Venta registrada correctamente")
                .showInformation();

        } catch (SQLException ex) {
            System.err.println("Error al crear venta: " + ex.getMessage());
            ex.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("Error al hacer rollback: " + e.getMessage());
                }
            }
            Notifications.create()
                .title("Error al registrar venta")
                .text(ex.getMessage())
                .showError();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
                if (generatedKeys != null) generatedKeys.close();
                if (stmtVenta != null) stmtVenta.close();
                if (stmtDetalle != null) stmtDetalle.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return exito;
    }


    // Métodos auxiliares
    private boolean verificarStockDisponible(Connection conn, int productoId, int cantidadRequerida) throws SQLException {
        String sql = "SELECT stock FROM productos WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock") >= cantidadRequerida;
            }
            throw new SQLException("Producto no encontrado");
        }
    }

    private void actualizarStockProducto(Connection conn, int productoId, int cantidadCambio) throws SQLException {
        String sql = "UPDATE productos SET stock = stock + ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cantidadCambio);
            stmt.setInt(2, productoId);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No se pudo actualizar el stock del producto");
            }
        }
    }

    // Método para obtener una venta por su ID
    public Venta obtenerPorId(int id) {
        String sql = "SELECT * FROM ventas WHERE id = ?";
        Venta venta = null;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Convertir java.sql.Timestamp a LocalDate
                    java.sql.Timestamp fechaTimestamp = rs.getTimestamp("fecha");
                    LocalDate fecha = fechaTimestamp != null ? fechaTimestamp.toLocalDateTime().toLocalDate() : null;

                    venta = new Venta(
                        rs.getInt("id"),
                        rs.getInt("cliente_id"),
                        rs.getInt("empleado_id"),
                        fecha,  // Usamos el LocalDate convertido
                        rs.getString("descripcion"),
                        rs.getDouble("total"),
                        rs.getString("metodo_pago"),
                        rs.getString("tipo_venta"),
                        rs.getString("estado")
                    );
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener venta: " + ex.getMessage());
            ex.printStackTrace();
        }
        return venta;
    }

    // Método para actualizar una venta existente
    public boolean actualizarVenta(Venta venta) {
        String sql = "UPDATE ventas SET cliente_id = ?, empleado_id = ?, fecha = ?, descripcion = ?, " +
                     "total = ?, metodo_pago = ?, tipo_venta = ?, estado = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, venta.getCliente_id());
            stmt.setInt(2, venta.getEmpleado_id());
            stmt.setTimestamp(3, Timestamp.valueOf(venta.getFecha().atStartOfDay()));
            stmt.setString(4, venta.getDescripcion());
            stmt.setDouble(5, venta.getTotal());
            stmt.setString(6, venta.getMetodo_pago());
            stmt.setString(7, venta.getTipo_venta());
            stmt.setString(8, venta.getEstado());
            stmt.setInt(9, venta.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al actualizar venta: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    
    
    
    public boolean eliminarVenta(Venta venta) {
        if (venta == null || venta.getId() == 0) {
            System.err.println("Error: Venta no válida para eliminar");
            Notifications.create()
                .title("Error")
                .text("Venta no válida para eliminar")
                .showError();
            return false;
        }

        // Verificar todas las relaciones posibles
        try {
            
            // Si pasa todas las validaciones, proceder con eliminación
            String sql = "DELETE FROM ventas WHERE id = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, venta.getId());

                int filasAfectadas = stmt.executeUpdate();

                if (filasAfectadas > 0) {
                    Notifications.create()
                        .title("Éxito")
                        .text("Venta eliminada correctamente")
                        .showInformation();
                    return true;
                }
                return false;
            }
        } catch (SQLException ex) {
            System.err.println("Error al eliminar la venta: " + ex.getMessage());
            Notifications.create()
                .title("Error al eliminar")
                .text(ex.getMessage())
                .showError();
            return false;
        }
    }
}