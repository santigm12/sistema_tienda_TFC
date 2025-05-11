/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobase.modelo;

import java.util.Date;

/**
 *
 * @author Santiago
 */
public class Venta {
    int id;
    Integer cliente_id;
    Integer empleado_id;
    Date fecha;
    String descripcion;

    
    double total;
    String metodo_pago;
    String tipo_venta;
    String estado;

    public Venta(int id, int cliente_id, int empleado_id, Date fecha, String descripcion, double total, String metodo_pago, String tipo_venta, String estado) {
        this.id = id;
        this.cliente_id = cliente_id;
        this.empleado_id = empleado_id;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.total = total;
        this.metodo_pago = metodo_pago;
        this.tipo_venta = tipo_venta;
        this.estado = estado;
    }
    
    
    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCliente_id() {
        return cliente_id;
    }

    public void setCliente_id(int cliente_id) {
        this.cliente_id = cliente_id;
    }

    public int getEmpleado_id() {
        return empleado_id;
    }

    public void setEmpleado_id(int empleado_id) {
        this.empleado_id = empleado_id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMetodo_pago() {
        return metodo_pago;
    }

    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public String getTipo_venta() {
        return tipo_venta;
    }

    public void setTipo_venta(String tipo_venta) {
        this.tipo_venta = tipo_venta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
}
