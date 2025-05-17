package com.proyectobase.modelo;

import java.time.LocalDateTime;
import javafx.beans.property.*;
import java.util.Date;

/**
 *
 * @author Santiago
 */
public class Producto {
    private final IntegerProperty id;
    private final StringProperty codigo_barras;
    private final StringProperty nombre;
    private final DoubleProperty precio;
    private final DoubleProperty precio_con_iva;
    private final IntegerProperty stock;
    private final StringProperty descripcion;
    private final StringProperty imagenB64;
    private final StringProperty categoria;
    private final ObjectProperty<Date> fecha_creacion;
    private final ObjectProperty<Date> fecha_actualizacion;

    

    public Producto(int id, String codigo_barras, String nombre, double precio, double precio_con_iva, int stock,
                    String descripcion, String imagenB64, String categoria, Date fecha_creacion, Date fecha_actualizacion) {
        this.id = new SimpleIntegerProperty(id);
        this.codigo_barras = new SimpleStringProperty(codigo_barras);
        this.nombre = new SimpleStringProperty(nombre);
        this.precio = new SimpleDoubleProperty(precio);
        this.precio_con_iva = new SimpleDoubleProperty(precio_con_iva);
        this.stock = new SimpleIntegerProperty(stock);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.imagenB64 = new SimpleStringProperty(imagenB64);
        this.categoria = new SimpleStringProperty(categoria);
        this.fecha_creacion = new SimpleObjectProperty<>(fecha_creacion);
        this.fecha_actualizacion = new SimpleObjectProperty<>(fecha_actualizacion);
    }
    

    // Getters y setters normales
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getCodigo_barras() { return codigo_barras.get(); }
    public void setCodigo_barras(String codigo_barras) { this.codigo_barras.set(codigo_barras); }
    public StringProperty codigo_barrasProperty() { return codigo_barras; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public StringProperty nombreProperty() { return nombre; }

    public double getPrecio() { return precio.get(); }
    public void setPrecio(double precio) { this.precio.set(precio); }
    public DoubleProperty precioProperty() { return precio; }

    public double getPrecio_con_iva() { return precio_con_iva.get(); }
    public void setPrecio_con_iva(double precio_con_iva) { this.precio_con_iva.set(precio_con_iva); }
    public DoubleProperty precio_con_ivaProperty() { return precio_con_iva; }

    public int getStock() { return stock.get(); }
    public void setStock(int stock) { this.stock.set(stock); }
    public IntegerProperty stockProperty() { return stock; }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
    public StringProperty descripcionProperty() { return descripcion; }

    public String getImagenB64() { return imagenB64.get(); }
    public void setImagenB64(String imagenB64) { this.imagenB64.set(imagenB64); }
    public StringProperty imagenB64Property() { return imagenB64; }

    public String getCategoria() { return categoria.get(); }
    public void setCategoria(String categoria) { this.categoria.set(categoria); }
    public StringProperty categoriaProperty() { return categoria; }

    public Date getFecha_creacion() { return fecha_creacion.get(); }
    public void setFecha_creacion(Date fecha_creacion) { this.fecha_creacion.set(fecha_creacion); }
    public ObjectProperty<Date> fecha_creacionProperty() { return fecha_creacion; }

    public Date getFecha_actualizacion() { return fecha_actualizacion.get(); }
    public void setFecha_actualizacion(Date fecha_actualizacion) { this.fecha_actualizacion.set(fecha_actualizacion); }
    public ObjectProperty<Date> fecha_actualizacionProperty() { return fecha_actualizacion;}
    
    @Override
    public String toString() {
        return nombre.get();
    }
}
   