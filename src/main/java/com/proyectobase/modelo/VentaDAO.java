/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobase.modelo;


import com.proyectobase.modelo.Venta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    private final Connection connection;

    public VentaDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para insertar una nueva venta
    public boolean insertarVenta(Venta venta) {
        String sql = "INSERT INTO ventas (cliente_id, empleado_id, fecha, descripcion, total, metodo_pago, tipo_venta, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Establecer parámetros
            stmt.setInt(1, venta.getCliente_id());
            stmt.setInt(2, venta.getEmpleado_id());
            stmt.setTimestamp(3, new Timestamp(venta.getFecha().getTime()));
            stmt.setString(4, venta.getDescripcion());
            stmt.setDouble(5, venta.getTotal());
            stmt.setString(6, venta.getMetodo_pago());
            stmt.setString(7, venta.getTipo_venta());
            stmt.setString(8, venta.getEstado());

            int filasAfectadas = stmt.executeUpdate();
            
            // Obtener el ID generado
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        venta.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al insertar venta: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    // Método para obtener una venta por su ID
    public Venta obtenerPorId(int id) {
        String sql = "SELECT * FROM ventas WHERE id = ?";
        Venta venta = null;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    venta = new Venta(
                        rs.getInt("id"),
                        rs.getInt("cliente_id"),
                        rs.getInt("empleado_id"),
                        new Date(rs.getTimestamp("fecha").getTime()),
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
            stmt.setTimestamp(3, new Timestamp(venta.getFecha().getTime()));
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

    // Método para eliminar una venta
    public boolean eliminarVenta(int id) {
        String sql = "DELETE FROM ventas WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al eliminar venta: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
}