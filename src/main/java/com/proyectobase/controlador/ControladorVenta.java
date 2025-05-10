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
import com.proyectobase.modelo.Venta;
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
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
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
    private TableView<CodigoBarras> tablaCodigoBarras;
    
    @FXML
    private TableColumn<CodigoBarras, String> cbColumnCodigo;

    @FXML
    private TableColumn<CodigoBarras, Date> cbColumnFechaGeneracion;

    @FXML
    private TableColumn<CodigoBarras, Integer> cbColumnUtilizado;
    
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
            
            }
            
            case 5 -> {
            
            }
        }
    }

    @FXML
    void eliminarItem(ActionEvent event) {
        /*try{
            clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
            String mensajeEliminar = "";
            for(Venta v  : obtenerListaVentas()){
                if(v.getIdCliente() == clienteSeleccionado.getIdCliente()){
                    mensajeEliminar = "Este cliente tiene ventas asociadas ¿desea eliminar las ventas y el cliente con id ";
                    break;
                }else{
                    mensajeEliminar = "¿Desea eliminar el cliente con id ";
                }
            }      
            Alert alertaSalir = new Alert(Alert.AlertType.CONFIRMATION, mensajeEliminar+clienteSeleccionado.getIdCliente()+" ("+clienteSeleccionado.getNombre()+" "+clienteSeleccionado.getApellidos()+")?", ButtonType.NO, ButtonType.YES);
            alertaSalir.setTitle("Eliminar");
            alertaSalir.setHeaderText(null);
            alertaSalir.showAndWait().ifPresent(response ->{
                if(response == ButtonType.YES){
                    if(clienteSeleccionado == null){
                        alerta.show();
                    }else{
                        String query = "DELETE FROM Cliente WHERE id_cliente=?";
                        try {
                            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
                            preparedStatement.setInt(1, tablaClientes.getSelectionModel().getSelectedItem().getIdCliente());
                            preparedStatement.executeUpdate();
                            Notifications.create() 
                                .title("Eliminar") 
                                .text("El cliente se ha eliminado correctamente")
                                .graphic(iconoBien)
                                .owner(stackPane)
                                .show();
                        } catch (SQLException e) {
                            System.out.println("Excepción: "+e.getMessage());
                            Notifications.create() 
                                .title("Eliminar") 
                                .text("No se ha eliminado el cliente")
                                .graphic(iconoBien)
                                .owner(stackPane)
                                .show();
                        }
                        tablaClientes.setItems(obtenerListaClientes());
                        tablaVentas.setItems(obtenerListaVentas());
                    }
                }
            });         
        }catch (Exception ex) {
            clienteSeleccionado = null;
            alerta.show();
        }*/
    }
    
    @FXML
    void insertarItem(ActionEvent event) {
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

    @FXML
    void verPaneVentas(ActionEvent event) {
        paneProductos.setVisible(false);
        paneSesiones.setVisible(false);
        paneUsuarios.setVisible(false);
        paneVentas.setVisible(true);
        paneCodigoBarras.setVisible(false);
    }
    
    @FXML
    void verPaneCodigoBarras(ActionEvent event) {
        paneProductos.setVisible(false);
        paneSesiones.setVisible(false);
        paneUsuarios.setVisible(false);
        paneVentas.setVisible(false);
        paneCodigoBarras.setVisible(true);
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
    
    
    public boolean generarImagenCodigoBarras(String data) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(data, BarcodeFormat.CODE_128, 300, 150);

            WritableImage writableImage = new WritableImage(bitMatrix.getWidth(), bitMatrix.getHeight());
            PixelWriter pixelWriter = writableImage.getPixelWriter();

            for (int y = 0; y < bitMatrix.getHeight(); y++) {
                for (int x = 0; x < bitMatrix.getWidth(); x++) {
                    pixelWriter.setColor(x, y, bitMatrix.get(x, y) ? javafx.scene.paint.Color.BLACK : javafx.scene.paint.Color.WHITE);
                }
            }

            imagenCodigo.setImage(writableImage);

            System.out.println("Código de barras generado y mostrado en memoria.");

            return true;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return false;
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
                            rs.getInt("utilizado"),
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
            cbColumnUtilizado.setCellValueFactory(new PropertyValueFactory<>("utilizado"));
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
        
        
        try {
            conexion = ConexionSingleton.obtenerConexion();
            this.productoDAO = new ProductoDAO(conexion);
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
