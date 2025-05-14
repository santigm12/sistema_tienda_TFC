package com.proyectobase.controlador;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.proyectobase.modelo.CodigoBarras;
import com.proyectobase.modelo.ConexionSingleton;
import com.proyectobase.modelo.Producto;
import com.proyectobase.modelo.ProductoDAO;
import com.proyectobase.modelo.Sesion;
import com.proyectobase.modelo.Usuario;
import com.proyectobase.modelo.UsuarioDAO;
import com.proyectobase.modelo.Venta;
import com.proyectobase.modelo.VentaDAO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javax.imageio.ImageIO;
import javax.management.openmbean.SimpleType;
import org.controlsfx.control.Notifications;






public class ControladorVenta implements Initializable {
    String codigoProducto = "";
    Connection conexion;
    Statement st;
    ResultSet rs;
    
    private ProductoDAO productoDAO;
    private UsuarioDAO usuarioDAO;
    private VentaDAO ventaDAO;
    ObservableList<Producto> lstProductos = FXCollections.observableArrayList();
    ObservableList<Producto> lstProductosEscaneados = FXCollections.observableArrayList();
    
    
    
    ObservableList<Usuario> lstUsuarios = FXCollections.observableArrayList();
    ObservableList<Venta> lstVentas = FXCollections.observableArrayList();
    ObservableList<Sesion> lstSesiones = FXCollections.observableArrayList();
    ObservableList<CodigoBarras> lstCodigoBarras = FXCollections.observableArrayList();
    
    
    
    
    
    @FXML
    private Button btnFinalizarCompra;

    @FXML
    private Label lblPrecio;

    @FXML
    private TableView<Producto> tablaProductos;
    
    @FXML
    private TableColumn<Producto, Integer> tc_id;

    @FXML
    private TableColumn<Producto, ImageView> tc_imagen;

    @FXML
    private TableColumn<Producto, String> tc_nombre;

    @FXML
    private TableColumn<Producto, Double> tc_precio;

    @FXML
    private TableColumn<Producto, Double> tc_precioIVA;

    @FXML
    private VBox vboxLista;

    @FXML
    private VBox vboxPrecio;
    
    @FXML
    private TextField campoCodigoOculto;
    
    @FXML
    private ImageView imagenCodigo;

    @FXML
    void finalizarCompra(ActionEvent event) {
        
        
    }
    
    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnInsertar;

    @FXML
    private Button btnProductos;

    @FXML
    private Button btnSesiones;

    @FXML
    private Button btnUsuarios;

    @FXML
    private Button btnVentas;
    
    @FXML
    private Button btnCodigoBarras;

    @FXML
    private ImageView imgBtnProductos;

    @FXML
    private ImageView imgBtnSesiones;

    @FXML
    private ImageView imgBtnUsuarios;

    @FXML
    private ImageView imgBtnVentas;


    @FXML
    private TableColumn<Producto, String> pColumnCategoria;

    @FXML
    private TableColumn<Producto, String> pColumnCodigoBarras;

    @FXML
    private TableColumn<Producto, String> pColumnDescripcion;

    @FXML
    private TableColumn<Producto, Date> pColumnFechaActualizacion;

    @FXML
    private TableColumn<Producto, Date> pColumnFechaCreacion;

    @FXML
    private TableColumn<Producto, Integer> pColumnID;

    @FXML
    private TableColumn<Producto, ImageView> pColumnImagen;

    @FXML
    private TableColumn<Producto, String> pColumnNombre;

    @FXML
    private TableColumn<Producto, Double> pColumnPrecio;

    @FXML
    private TableColumn<Producto, Double> pColumnPrecioIva;
    
    @FXML
    private TableColumn<Producto, Integer> pColumnStock;
    
    @FXML
    private Pane paneCodigoBarras;

    @FXML
    private Pane paneProductos;

    @FXML
    private Pane paneSesiones;

    @FXML
    private Pane paneUsuarios;

    @FXML
    private Pane paneVentas;


    @FXML
    private Tab tabAdmin;

    @FXML
    private TableView<Producto> tablaProductosAdmin;

    @FXML
    private TableView<Sesion> tablaSesiones;

    @FXML
    private TableView<Usuario> tablaUsuarios;

    @FXML
    private TableView<Venta> tablaVentas;

    @FXML
    private VBox vboxEditarItem;

    @FXML
    private TableColumn<Venta, Integer> vColumnCliente;

    @FXML
    private TableColumn<Venta, Integer> vColumnEmpleado;

    @FXML
    private TableColumn<Venta, String> vColumnEstado;

    @FXML
    private TableColumn<Venta, Date> vColumnFecha;

    @FXML
    private TableColumn<Venta, Integer> vColumnID;

    @FXML
    private TableColumn<Venta, String> vColumnMetodoPago;

    @FXML
    private TableColumn<Venta, String> vColumnTipoVenta;

    @FXML
    private TableColumn<Venta, Double> vColumnTotal;
    
    @FXML
    private TableColumn<Venta, String> vColumnDescripcion;
    
    @FXML
    private TableView<CodigoBarras> tablaCodigoBarras;
    
    @FXML
    private TableColumn<CodigoBarras, String> cbColumnCodigo;

    @FXML
    private TableColumn<CodigoBarras, Date> cbColumnFechaGeneracion;

    @FXML
    private TableColumn<CodigoBarras, Integer> cbColumnProductoAsignado;
    
    @FXML
    private TableColumn<CodigoBarras, Integer> cbColumnUsuarioGenerador;
    
    @FXML
    private TableColumn<Usuario, String> uColumnApellidos;

    @FXML
    private TableColumn<Usuario, String> uColumnCorreo;

    @FXML
    private TableColumn<Usuario, String> uColumnDireccion;

    @FXML
    private TableColumn<Usuario, Date> uColumnFechaRegistro;

    @FXML
    private TableColumn<Usuario, Integer> uColumnID;

    @FXML
    private TableColumn<Usuario, String> uColumnNombre;

    @FXML
    private TableColumn<Usuario, String> uColumnPermisos;

    @FXML
    private TableColumn<Usuario, String> uColumnTelefono;
    
    @FXML
    private TableColumn<Sesion, Integer> sColumnActiva;

    @FXML
    private TableColumn<Sesion, String> sColumnDispositivo;

    @FXML
    private TableColumn<Sesion, Date> sColumnFechaInicio;

    @FXML
    private TableColumn<Sesion, Integer> sColumnID;

    @FXML
    private TableColumn<Sesion, Date> sColumnUltimaActividad;

    @FXML
    private TableColumn<Sesion, Integer> sColumnUsuario;
    int identificadorTabla = 0;

    @FXML
    void actualizarItem(ActionEvent event) {
        switch(identificadorTabla){
            case 1 -> {
                String query = "UPDATE productos SET nombre=?, precio=?, stock=?, descripcion=?, imagenB64=?, categoria=? WHERE id=?";
                        try {
                            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
                            preparedStatement.setString(1, tfpNombre.getText());
                            preparedStatement.setDouble(2, Double.parseDouble(tfpPrecio.getText()));
                            preparedStatement.setInt(3, Integer.parseInt(tfpStock.getText()));
                            preparedStatement.setString(4, tfpDescripcion.getText());
                            preparedStatement.setString(5, imagenToBase64(imagenActualizar.getImage()));
                            preparedStatement.setString(6, tfpCategoria.getText());
                            preparedStatement.setInt(7, Integer.parseInt(tfpid.getText()));
                            preparedStatement.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Excepción: "+ex.getMessage());
                        }catch (IllegalArgumentException e){
                            System.out.println("El número introducido no es correcto");
                        }
                        tablaProductosAdmin.setItems(obtenerListaProductos());
            }
            
            case 2 -> {
            
            }
            
            case 3 -> {
                String query = "UPDATE usuarios SET correo=?, rol=?, nombre=?, apellido=?, telefono=?, direccion=? WHERE id=?";
                        try {
                            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
                            preparedStatement.setString(1, tfuCorreo.getText());
                            preparedStatement.setString(2, tfuPermisos.getText());
                            preparedStatement.setString(3, tfuNombre.getText());
                            preparedStatement.setString(4, tfuApellidos.getText());
                            preparedStatement.setString(5, tfuTelefono.getText());
                            preparedStatement.setString(6, tfuDireccion.getText());
                            preparedStatement.setInt(7, Integer.parseInt(tfuId.getText()));
                            preparedStatement.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Excepción: "+ex.getMessage());
                        }catch (IllegalArgumentException e){
                            System.out.println("El número introducido no es correcto");
                        }tablaUsuarios.getItems().clear();
                        tablaUsuarios.setItems(obtenerListaUsuarios());
            }
            
            case 4 -> {
                String query = "UPDATE ventas SET cliente_id=?, empleado_id=?, fecha=?, descripcion=?, total=?, metodo_pago=?, tipo_venta=?, estado=? WHERE id=?";
                int idCliente = 0;
                int idEmpleado = 0;
                        for(Usuario usuario : lstUsuarios){
                            String usuarioNombreYapellidos = usuario.getNombre()+" "+usuario.getApellido();
                            if(usuarioNombreYapellidos.equals(cbCliente.getValue())){
                                idCliente = usuario.getId();
                            }
                            
                            if(usuarioNombreYapellidos.equals(cbEmpleado.getValue())){
                                idEmpleado = usuario.getId();
                            }
                        }
                
                        try {
                            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
                            preparedStatement.setInt(1, idCliente);
                            preparedStatement.setInt(2, idEmpleado);
                            preparedStatement.setString(3, dpFecha.getValue().toString());
                            preparedStatement.setString(4, tfvDescripcion.getText());
                            preparedStatement.setDouble(5, Double.parseDouble(tfvTotal.getText()));
                            preparedStatement.setString(6, tfvMetodoPago.getText());
                            preparedStatement.setString(7, tfvTipoVenta.getText());
                            preparedStatement.setString(8, tfvEstado.getText());
                            preparedStatement.setInt(9, Integer.parseInt(tfvId.getText()));
                            preparedStatement.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Excepción: "+ex.getMessage());
                        }catch (IllegalArgumentException e){
                            System.out.println("El número introducido no es correcto");
                        }
                        tablaVentas.getItems().clear();
                        tablaVentas.setItems(obtenerListaVentas());
            }
            
            case 5 -> {
                String query = "UPDATE codigos_barras SET cliente_id=?, empleado_id=?, fecha=?, descripcion=?, total=?, metodo_pago=?, tipo_venta=?, estado=? WHERE id=?";
                int idCliente = 0;
                int idEmpleado = 0;
                        for(Usuario usuario : lstUsuarios){
                            String usuarioNombreYapellidos = usuario.getNombre()+" "+usuario.getApellido();
                            if(usuarioNombreYapellidos.equals(cbCliente.getValue())){
                                idCliente = usuario.getId();
                            }
                            
                            if(usuarioNombreYapellidos.equals(cbEmpleado.getValue())){
                                idEmpleado = usuario.getId();
                            }
                        }
                
                        try {
                            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
                            preparedStatement.setInt(1, idCliente);
                            preparedStatement.setInt(2, idEmpleado);
                            preparedStatement.setString(3, dpFecha.getValue().toString());
                            preparedStatement.setString(4, tfvDescripcion.getText());
                            preparedStatement.setDouble(5, Double.parseDouble(tfvTotal.getText()));
                            preparedStatement.setString(6, tfvMetodoPago.getText());
                            preparedStatement.setString(7, tfvTipoVenta.getText());
                            preparedStatement.setString(8, tfvEstado.getText());
                            preparedStatement.setInt(9, Integer.parseInt(tfvId.getText()));
                            preparedStatement.executeUpdate();
                        } catch (SQLException ex) {
                            System.out.println("Excepción: "+ex.getMessage());
                        }catch (IllegalArgumentException e){
                            System.out.println("El número introducido no es correcto");
                        }
                        tablaVentas.getItems().clear();
                        tablaVentas.setItems(obtenerListaVentas());
            }
        }
    }

    @FXML
    void eliminarItem(ActionEvent event) {
        switch (identificadorTabla) {
            //case 1 -> 
            //case 2 -> 
            case 3 -> {
                usuarioDAO.eliminarUsuario(tablaUsuarios.getSelectionModel().getSelectedItem());
                tablaUsuarios.getItems().clear();
                tablaUsuarios.setItems(obtenerListaUsuarios());
            }
            
            case 4 -> { 
                ventaDAO.eliminarVenta(tablaVentas.getSelectionModel().getSelectedItem()); 
                tablaVentas.getItems().clear();
                tablaVentas.setItems(obtenerListaVentas());
            }
            //case 5 -> 
        }
    }
    
    private String generarCorreoUnico(String nombre, String apellido) {
    // Validación básica
    if ((nombre == null || nombre.trim().isEmpty()) && 
        (apellido == null || apellido.trim().isEmpty())) {
        return "usuario" + System.currentTimeMillis() + "@empresa.com";
    }

    // Normalizar texto (quitar acentos y caracteres especiales)
    String nombreBase = normalizarTexto(nombre != null ? nombre.trim().split(" ")[0] : "");
    String apellidoBase = normalizarTexto(apellido != null ? apellido.trim().split(" ")[0] : "");

    // Construir base del correo
    String correoBase = (nombreBase.isEmpty() ? "user" : nombreBase.toLowerCase()) + 
                       (apellidoBase.isEmpty() ? "" : "." + apellidoBase.toLowerCase());

    // Dominio de la empresa
    String dominio = "empresa.com";

    // Generar primera versión del correo
    String correoPropuesto = correoBase + "@" + dominio;
    
    // Verificar si ya existe
    if (!existeCorreoEnLista(correoPropuesto)) {
        return correoPropuesto;
    }

    // Si existe, añadir números hasta encontrar uno único
    int contador = 1;
    while (contador < 1000) { // Límite para evitar bucles infinitos
        String correoConNumero = correoBase + contador + "@" + dominio;
        if (!existeCorreoEnLista(correoConNumero)) {
            return correoConNumero;
        }
        contador++;
    }

    // Si no se encontró uno único con números, usar timestamp
    return correoBase + System.currentTimeMillis() + "@" + dominio;
}

/**
 * Normaliza texto quitando acentos y caracteres especiales
 */
private String normalizarTexto(String texto) {
    if (texto == null || texto.isEmpty()) {
        return "";
    }
    return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "")
            .replaceAll("[^a-zA-Z0-9]", "");
}

/**
 * Verifica si un correo ya existe en la lista de usuarios
 */
private boolean existeCorreoEnLista(String correo) {
    return lstUsuarios.stream()
            .anyMatch(usuario -> usuario.getCorreo() != null && 
                                usuario.getCorreo().equalsIgnoreCase(correo));
}
        
    
    @FXML
    void insertarItem(ActionEvent event) {
        switch (identificadorTabla) {
            case 1 -> {
                try{
                    Producto productoNuevo = new Producto(
                        lstProductos.size()+1,
                        generarCodigoBarras(),
                        " ",
                        0.0,
                        0.0,
                        0,
                        " ",
                            imagenToBase64(new Image(getClass().getResource("/no_image.png").toExternalForm())),
                        " ",
                        new Date(),
                        new Date()
                    );

                    boolean exito = productoDAO.insertarProducto(productoNuevo);
                    if (exito) {
                        lstProductos.add(productoNuevo); 
                        System.out.println("Producto insertado con éxito");
                    } else {
                        System.out.println("No se pudo insertar el producto en la base de datos");
                    }

                } catch (Exception e) {
                    System.err.println("Error al insertar el producto: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            case 2 -> {
                
            }
            
            case 3 -> {
                try{
                    Usuario usuarioNuevo = new Usuario(
                        lstUsuarios.size()+1,
                            generarCorreoUnico("nombre", "apellido"),
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            new Date(),
                            0
                    );

                    boolean exito = usuarioDAO.insertarUsuario(usuarioNuevo);
                    if (exito) {
                        lstUsuarios.add(usuarioNuevo); 
                        System.out.println("Usuario insertado con éxito");
                    } else {
                        System.out.println("No se pudo insertar el usuario en la base de datos");
                    }

                } catch (Exception e) {
                    System.err.println("Error al insertar el usuario: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            case 4 -> {
                try{
                    Venta ventaNueva = new Venta(
                        lstVentas.size()+1,
                            1,
                            1,
                            new Date(),
                            "",
                            0.0,
                            "",
                            "",
                            ""
                    );

                    boolean exito = ventaDAO.insertarVenta(ventaNueva);
                    if (exito) {
                        lstVentas.add(ventaNueva); 
                        System.out.println("Usuario insertado con éxito");
                    } else {
                        System.out.println("No se pudo insertar el usuario en la base de datos");
                    }

                } catch (Exception e) {
                    System.err.println("Error al insertar el usuario: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    private TextField tfpNombre;
    private TextField tfpPrecio;
    private TextField tfpStock;
    private TextArea tfpDescripcion;
    private TextField tfpCategoria;
    private FileChooser fileChooser;
    private String imagenSeleccionadaB64;
    private TextField tfpid;
    private TextField tfpcodigoBarras;
    private TextField tfpPrecioIva;
    private TextField tfpFechaCreacion;
    private TextField tfpFechaActualizacion;
    private ImageView imagenActualizar;
    

    
    @FXML
    void verPaneProductos(ActionEvent event) {
        vboxEditarItem.setVisible(true);
        vboxEditarItem.getChildren().clear();
        identificadorTabla = 1;
        paneProductos.setVisible(true);
        paneSesiones.setVisible(false);
        paneUsuarios.setVisible(false);
        paneVentas.setVisible(false);
        paneCodigoBarras.setVisible(false);
        vboxEditarItem.getChildren().clear();

        // Inicializamos los TextField
        tfpNombre = new TextField();
        tfpPrecio = new TextField();
        tfpStock = new TextField();
        tfpDescripcion = new TextArea();
        tfpCategoria = new TextField();
        tfpid = new TextField();
        tfpcodigoBarras = new TextField();
        tfpPrecioIva = new TextField();
        tfpFechaCreacion = new TextField();
        tfpFechaActualizacion = new TextField();
        
        tfpid.setEditable(false);
        tfpcodigoBarras.setEditable(false);
        tfpPrecioIva.setEditable(false);
        tfpFechaCreacion.setEditable(false);
        tfpFechaActualizacion.setEditable(false);
        // Configuración del FileChooser (fuera del listener)
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Creamos las etiquetas y componentes
        Label titulo = new Label("Editar producto:");
        titulo.setStyle("-fx-font-size: 20px;");
        Label labelNombre = new Label("Nombre del producto:");
        Label labelPrecio = new Label("Precio:");
        Label labelStock = new Label("Stock:");
        Label labelDescripcion = new Label("Descripción:");
        Label labelImagen = new Label("Imagen:");
        Label labelCategoria = new Label("Categoría:");
        Label labelId = new Label("ID");
        Label labelCodigoBarras = new Label("Código de barras");
        Label labelPrecioIva = new Label("Precio + IVA");
        Label labelFechaCreacion = new Label("Fecha craeción");
        Label labelFechaActualizacion = new Label("Fecha actualización");

        imagenActualizar = new ImageView();
        imagenActualizar.setFitWidth(150);
        imagenActualizar.setFitHeight(150);
        imagenActualizar.setStyle("-fx-cursor: hand;"); // Cambia el cursor al pasar sobre la imagen

        // Evento para abrir el FileChooser al hacer clic en la imagen
        imagenActualizar.setOnMouseClicked(e -> {
            File file = fileChooser.showOpenDialog(paneProductos.getScene().getWindow());
            if (file != null) {
                Image nuevaImagen = new Image(file.toURI().toString());
                imagenActualizar.setImage(nuevaImagen);
                imagenSeleccionadaB64 = imagenToBase64(nuevaImagen);

                // Actualiza la imagen en el producto seleccionado (si hay uno)
                Producto selectedProduct = tablaProductosAdmin.getSelectionModel().getSelectedItem();
                if (selectedProduct != null) {
                    selectedProduct.setImagenB64(imagenSeleccionadaB64);
                }
            }
        });

        // Añadimos los componentes al VBox
        vboxEditarItem.getChildren().addAll(
             titulo,labelId, tfpid, labelCodigoBarras, tfpcodigoBarras, labelNombre, tfpNombre, labelPrecio,tfpPrecio, labelPrecioIva ,tfpPrecioIva, 
            labelStock, tfpStock, labelDescripcion, tfpDescripcion, 
            labelImagen, imagenActualizar, labelCategoria, tfpCategoria,  
            labelFechaCreacion , tfpFechaCreacion, labelFechaActualizacion , tfpFechaActualizacion
        );
        VBox.setMargin(titulo, new Insets(0, 0, 20, 0));

        vboxEditarItem.setPadding(new Insets(20, 20, 20, 20));
        vboxEditarItem.setSpacing(10);
        vboxEditarItem.setPrefWidth(Region.USE_COMPUTED_SIZE);

        // Listener para actualizar campos al seleccionar un producto (sin FileChooser aquí)
        tablaProductosAdmin.getSelectionModel().selectedItemProperty().addListener((observable, oldProduct, selectedProduct) -> {
            if (selectedProduct != null) {
                System.out.println("Producto seleccionado: " + selectedProduct.getNombre());

                tfpNombre.setText(selectedProduct.getNombre());
                tfpPrecio.setText(String.valueOf(selectedProduct.getPrecio()));
                tfpStock.setText(String.valueOf(selectedProduct.getStock()));
                tfpDescripcion.setText(selectedProduct.getDescripcion());
                tfpCategoria.setText(selectedProduct.getCategoria());
                tfpid.setText(String.valueOf(selectedProduct.getId()));
                tfpcodigoBarras.setText(selectedProduct.getCodigo_barras());
                tfpPrecioIva.setText(String.valueOf(selectedProduct.getPrecio_con_iva()));
                tfpFechaActualizacion.setText(String.valueOf(selectedProduct.getFecha_actualizacion()));
                tfpFechaCreacion.setText(String.valueOf(selectedProduct.getFecha_creacion()));

                // Cargar imagen actual del producto
                if (selectedProduct.getImagenB64() != null && !selectedProduct.getImagenB64().isEmpty()) {
                    imagenActualizar.setImage(base64ToImage(selectedProduct.getImagenB64()));
                } else {
                    imagenActualizar.setImage(null); // Limpiar si no hay imagen
                }
            }
        });
    }
    
    
    @FXML
    void verPaneSesion(ActionEvent event) {
        paneProductos.setVisible(false);
        paneSesiones.setVisible(true);
        paneUsuarios.setVisible(false);
        paneVentas.setVisible(false);
        paneCodigoBarras.setVisible(false);
        
        vboxEditarItem.getChildren().clear();
        vboxEditarItem.setVisible(true);
        identificadorTabla = 2;
        
    }
    private TextField tfuId;
    private TextField tfuCorreo;
    private TextField tfuPermisos;
    private TextField tfuNombre;
    private TextField tfuApellidos;
    private TextField tfuTelefono;
    private TextField tfuDireccion;
    private TextField tfuFechaRegistro;
    
    @FXML
    void verPaneUsuarios(ActionEvent event) {
        paneProductos.setVisible(false);
        paneSesiones.setVisible(false);
        paneUsuarios.setVisible(true);
        paneVentas.setVisible(false);
        paneCodigoBarras.setVisible(false);
        vboxEditarItem.getChildren().clear();
        vboxEditarItem.setVisible(true);
        identificadorTabla = 3;
        
        tfuId = new TextField();
        tfuCorreo = new TextField();
        tfuPermisos = new TextField();
        tfuNombre = new TextField();
        tfuApellidos = new TextField();
        tfuTelefono = new TextField();
        tfuDireccion = new TextField();
        tfuFechaRegistro = new TextField();
        tfuId.setEditable(false);
        tfuFechaRegistro.setEditable(false);
        
        Label titulo = new Label("Editar usuario:");
        titulo.setStyle("-fx-font-size: 20px;");
        Label labelId = new Label("ID:");
        Label labelCorreo = new Label("Correo:");
        Label labelPermisos = new Label("Permisos:");
        Label labelNombre = new Label("Nombre:");
        Label labelApellidos = new Label("Apellidos:");
        Label labelTelefono = new Label("Teléfono:");
        Label labelDireccion = new Label("Dirección:");
        Label labelFechaRegistro = new Label("Fecha de registro:");
        
        vboxEditarItem.getChildren().addAll(titulo,
                labelId, tfuId, labelCorreo, tfuCorreo, labelPermisos, tfuPermisos,
                labelNombre, tfuNombre, labelApellidos, tfuApellidos, labelTelefono,
                tfuTelefono, labelDireccion, tfuDireccion, labelFechaRegistro, tfuFechaRegistro
        );
        VBox.setMargin(titulo, new Insets(0, 0, 20, 0));

        vboxEditarItem.setPadding(new Insets(20, 20, 20, 20));
        vboxEditarItem.setSpacing(10);
        vboxEditarItem.setPrefWidth(Region.USE_COMPUTED_SIZE);

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((observable, oldUsuario, selectedUsuario) -> {
            if (selectedUsuario != null) {

                tfuId.setText(String.valueOf(selectedUsuario.getId()));
                tfuCorreo.setText(String.valueOf(selectedUsuario.getCorreo()));
                tfuPermisos.setText(String.valueOf(selectedUsuario.getRol()));
                tfuNombre.setText(selectedUsuario.getNombre());
                tfuApellidos.setText(selectedUsuario.getApellido());
                tfuTelefono.setText(String.valueOf(selectedUsuario.getTelefono()));
                tfuDireccion.setText(selectedUsuario.getDireccion());
                tfuFechaRegistro.setText(String.valueOf(selectedUsuario.getFecha_registro()));
            }
        });
    }
    private TextField tfvId;
    private ComboBox<String> cbCliente;
    private ComboBox<String> cbEmpleado;
    private DatePicker dpFecha;
    private TextField tfvDescripcion;
    private TextField tfvTotal;
    private TextField tfvMetodoPago;
    private TextField tfvTipoVenta;
    private TextField tfvEstado;
    
    @FXML
    void verPaneVentas(ActionEvent event) {
        paneProductos.setVisible(false);
        paneSesiones.setVisible(false);
        paneUsuarios.setVisible(false);
        paneVentas.setVisible(true);
        paneCodigoBarras.setVisible(false);
        
        vboxEditarItem.getChildren().clear();
        vboxEditarItem.setVisible(true);
        identificadorTabla = 4;
        
        tfvId = new TextField();
        cbCliente = new ComboBox();
        cbEmpleado = new ComboBox();
        dpFecha = new DatePicker();
        tfvDescripcion = new TextField();
        tfvTotal = new TextField();
        tfvMetodoPago = new TextField();
        tfvTipoVenta = new TextField();
        tfvTipoVenta = new TextField();
        tfvEstado = new TextField();
        tfvId.setEditable(false);
        
        Label titulo = new Label("Editar venta: ");
        titulo.setStyle("-fx-font-size: 20px;");
        Label labelId = new Label("ID:");
        Label labelCliente = new Label ("Cliente:");
        Label labelEmpleado = new Label ("Empleado");
        Label labelFecha = new Label ("Fecha:");
        Label labelDescripcion = new Label("Descripción:");
        Label labelTotal = new Label("Total:");
        Label labelMetodoPago = new Label("Método de pago:");
        Label labelTipoVenta = new Label("Tipo de venta:");
        Label labelEstado = new Label ("Estado:");
        
        vboxEditarItem.getChildren().addAll(titulo, labelId, tfvId, labelCliente, cbCliente,
                labelEmpleado, cbEmpleado, labelFecha, dpFecha, labelDescripcion, tfvDescripcion,
                labelTotal, tfvTotal, labelMetodoPago, tfvMetodoPago, labelTipoVenta, tfvTipoVenta,
                labelEstado, tfvEstado);
        
        VBox.setMargin(titulo, new Insets(0, 0, 20, 0));

        vboxEditarItem.setPadding(new Insets(20, 20, 20, 20));
        vboxEditarItem.setSpacing(10);
        vboxEditarItem.setPrefWidth(Region.USE_COMPUTED_SIZE);
        List<String> listaClientes = new ArrayList();
        List<String> listaEmpleados = new ArrayList();
        for(int i = 0; i<lstUsuarios.size(); i++){
            if(lstUsuarios.get(i).getRol().equals("Cliente") || lstUsuarios.get(i).getRol().equals("cliente")){
                listaClientes.add(lstUsuarios.get(i).getNombre()+" "+lstUsuarios.get(i).getApellido());
            }else{
                listaEmpleados.add(lstUsuarios.get(i).getNombre()+" "+lstUsuarios.get(i).getApellido());
            }
        }
        
        cbCliente.getItems().addAll(listaClientes);
        cbEmpleado.getItems().addAll(listaEmpleados);
        
        tablaVentas.getSelectionModel().selectedItemProperty().addListener((observable, oldVenta, selectedVenta) -> {
            if (selectedVenta != null) {

                tfvId.setText(String.valueOf(selectedVenta.getId()));
                cbCliente.getItems().addAll();
                for(int i = 0; i<lstUsuarios.size(); i++){
                    if(lstUsuarios.get(i).getId() == selectedVenta.getCliente_id()){
                        cbCliente.setValue(lstUsuarios.get(i).getNombre()+" "+lstUsuarios.get(i).getApellido());
                    }
                    
                    if(lstUsuarios.get(i).getId() == selectedVenta.getEmpleado_id()){
                        cbEmpleado.setValue(lstUsuarios.get(i).getNombre()+" "+lstUsuarios.get(i).getApellido());
                    }
                }
                System.out.println(selectedVenta.getFecha().toString());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                dpFecha.setValue(LocalDate.parse(selectedVenta.getFecha().toString(), formatter));
                tfvDescripcion.setText(selectedVenta.getDescripcion());
                tfvTotal.setText(String.valueOf(selectedVenta.getTotal()));
                tfvMetodoPago.setText(selectedVenta.getMetodo_pago());
                tfvTipoVenta.setText(String.valueOf(selectedVenta.getTipo_venta()));
                tfvEstado.setText(selectedVenta.getEstado());
            }
        });
    }
    
    private TextField tfcCodigo;
    private TextField tfcProductoAsignado;
    private TextField tfcFechaGeneracion;
    private TextField tfcUsuarioGenerador;
    private ImageView imagenCB;
    private Button btnDescargar;
    
    
    
    
    @FXML
private void descargarImagen() {
    if (imagenCB == null) {
        return;
    }

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Guardar código de barras");
    fileChooser.setInitialFileName("codigo_barras.png");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Imagen PNG", "*.png"),
        new FileChooser.ExtensionFilter("Imagen JPEG", "*.jpg", "*.jpeg")
    );

    File archivo = fileChooser.showSaveDialog(btnDescargar.getScene().getWindow());
    
    if (archivo != null) {
        try {
            // Asegurar la extensión correcta
            String nombreArchivo = archivo.getName().toLowerCase();
            String formato;
            
            if (nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg")) {
                formato = "jpg";
            } else {
                // Por defecto usamos PNG
                if (!nombreArchivo.endsWith(".png")) {
                    archivo = new File(archivo.getAbsolutePath() + ".png");
                }
                formato = "png";
            }

            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imagenCB.getImage(), null);
            

            // Guardar la imagen
            boolean exito = ImageIO.write(bufferedImage, formato, archivo);
            
           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    
    @FXML
    void verPaneCodigoBarras(ActionEvent event) {
        paneProductos.setVisible(false);
        paneSesiones.setVisible(false);
        paneUsuarios.setVisible(false);
        paneVentas.setVisible(false);
        paneCodigoBarras.setVisible(true);
        
        vboxEditarItem.getChildren().clear();
        vboxEditarItem.setVisible(true);
        identificadorTabla = 5;
        
        tfcCodigo = new TextField();
        tfcProductoAsignado = new TextField();
        tfcFechaGeneracion = new TextField();
        tfcUsuarioGenerador = new TextField();
        imagenCB = new ImageView();
        
        
        
        Label titulo = new Label("Información código de barras: ");
        titulo.setStyle("-fx-font-size: 20px;");
        Label labelCodigo = new Label("Codigo: ");
        Label labelProducto = new Label("Producto asignado: ");
        Label labelFechaGen = new Label ("Fecha generación: ");
        Label labelUsuarioGen = new Label("Usuario generador: ");
        Label labelImagen = new Label("Imagen: ");
        
        vboxEditarItem.getChildren().addAll(titulo, labelCodigo, tfcCodigo,
                labelProducto, tfcProductoAsignado, labelFechaGen, tfcFechaGeneracion,
                labelUsuarioGen, tfcUsuarioGenerador, labelImagen, imagenCB, btnDescargar);
        
        VBox.setMargin(titulo, new Insets(0, 0, 20, 0));

        vboxEditarItem.setPadding(new Insets(20, 20, 20, 20));
        vboxEditarItem.setSpacing(10);
        vboxEditarItem.setPrefWidth(Region.USE_COMPUTED_SIZE);
        
        tablaCodigoBarras.getSelectionModel().selectedItemProperty().addListener((observable, oldVenta, selectedCodigo) -> {
            if (selectedCodigo != null) {
                
                tfcCodigo.setText(selectedCodigo.getCodigo());
                tfcFechaGeneracion.setText(String.valueOf(selectedCodigo.getFecha_generacion()));
                tfcProductoAsignado.setText(String.valueOf(selectedCodigo.getProducto_asignado()));
                tfcUsuarioGenerador.setText(String.valueOf(selectedCodigo.getUsuario_generador()));
                imagenCB.setImage(generarImagenCodigoBarras(selectedCodigo.getCodigo()));
                
            }
        });
    }
    
    public static String generarCodigoBarras() {
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();
        
        for (int i = 0; i < 12; i++) {
            int digito = random.nextInt(10); 
            codigo.append(digito);
        }

        int suma = 0;
        for (int i = 0; i < codigo.length(); i++) {
            int valor = Character.getNumericValue(codigo.charAt(i));
            suma += (i % 2 == 0) ? valor * 1 : valor * 3;
        }

        int digitoControl = (10 - (suma % 10)) % 10;
        codigo.append(digitoControl);

        return codigo.toString();
    }
    
    
        public Image generarImagenCodigoBarras(String data) {
        try {
            // Crear la matriz de bits del código de barras
            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(data, BarcodeFormat.CODE_128, 300, 150);

            // Crear imagen escribible con las dimensiones del código de barras
            WritableImage writableImage = new WritableImage(
                bitMatrix.getWidth(), 
                bitMatrix.getHeight()
            );

            PixelWriter pixelWriter = writableImage.getPixelWriter();

            // Rellenar la imagen con píxeles blancos y negros según la matriz
            for (int y = 0; y < bitMatrix.getHeight(); y++) {
                for (int x = 0; x < bitMatrix.getWidth(); x++) {
                    Color color = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                    pixelWriter.setColor(x, y, color);
                }
            }

            return writableImage;

        } catch (Exception e) {
            System.err.println("Error al generar código de barras: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String imagenToBase64(Image image) {
        try {
            BufferedImage bufferedImage = javafx.embed.swing.SwingFXUtils.fromFXImage(image, null);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            return base64Image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Image base64ToImage(String base64String) {
        if (base64String == null || base64String.trim().isEmpty()) {
            return null;
        }
        try {
            String pureBase64 = base64String.split(",")[base64String.split(",").length - 1];
            byte[] decodedBytes = Base64.getDecoder().decode(pureBase64);
            return new Image(new ByteArrayInputStream(decodedBytes));
        } catch (Exception e) {
            System.err.println("Error al decodificar imagen Base64: " + e.getMessage());
            return null;
        }
    }
        
    ///////////////////////////////////////////////////////////////////////////////////Consulta a base de datos /////////////////////////////////////////////////////////////////////////////
        
        
    private ObservableList obtenerListaProductos() {
        if (conexion != null) {
            lstProductos.clear();
            String query = "SELECT * FROM productos";
            try {
                rs = st.executeQuery(query);
                Producto pro;
                while (rs.next()) { 
                    pro = new Producto(
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
                            rs.getDate("fecha_actualizacion"));
                    lstProductos.add(pro);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: "+e.getMessage());
            }
            return lstProductos;
        }
        return null;
    }
    
    private ObservableList obtenerListaUsuarios() {
        if (conexion != null) {
            String query = "SELECT * FROM usuarios";
            try {
                rs = st.executeQuery(query);
                Usuario usu;
                while (rs.next()) { 
                    usu = new Usuario(
                            rs.getInt("id"), 
                            rs.getString("correo"),
                            rs.getString("password_hash"),
                            rs.getString("rol"), 
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("telefono"),
                            rs.getString("direccion"),
                            rs.getDate("fecha_registro"),
                            rs.getInt("activo"));
                    lstUsuarios.add(usu);
                    
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: "+e.getMessage());
            }
            return lstUsuarios;
        }
        return null;
    }
    
    private ObservableList obtenerListaVentas() {
        if (conexion != null) {
            String query = "SELECT * FROM ventas";
            try {
                rs = st.executeQuery(query);
                Venta ven;
                while (rs.next()) { 
                    ven = new Venta(
                            rs.getInt("id"), 
                            rs.getInt("cliente_id"),
                            rs.getInt("empleado_id"),
                            rs.getDate("fecha"), 
                            rs.getString("descripcion"),
                            rs.getDouble("total"),
                            rs.getString("metodo_pago"),
                            rs.getString("tipo_venta"),
                            rs.getString("estado"));
                    lstVentas.add(ven);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: "+e.getMessage());
            }
            return lstVentas;
        }
        return null;
    }
    
    private ObservableList obtenerListaSesiones() {
        if (conexion != null) {
            String query = "SELECT * FROM sesiones";
            try {
                rs = st.executeQuery(query);
                Sesion ses;
                while (rs.next()) { 
                    ses = new Sesion(
                            rs.getInt("id"), 
                            rs.getInt("usuario_id"),
                            rs.getString("dispositivo"),
                            rs.getDate("fecha_inicio"), 
                            rs.getDate("fecha_ultima_actividad"),
                            rs.getInt("activa"));
                    lstSesiones.add(ses);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: "+e.getMessage());
            }
            return lstSesiones;
        }
        return null;
    }
    
    private ObservableList obtenerListaCodigoBarras() {
        if (conexion != null) {
            String query = "SELECT * FROM codigos_barras";
            try {
                rs = st.executeQuery(query);
                CodigoBarras cb;
                while (rs.next()) { 
                    cb = new CodigoBarras(
                            rs.getString("codigo"), 
                            rs.getInt("producto_asignado"),
                            rs.getDate("fecha_generacion"),
                            rs.getInt("usuario_generador"));
                    lstCodigoBarras.add(cb);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: "+e.getMessage());
            }
            return lstCodigoBarras;
        }
        return null;
    }
    
    
    public void inicializarTablasProductos() {
        try {
            tc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tc_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            tc_precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
            tc_precioIVA.setCellValueFactory(new PropertyValueFactory<>("precio_con_iva"));
            tc_imagen.setCellValueFactory(cellData -> {
                String base64 = cellData.getValue().getImagenB64();
                ImageView imageView = new ImageView();

                if (base64 != null && !base64.isBlank()) {
                    Image image = base64ToImage(base64);
                    if (image != null) {
                        imageView.setImage(image);
                        imageView.setFitHeight(60);
                        imageView.setFitWidth(60);
                        imageView.setPreserveRatio(true);
                        System.out.println("entra en el inicializar");
                    } else {
                        System.out.println("Error al convertir la imagen del producto ID: " + cellData.getValue().getId());
                    }
                }
                return new SimpleObjectProperty<>(imageView);
            });
            tablaProductos.setItems(lstProductosEscaneados);

        } catch (Exception ex) {
            System.out.println("Error en inicializarTablasProductos: " + ex.getMessage());
        }
    }
    
    public void inicializarTablasProductosAdmin() {
        try {
            pColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
            pColumnCodigoBarras.setCellValueFactory(new PropertyValueFactory<>("codigo_barras"));
            pColumnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            pColumnPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
            pColumnPrecioIva.setCellValueFactory(new PropertyValueFactory<>("precio_con_iva"));
            pColumnStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
            pColumnDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
            pColumnCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
            pColumnFechaCreacion.setCellValueFactory(new PropertyValueFactory<>("fecha_creacion"));
            pColumnFechaActualizacion.setCellValueFactory(new PropertyValueFactory<>("fecha_actualizacion"));
            pColumnImagen.setCellValueFactory(cellData -> {
                String base64 = cellData.getValue().getImagenB64();
                Image image = base64ToImage(base64);

                ImageView imageView = new ImageView();
                if (image != null) {
                    imageView.setImage(image);
                    imageView.setFitHeight(60);
                    imageView.setFitWidth(60);
                    imageView.setPreserveRatio(true);
                } 
                return new SimpleObjectProperty<>(imageView);
            });
            tablaProductosAdmin.setItems(lstProductos);
        } catch (Exception ex) {
            System.out.println("Error en inicializarTablasProductos: " + ex.getMessage());
        }
    }
    
    
    public void inicializarTablaUsuarios() {
        try {
            uColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
            uColumnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            uColumnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellido"));
            uColumnCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
            uColumnTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
            uColumnDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
            uColumnFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fecha_registro"));
            uColumnPermisos.setCellValueFactory(new PropertyValueFactory<>("rol"));
            
            tablaUsuarios.setItems(lstUsuarios);

        } catch (Exception ex) {
            System.out.println("Error en inicializarTablaUsuarios: " + ex.getMessage());
        }
    }
    
    
    public void inicializarTablaVentas() {
        try {
            vColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
            vColumnCliente.setCellValueFactory(new PropertyValueFactory<>("cliente_id"));
            vColumnEmpleado.setCellValueFactory(new PropertyValueFactory<>("empleado_id"));
            vColumnFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            vColumnDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
            vColumnTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
            vColumnMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodo_pago"));
            vColumnTipoVenta.setCellValueFactory(new PropertyValueFactory<>("tipo_venta"));
            vColumnEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
            
            tablaVentas.setItems(lstVentas);

        } catch (Exception ex) {
            System.out.println("Error en inicializarTablaUsuarios: " + ex.getMessage());
        }
    }
    
    public void inicializarTablaSesiones() {
        try {
            sColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
            sColumnUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario_id"));
            sColumnDispositivo.setCellValueFactory(new PropertyValueFactory<>("dispositivo"));
            sColumnFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fecha_inicio"));
            sColumnUltimaActividad.setCellValueFactory(new PropertyValueFactory<>("fecha_ultima_actividad"));
            sColumnActiva.setCellValueFactory(new PropertyValueFactory<>("activa"));
            
            tablaSesiones.setItems(lstSesiones);

        } catch (Exception ex) {
            System.out.println("Error en inicializarTablaUsuarios: " + ex.getMessage());
        }
    }
    
    public void inicializarTablaCodigosBarras() {
        try {
            cbColumnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            cbColumnFechaGeneracion.setCellValueFactory(new PropertyValueFactory<>("fecha_generacion"));
            cbColumnProductoAsignado.setCellValueFactory(new PropertyValueFactory<>("producto_asignado"));
            cbColumnUsuarioGenerador.setCellValueFactory(new PropertyValueFactory<>("usuario_generador"));
            
            tablaCodigoBarras.setItems(lstCodigoBarras);

        } catch (Exception ex) {
            System.out.println("Error en inicializarTablaUsuarios: " + ex.getMessage());
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void abrirVentanaImagen(String imagenB64) {
    try {
        Stage ventana = new Stage();
        ventana.setTitle("Imagen Ampliada");
        
        ImageView imageViewAmpliada = new ImageView();
        imageViewAmpliada.setPreserveRatio(true);
        imageViewAmpliada.setFitWidth(600);
        
        byte[] imageData = Base64.getDecoder().decode(imagenB64);
        Image image = new Image(new ByteArrayInputStream(imageData));
        imageViewAmpliada.setImage(image);
        
        StackPane root = new StackPane(imageViewAmpliada);
        Scene scene = new Scene(root);
        
        ventana.setScene(scene);
        ventana.show();
    } catch (Exception e) {
        System.err.println("Error al mostrar imagen: " + e.getMessage());
    }
}
    
    double totalCompra = 0;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vboxEditarItem.setVisible(false);
        btnDescargar = new Button("Descargar");
        btnDescargar.setOnAction(event -> descargarImagen());
        
        try {
            conexion = ConexionSingleton.obtenerConexion();
            this.productoDAO = new ProductoDAO(conexion);
            this.usuarioDAO = new UsuarioDAO(conexion);
            this.ventaDAO = new VentaDAO(conexion);
            if (conexion != null) {
                this.st = conexion.createStatement();
            }

            System.out.println("Se ha conectado a la base de datos");
        } catch (SQLException e) {
            System.out.println("No se ha conectado a la base de datos");
        }
        
        obtenerListaProductos();
        obtenerListaUsuarios();
        obtenerListaVentas();
        obtenerListaSesiones();
        obtenerListaCodigoBarras();
        inicializarTablasProductos();
        inicializarTablaUsuarios();
        inicializarTablaVentas();
        inicializarTablaSesiones();
        inicializarTablasProductosAdmin();
        inicializarTablaCodigosBarras();
        
        Image imagen;
        /*try {
            imagen = new Image(new FileInputStream("C:/Users/santi/Desktop/imagen.png"));
            System.out.println(imagenToBase64(imagen));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ControladorVenta.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        
        
        Platform.runLater(() -> campoCodigoOculto.requestFocus());
        //System.out.println(lstProductos.size());
        
        campoCodigoOculto.setOnAction(event -> {
            String codigo = campoCodigoOculto.getText();
            for(int i = 0; i<lstProductos.size(); i++){
                if(lstProductos.get(i).getCodigo_barras().equals(codigo)){
                    lstProductosEscaneados.add(lstProductos.get(i));
                    tablaProductos.setItems(lstProductosEscaneados);
                    totalCompra+=lstProductos.get(i).getPrecio();
                    lblPrecio.setText(""+totalCompra);
                    System.out.println("producto encontrado");
                    break;
                }
            }
            System.out.println("Código de barras escaneado: " + codigo);

            campoCodigoOculto.clear(); // Limpiar para el próximo escaneo
        });
        /*Platform.runLater(() -> {
            Scene scene = vboxPrecio.getScene(); 
            if (scene != null) {
                scene.setOnKeyPressed(event -> {
                    codigoProducto.concat(event.getText());
                });
            }
        });*/
        System.out.println(codigoProducto);
    }
}
