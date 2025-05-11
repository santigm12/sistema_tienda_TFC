package com.proyectobase.modelo;

import java.util.Date;

/**
 *
 * @author Santiago
 */
public class CodigoBarras {
    String codigo;
    int producto_asignado;

    
    Date fecha_generacion;
    int usuario_generador;

    public CodigoBarras(String codigo, int producto_asignado, Date fecha_generacion, int usuario_generador) {
        this.codigo = codigo;
        this.producto_asignado = producto_asignado;
        this.fecha_generacion = fecha_generacion;
        this.usuario_generador = usuario_generador;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getProducto_asignado() {
        return producto_asignado;
    }

    public void setProducto_asignado(int producto_asignado) {
        this.producto_asignado = producto_asignado;
    }

    public Date getFecha_generacion() {
        return fecha_generacion;
    }

    public void setFecha_generacion(Date fecha_generacion) {
        this.fecha_generacion = fecha_generacion;
    }

    public int getUsuario_generador() {
        return usuario_generador;
    }

    public void setUsuario_generador(int usuario_generador) {
        this.usuario_generador = usuario_generador;
    }
    
    
    
    
}
