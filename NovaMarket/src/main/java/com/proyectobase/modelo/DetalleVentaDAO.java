/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobase.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.controlsfx.control.Notifications;

/**
 *
 * @author Santiago
 */


public class DetalleVentaDAO {
        private final Connection connection;

        public DetalleVentaDAO(Connection connection) {
            this.connection = connection;
        }
        
        
        
        private DetalleVenta obtenerDetallePorId(int id, Connection conn) throws SQLException {
            String sql = "SELECT * FROM detalle_venta WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new DetalleVenta(
                        rs.getInt("id"),
                        rs.getInt("venta_id"),
                        rs.getInt("producto_id"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio_unitario"),
                        rs.getDouble("subtotal")
                    );
                }
                return null;
            }
        }
        
        
        private void actualizarTotalVenta(int ventaId, Connection conn) throws SQLException {
            // Calcular nuevo total sumando los subtotales de los detalles restantes
            String sqlSuma = "SELECT COALESCE(SUM(subtotal), 0) AS total FROM detalle_venta WHERE venta_id = ?";
            double nuevoTotal = 0;

            try (PreparedStatement stmt = conn.prepareStatement(sqlSuma)) {
                stmt.setInt(1, ventaId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    nuevoTotal = rs.getDouble("total");
                }
            }

            // Actualizar el total en la venta
            String sqlUpdate = "UPDATE ventas SET total = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setDouble(1, nuevoTotal);
                stmt.setInt(2, ventaId);
                stmt.executeUpdate();
            }
        }
        public boolean eliminarDetalleVenta(DetalleVenta detalle) throws SQLException {
            if (detalle == null || detalle.getId() == 0) {
                throw new SQLException("Detalle de venta no válido para eliminar");
            }

            Connection conn = null;
            PreparedStatement stmtDetalle = null;
            PreparedStatement stmtActualizarVenta = null;
            PreparedStatement stmtActualizarStock = null;
            boolean exito = false;

            try {
                conn = ConexionSingleton.obtenerConexion();
                conn.setAutoCommit(false);

                // 1. Eliminar el detalle de venta
                String sqlDetalle = "DELETE FROM detalle_venta WHERE id = ?";
                stmtDetalle = conn.prepareStatement(sqlDetalle);
                stmtDetalle.setInt(1, detalle.getId());
                int filasEliminadas = stmtDetalle.executeUpdate();

                if (filasEliminadas == 0) {
                    throw new SQLException("No se encontró el detalle de venta para eliminar");
                }

                String sqlActualizarStock = "UPDATE productos SET stock = stock + ? WHERE id = ?";
                stmtActualizarStock = conn.prepareStatement(sqlActualizarStock);
                stmtActualizarStock.setInt(1, detalle.getCantidad());
                stmtActualizarStock.setInt(2, detalle.getProducto_id());
                stmtActualizarStock.executeUpdate();

                String sqlActualizarVenta = "UPDATE ventas SET total = total - ? WHERE id = ?";
                stmtActualizarVenta = conn.prepareStatement(sqlActualizarVenta);

                BigDecimal subtotal = new BigDecimal(detalle.getSubtotal()).setScale(2, RoundingMode.HALF_UP);
                stmtActualizarVenta.setBigDecimal(1, subtotal);

                stmtActualizarVenta.setInt(2, detalle.getVenta_id());
                stmtActualizarVenta.executeUpdate();

                conn.commit();
                exito = true;

            } catch (SQLException ex) {
                if (conn != null) conn.rollback();
                throw ex;
            } finally {
                try {
                    if (conn != null) conn.setAutoCommit(true);
                    if (stmtDetalle != null) stmtDetalle.close();
                    if (stmtActualizarVenta != null) stmtActualizarVenta.close();
                    if (stmtActualizarStock != null) stmtActualizarStock.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar recursos: " + e.getMessage());
                }
            }
            return exito;
        }

        public boolean insertarDetalleVenta(DetalleVenta detalle) throws SQLException {
            if (detalle == null || detalle.getVenta_id() == 0 || detalle.getProducto_id() == 0) {
                throw new SQLException("Detalle de venta no válido para insertar");
            }

            Connection conn = null;
            PreparedStatement stmtDetalle = null;
            PreparedStatement stmtActualizarVenta = null;
            PreparedStatement stmtActualizarStock = null;
            ResultSet generatedKeys = null;
            boolean exito = false;

            try {
                // Obtener conexión
                conn = ConexionSingleton.obtenerConexion();
                if (conn == null || conn.isClosed()) {
                    System.err.println("Conexión cerrada. Reconectando...");
                    conn = ConexionSingleton.obtenerConexion();
                    if (conn == null) {
                        throw new SQLException("No se pudo reestablecer la conexión");
                    }
                }

                // Iniciar transacción
                conn.setAutoCommit(false);

                // 1. Verificar stock disponible
                String sqlVerificarStock = "SELECT stock FROM productos WHERE id = ? FOR UPDATE";
                try (PreparedStatement stmtVerificar = conn.prepareStatement(sqlVerificarStock)) {
                    stmtVerificar.setInt(1, detalle.getProducto_id());
                    ResultSet rs = stmtVerificar.executeQuery();
                    if (!rs.next()) {
                        throw new SQLException("Producto no encontrado");
                    }
                    int stockActual = rs.getInt("stock");
                    if (stockActual < detalle.getCantidad()) {
                        throw new SQLException("Stock insuficiente para el producto");
                    }
                }

                // 2. Insertar el detalle de venta
                String sqlDetalle = "INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_unitario, subtotal) "
                                 + "VALUES (?, ?, ?, ?, ?)";
                stmtDetalle = conn.prepareStatement(sqlDetalle, Statement.RETURN_GENERATED_KEYS);

                stmtDetalle.setInt(1, detalle.getVenta_id());
                stmtDetalle.setInt(2, detalle.getProducto_id());
                stmtDetalle.setInt(3, detalle.getCantidad());
                stmtDetalle.setDouble(4, detalle.getPrecio_unitario());
                stmtDetalle.setDouble(5, detalle.getSubtotal());

                int filas = stmtDetalle.executeUpdate();

                if (filas == 0) {
                    throw new SQLException("La inserción del detalle falló, no se insertaron filas");
                }

                // Obtener ID generado
                generatedKeys = stmtDetalle.getGeneratedKeys();
                if (!generatedKeys.next()) {
                    throw new SQLException("No se pudo obtener el ID del detalle insertado");
                }
                detalle.setId(generatedKeys.getInt(1));

                // 3. Actualizar stock del producto
                String sqlActualizarStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";
                stmtActualizarStock = conn.prepareStatement(sqlActualizarStock);
                stmtActualizarStock.setInt(1, detalle.getCantidad());
                stmtActualizarStock.setInt(2, detalle.getProducto_id());
                stmtActualizarStock.executeUpdate();

                // 4. Actualizar total de la venta
                String sqlActualizarVenta = "UPDATE ventas SET total = total + ? WHERE id = ?";
                stmtActualizarVenta = conn.prepareStatement(sqlActualizarVenta);
                stmtActualizarVenta.setDouble(1, detalle.getSubtotal());
                stmtActualizarVenta.setInt(2, detalle.getVenta_id());
                stmtActualizarVenta.executeUpdate();

                // Confirmar transacción
                conn.commit();
                exito = true;

            } catch (SQLException ex) {
                System.err.println("Error al insertar detalle de venta: " + ex.getMessage());
                // Rollback en caso de error
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException e) {
                        System.err.println("Error al hacer rollback: " + e.getMessage());
                    }
                }
                throw ex; // Relanzar la excepción para manejo en el controlador
            } finally {
                // Restaurar auto-commit y cerrar recursos
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                    }
                    if (generatedKeys != null) generatedKeys.close();
                    if (stmtDetalle != null) stmtDetalle.close();
                    if (stmtActualizarStock != null) stmtActualizarStock.close();
                    if (stmtActualizarVenta != null) stmtActualizarVenta.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar recursos: " + e.getMessage());
                }
            }
            return exito;
        }
        

        // Método para verificar stock antes de insertar (opcional)
        public boolean verificarStockDisponible(int productoId, int cantidad) throws SQLException {
            String sql = "SELECT stock FROM productos WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, productoId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("stock") >= cantidad;
                }
                throw new SQLException("Producto no encontrado");
            }
        }
    }
