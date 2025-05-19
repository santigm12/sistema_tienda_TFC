package com.proyectobase.modelo;

import javafx.beans.property.*;
import java.util.Date;

public class Usuario {
    private final IntegerProperty id;
    private final StringProperty correo;
    private final StringProperty password_hash;
    private final StringProperty rol;
    private final StringProperty nombre;
    private final StringProperty apellido;
    private final StringProperty telefono;
    private final StringProperty direccion;
    private final ObjectProperty<Date> fecha_registro;
    private final IntegerProperty activo;
    
    public Usuario(int id, String correo, String password_hash, String rol, String nombre, 
                  String apellido, String telefono, String direccion, Date fecha_registro, int activo) {
        this.id = new SimpleIntegerProperty(id);
        this.correo = new SimpleStringProperty(correo);
        this.password_hash = new SimpleStringProperty(password_hash);
        this.rol = new SimpleStringProperty(rol);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido = new SimpleStringProperty(apellido);
        this.telefono = new SimpleStringProperty(telefono);
        this.direccion = new SimpleStringProperty(direccion);
        this.fecha_registro = new SimpleObjectProperty<>(fecha_registro);
        this.activo = new SimpleIntegerProperty(activo);
    }

    // Getters y setters normales
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getCorreo() { return correo.get(); }
    public void setCorreo(String correo) { this.correo.set(correo); }
    public StringProperty correoProperty() { return correo; }

    public String getPassword_hash() { return password_hash.get(); }
    public void setPassword_hash(String password_hash) { this.password_hash.set(password_hash); }
    public StringProperty password_hashProperty() { return password_hash; }

    public String getRol() { return rol.get(); }
    public void setRol(String rol) { this.rol.set(rol); }
    public StringProperty rolProperty() { return rol; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public StringProperty nombreProperty() { return nombre; }

    public String getApellido() { return apellido.get(); }
    public void setApellido(String apellido) { this.apellido.set(apellido); }
    public StringProperty apellidoProperty() { return apellido; }

    public String getTelefono() { return telefono.get(); }
    public void setTelefono(String telefono) { this.telefono.set(telefono); }
    public StringProperty telefonoProperty() { return telefono; }

    public String getDireccion() { return direccion.get(); }
    public void setDireccion(String direccion) { this.direccion.set(direccion); }
    public StringProperty direccionProperty() { return direccion; }

    public Date getFecha_registro() { return fecha_registro.get(); }
    public void setFecha_registro(Date fecha_registro) { this.fecha_registro.set(fecha_registro); }
    public ObjectProperty<Date> fecha_registroProperty() { return fecha_registro; }

    public int getActivo() { return activo.get(); }
    public void setActivo(int activo) { this.activo.set(activo); }
    public IntegerProperty activoProperty() { return activo; }
}