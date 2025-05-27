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
public class Sesion {
    int id;
    int usuario_id;
    String dispositivo;
    Date fecha_inicio;
    Date fecha_ultima_actividad;
    int activa;

    public Sesion(int id, int usuario_id, String dispositivo, Date fecha_inicio, Date fecha_ultima_actividad, int activa) {
        this.id = id;
        this.usuario_id = usuario_id;
        this.dispositivo = dispositivo;
        this.fecha_inicio = fecha_inicio;
        this.fecha_ultima_actividad = fecha_ultima_actividad;
        this.activa = activa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFecha_ultima_actividad() {
        return fecha_ultima_actividad;
    }

    public void setFecha_ultima_actividad(Date fecha_ultima_actividad) {
        this.fecha_ultima_actividad = fecha_ultima_actividad;
    }

    public int getActiva() {
        return activa;
    }

    public void setActiva(int activa) {
        this.activa = activa;
    }
}
