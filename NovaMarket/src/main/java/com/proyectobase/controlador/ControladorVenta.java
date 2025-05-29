package com.proyectobase.controlador;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.proyectobase.modelo.CodigoBarras;
import com.proyectobase.modelo.ConexionSingleton;
import com.proyectobase.modelo.DetalleVenta;
import com.proyectobase.modelo.DetalleVentaDAO;
import com.proyectobase.modelo.Producto;
import com.proyectobase.modelo.ProductoDAO;
import com.proyectobase.modelo.Sesion;
import com.proyectobase.modelo.SessionManager;
import com.proyectobase.modelo.Usuario;
import com.proyectobase.modelo.UsuarioDAO;
import com.proyectobase.modelo.Venta;
import com.proyectobase.modelo.VentaDAO;
import com.proyectobase.modelo.WSClient;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
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
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageRange;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javax.imageio.ImageIO;
import javax.management.openmbean.SimpleType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.controlsfx.control.Notifications;
import org.mindrot.jbcrypt.BCrypt;






public class ControladorVenta implements Initializable {
    String codigoProducto = "";
    Connection conexion;
    Statement st;
    ResultSet rs;
    
    private ProductoDAO productoDAO;
    private UsuarioDAO usuarioDAO;
    private VentaDAO ventaDAO;
    private DetalleVentaDAO detalleVentaDAO;
    ObservableList<Producto> lstProductos = FXCollections.observableArrayList();
    ObservableList<Producto> lstProductosEscaneados = FXCollections.observableArrayList();
    
    
    
    ObservableList<Usuario> lstUsuarios = FXCollections.observableArrayList();
    ObservableList<Venta> lstVentas = FXCollections.observableArrayList();
    ObservableList<Sesion> lstSesiones = FXCollections.observableArrayList();
    ObservableList<CodigoBarras> lstCodigoBarras = FXCollections.observableArrayList();
    ObservableList<DetalleVenta> lstDetalleVenta = FXCollections.observableArrayList();
    
    
    
    
    
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
    private Button btnDetalleProducto;
    
    @FXML
    private Button btnQuitarProducto;

    @FXML
    private ImageView imgBtnProductos;

    @FXML
    private ImageView imgBtnSesiones;

    @FXML
    private ImageView imgBtnUsuarios;

    @FXML
    private ImageView imgBtnVentas;
    
    @FXML
    private TableColumn<DetalleVenta, Integer> dvColumnCantidad;

    @FXML
    private TableColumn<DetalleVenta, Integer> dvColumnId;

    @FXML
    private TableColumn<DetalleVenta, Double> dvColumnPrecioUnitario;

    @FXML
    private TableColumn<DetalleVenta, Integer> dvColumnProductoId;

    @FXML
    private TableColumn<DetalleVenta, Double> dvColumnSubtotal;

    @FXML
    private TableColumn<DetalleVenta, Integer> dvColumnVentaId;

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
    private TableView<DetalleVenta> tablaDetalleVenta;

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
    private Button btnCerrarSesion;
    
    @FXML
    private TabPane tabPanePrincipal;
    
    @FXML
    private Tab tabVenta;
    
    /*@FXML
    void accederTabVenta(ActionEvent event) {
        //campoCodigoOculto.requestFocus();
    }*/

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmación");
        alerta.setHeaderText("¿Está seguro de que desea cerrar sesión?");
        alerta.setContentText("Se cerrará la sesión actual y volverá a la pantalla de login.");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectobase/vista/ventanaLogin.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                stage.setMaximized(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private ImageView imgCabezera;
    
    @FXML
    private Label lblCabezera;
    
    @FXML
    private ImageView imgBotonSalir;

    
    @FXML
    void quitarProductoListaVenta(ActionEvent event) {
        for(int i = 0; i<lstProductosEscaneados.size(); i++){
            if(lstProductosEscaneados.get(i).getId() == tablaProductos.getSelectionModel().getSelectedItem().getId()){
                lstProductosEscaneados.remove(i);
                break;
            }
        }
        actualizarLabelTotalVenta();
    }
    
    public double actualizarLabelTotalVenta(){
        double total = 0;
        
        
        for(int i = 0; i<lstProductosEscaneados.size(); i++){
            total+=lstProductosEscaneados.get(i).getPrecio_con_iva();
        }
        lblPrecio.setText(total+"€");
        return total;
    }
    ObservableList<DetalleVenta> lstDetalleVentaEscaneados = FXCollections.observableArrayList();
    @FXML
    void finalizarCompra(ActionEvent event) {
        try {
        
            Venta venta = new Venta(
                    0, 1, 2, LocalDate.now(), "Venta regular",
                    actualizarLabelTotalVenta(), "EFECTIVO", "CONTADO", "COMPLETADA"
            );


            List<DetalleVenta> lstDetalleVentasEscaneados = new ArrayList<>();
            for (Producto p : lstProductosEscaneados) {
                int idProducto = p.getId();
                int cantidad = 1;
                double precioUnitario = p.getPrecio_con_iva();
                double subtotal = cantidad * precioUnitario;

                DetalleVenta detalle = new DetalleVenta(0, venta.getId(), idProducto, cantidad, precioUnitario, subtotal);
                lstDetalleVentasEscaneados.add(detalle);

            }

            ventaDAO.crearVentaConDetalles(venta, lstDetalleVentasEscaneados);
            obtenerListaDetalleVenta();
            tablaVentas.getItems().clear();
            tablaVentas.setItems(obtenerListaVentas());
            String nombreArchivo = "ticket_venta_" + System.currentTimeMillis() + ".pdf";

            generarTicketPDF(nombreArchivo, lstProductosEscaneados, actualizarLabelTotalVenta());

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Venta completada");
            confirmacion.setHeaderText("¿Desea ver el ticket de venta?");
            confirmacion.setContentText("Se abrirá en el visor de PDF predeterminado de su sistema.");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                abrirPDFConVisorExterno(nombreArchivo);
            }

            tablaProductos.getItems().clear();
            actualizarLabelTotalVenta();

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al generar el ticket: " + ex.getMessage()).show();
        }
    }

    private void abrirPDFConVisorExterno(String rutaPDF) {
        try {
            File file = new File(rutaPDF);
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (file.exists()) {
                    desktop.open(file);

                    Alert imprimirAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    imprimirAlert.setTitle("Imprimir Ticket");
                    imprimirAlert.setHeaderText("¿Desea imprimir el ticket ahora?");

                    Optional<ButtonType> printResult = imprimirAlert.showAndWait();
                    if (printResult.isPresent() && printResult.get() == ButtonType.OK) {
                        imprimirPDF(rutaPDF);
                    }
                }
            } else {
                throw new Exception("No se puede abrir el PDF: Desktop no soportado en este sistema");
            }

            new Thread(() -> {
                try {
                    Thread.sleep(30000);
                    file.delete();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el PDF: " + e.getMessage()).show();
        }
    }

    private void imprimirPDF(String rutaPDF) {
        try {
            PDDocument document = PDDocument.load(new File(rutaPDF));

            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage bufferedImage = renderer.renderImageWithDPI(0, 300); // 300 DPI para buena calidad
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);

            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(null)) {
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(job.getJobSettings().getPageLayout().getPrintableWidth());
                imageView.setPreserveRatio(true);

                if (job.printPage(imageView)) {
                    job.endJob();
                }
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al imprimir: " + e.getMessage()).show();
        }
    }
    
    public void generarTicketPDF(String rutaArchivo, List<Producto> productos, double total) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
                content.beginText();
                content.setFont(PDType1Font.COURIER, 12);
                content.newLineAtOffset(50, 700);
                content.showText("=== TICKET DE VENTA ===");
                content.newLineAtOffset(0, -20);

                for (Producto p : productos) {
                    content.showText(p.getNombre() + " - " + p.getPrecio()+"€");
                    content.newLineAtOffset(0, -15);
                }

                content.newLineAtOffset(0, -20);
                content.showText("Total: " + total+"€");
                content.endText();
            }

            doc.save(rutaArchivo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void actualizarItem(ActionEvent event) {
        switch(identificadorTabla){
            case 1 -> {
                if (!validarTodosLosCamposProducto()) {
                    mostrarAlerta("Campos inválidos", "Por favor corrige los campos marcados antes de actualizar.");
                    return;
                }
                
                String query = "UPDATE productos SET nombre=?, precio=?, precio_con_iva=?, stock=?, descripcion=?, imagenB64=?, categoria=? WHERE id=?";
                try {
                    PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
                    preparedStatement.setString(1, tfpNombre.getText());
                    preparedStatement.setDouble(2, Double.parseDouble(tfpPrecio.getText()));
                    preparedStatement.setDouble(3, Double.parseDouble(tfpPrecioIva.getText()));
                    preparedStatement.setInt(4, Integer.parseInt(tfpStock.getText()));
                    preparedStatement.setString(5, tfpDescripcion.getText());

                    Image imagen = imagenActualizar.getImage();
                    if (imagen != null) {
                        preparedStatement.setString(6, imagenToBase64(imagen));
                    } else {
                        preparedStatement.setNull(6, java.sql.Types.VARCHAR); // o usar preparedStatement.setString(5, ""); si prefieres cadena vacía
                    }

                    preparedStatement.setString(7, tfpCategoria.getText());
                    preparedStatement.setInt(8, Integer.parseInt(tfpid.getText()));

                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println("Excepción: " + ex.getMessage());
                    mostrarAlerta("Error al actualizar el producto", "Alguno de los campos son incorrectos");
                } catch (IllegalArgumentException e) {
                    System.out.println("El número introducido no es correcto");
                    mostrarAlerta("Error al actualizar el producto", "Alguno de los campos son incorrectos");
                }

                tablaProductosAdmin.setItems(obtenerListaProductos());
            }
            
            case 2 -> {
            
            }
            
            case 3 -> {
                
                if (!validarTodosLosCamposUsuario()) {
                    mostrarAlerta("Campos inválidos", "Por favor corrige los campos marcados antes de actualizar.");
                    return;
                }

                boolean actualizarPassword = checkEditarPassword.isSelected() && !tfuPassword.getText().isEmpty();

                String query;
                if (actualizarPassword) {
                    query = "UPDATE usuarios SET correo=?, rol=?, nombre=?, apellido=?, telefono=?, direccion=?, password_hash=? WHERE id=?";
                } else {
                    query = "UPDATE usuarios SET correo=?, rol=?, nombre=?, apellido=?, telefono=?, direccion=? WHERE id=?";
                }

                try {
                    PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
                    preparedStatement.setString(1, tfuCorreo.getText());
                    preparedStatement.setString(2, tfuPermisos.getValue());
                    preparedStatement.setString(3, tfuNombre.getText());
                    preparedStatement.setString(4, tfuApellidos.getText());
                    preparedStatement.setString(5, tfuTelefono.getText());
                    preparedStatement.setString(6, tfuDireccion.getText());

                    if (actualizarPassword) {
                        // Hashear la contraseña antes de guardarla (IMPORTANTE para seguridad)
                        String hashedPassword = BCrypt.hashpw(tfuPassword.getText(), BCrypt.gensalt());
                        preparedStatement.setString(7, hashedPassword);
                        preparedStatement.setInt(8, Integer.parseInt(tfuId.getText()));
                    } else {
                        preparedStatement.setInt(7, Integer.parseInt(tfuId.getText()));
                    }

                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println("Excepción: "+ex.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("El número introducido no es correcto");
                }

                tablaUsuarios.getItems().clear();
                tablaUsuarios.setItems(obtenerListaUsuarios());
            }
            
            case 4 -> {
                if (!validarTodosLosCamposVenta()) {
                    mostrarAlerta("Campos inválidos", "Por favor corrige los campos marcados antes de actualizar.");
                    return;
                }
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
            case 1 -> {
                productoDAO.eliminarProducto(tablaProductosAdmin.getSelectionModel().getSelectedItem());
                tablaProductos.getItems().clear();
                tablaProductos.setItems(obtenerListaProductos());
                tablaCodigoBarras.getItems().clear();
                tablaCodigoBarras.setItems(obtenerListaCodigoBarras());
            
            }
            //case 2 -> 
            case 3 -> {
                usuarioDAO.eliminarUsuario(tablaUsuarios.getSelectionModel().getSelectedItem());
                tablaUsuarios.getItems().clear();
                tablaUsuarios.setItems(obtenerListaUsuarios());
                tablaSesiones.getItems().clear();
                tablaSesiones.setItems(obtenerListaSesiones());
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
   
    if ((nombre == null || nombre.trim().isEmpty()) && 
        (apellido == null || apellido.trim().isEmpty())) {
        return "usuario" + System.currentTimeMillis() + "@empresa.com";
    }

    String nombreBase = normalizarTexto(nombre != null ? nombre.trim().split(" ")[0] : "");
    String apellidoBase = normalizarTexto(apellido != null ? apellido.trim().split(" ")[0] : "");

  
    String correoBase = (nombreBase.isEmpty() ? "user" : nombreBase.toLowerCase()) + 
                       (apellidoBase.isEmpty() ? "" : "." + apellidoBase.toLowerCase());


    String dominio = "empresa.com";


    String correoPropuesto = correoBase + "@" + dominio;
    

    if (!existeCorreoEnLista(correoPropuesto)) {
        return correoPropuesto;
    }


    int contador = 1;
    while (contador < 1000) {
        String correoConNumero = correoBase + contador + "@" + dominio;
        if (!existeCorreoEnLista(correoConNumero)) {
            return correoConNumero;
        }
        contador++;
    }


    return correoBase + System.currentTimeMillis() + "@" + dominio;
}


private String normalizarTexto(String texto) {
    if (texto == null || texto.isEmpty()) {
        return "";
    }
    return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "")
            .replaceAll("[^a-zA-Z0-9]", "");
}


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
                tablaCodigoBarras.getItems().clear();
                tablaCodigoBarras.setItems(obtenerListaCodigoBarras());
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
                    tablaSesiones.getItems().clear();
                    tablaSesiones.setItems(obtenerListaSesiones());
                    
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
                            2,
                            LocalDate.now(),
                            "",
                            0.0,
                            "",
                            "",
                            ""
                    );

                    boolean exito = ventaDAO.crearVentaConDetalles(ventaNueva,lstDetalleVenta);
                    tablaVentas.getItems().clear();
                    tablaVentas.setItems(obtenerListaVentas());
                    if (exito) {
                        lstVentas.add(ventaNueva); 
                        lstDetalleVenta.addAll(listaDetalleProductosParaVenta);
                    } else {
                    }

                } catch (Exception e) {
                    System.err.println("Error al insertar el usuario: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    ObservableList<DetalleVenta> listaDetalleProductosParaVenta = FXCollections.observableArrayList();
        
    
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
    Tooltip tooltipNombre = new Tooltip("El nombre no puede estar vacío.");
    Tooltip tooltipPrecio = new Tooltip("Introduce un precio válido (ej: 10.99).");
    Tooltip tooltipStock = new Tooltip("El stock debe ser un número entero.");
    Tooltip tooltipCategoria = new Tooltip("La categoría no puede estar vacía.");
    Tooltip tooltipDescripcion = new Tooltip("La descripción no puede estar vacía.");
    
    private boolean validarCampoNombre() {
        if (tfpNombre.getText().trim().isEmpty()) {
            tfpNombre.setStyle("-fx-border-color: red;");
            tfpNombre.setTooltip(tooltipNombre);
            return false;
        } else {
            tfpNombre.setStyle(null);
            tfpNombre.setTooltip(null);
            return true;
        }
    }

    private boolean validarCampoPrecio() {
        if (!tfpPrecio.getText().matches("\\d+(\\.\\d{1,2})?")) {
            tfpPrecio.setStyle("-fx-border-color: red;");
            tfpPrecio.setTooltip(tooltipPrecio);
            return false;
        } else {
            tfpPrecio.setStyle(null);
            tfpPrecio.setTooltip(null);
            return true;
        }
    }

    private boolean validarCampoStock() {
        if (!tfpStock.getText().matches("\\d+")) {
            tfpStock.setStyle("-fx-border-color: red;");
            tfpStock.setTooltip(tooltipStock);
            return false;
        } else {
            tfpStock.setStyle(null);
            tfpStock.setTooltip(null);
            return true;
        }
    }
    
    private boolean validarCampoDescripcion() {
        if (tfpDescripcion.getText().trim().isEmpty()) {
            tfpDescripcion.setStyle("-fx-border-color: red;");
            tfpDescripcion.setTooltip(tooltipDescripcion);
            return false;
        } else {
            tfpDescripcion.setStyle(null);
            tfpDescripcion.setTooltip(null);
            return true;
        }
    }

    private boolean validarCampoCategoria() {
        if (tfpCategoria.getText().trim().isEmpty()) {
            tfpCategoria.setStyle("-fx-border-color: red;");
            tfpCategoria.setTooltip(tooltipCategoria);
            return false;
        } else {
            tfpCategoria.setStyle(null);
            tfpCategoria.setTooltip(null);
            return true;
        }
    }
    
    private void calcularPrecioConIva() {
        try {
            double precio = Double.parseDouble(tfpPrecio.getText().trim());
            double precioConIva = precio * 1.21;
            tfpPrecioIva.setText(""+precioConIva);
        } catch (NumberFormatException e) {
            tfpPrecioIva.setText("");
        }
    }
    
    private boolean validarTodosLosCamposProducto() {
        /*System.out.println("Nombre: "+validarCampoNombre());
        System.out.println("Precio: "+validarCampoPrecio());
        System.out.println("Stock: "+validarCampoStock());
        System.out.println("Descripcion: "+validarCampoDescripcion());
        System.out.println("Categoria: "+validarCampoCategoria());*/
        return validarCampoNombre() &&
           validarCampoPrecio() &&
           validarCampoStock() &&
           validarCampoDescripcion() &&
           validarCampoCategoria();
    
}
    
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

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

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
        Label labelFechaCreacion = new Label("Fecha creación");
        Label labelFechaActualizacion = new Label("Fecha actualización");

        imagenActualizar = new ImageView();
        imagenActualizar.setFitWidth(130);
        imagenActualizar.setFitHeight(130);
        imagenActualizar.setStyle("-fx-cursor: hand;");

        imagenActualizar.setOnMouseClicked(e -> {
            File file = fileChooser.showOpenDialog(paneProductos.getScene().getWindow());
            if (file != null) {
                Image nuevaImagen = new Image(file.toURI().toString());
                imagenActualizar.setImage(nuevaImagen);
                imagenSeleccionadaB64 = imagenToBase64(nuevaImagen);

                Producto selectedProduct = tablaProductosAdmin.getSelectionModel().getSelectedItem();
                if (selectedProduct != null) {
                    selectedProduct.setImagenB64(imagenSeleccionadaB64);
                }
            }
        });

        vboxEditarItem.getChildren().addAll(
            titulo, labelId, tfpid, labelCodigoBarras, tfpcodigoBarras, labelNombre, tfpNombre,
            labelPrecio, tfpPrecio, labelPrecioIva, tfpPrecioIva,
            labelStock, tfpStock, labelDescripcion, tfpDescripcion,
            labelImagen, imagenActualizar, labelCategoria, tfpCategoria,
            labelFechaCreacion, tfpFechaCreacion, labelFechaActualizacion, tfpFechaActualizacion
        );

        VBox.setMargin(titulo, new Insets(0, 0, 20, 0));
        vboxEditarItem.setPadding(new Insets(20, 20, 20, 20));
        vboxEditarItem.setSpacing(10);
        vboxEditarItem.setPrefWidth(Region.USE_COMPUTED_SIZE);

        tablaProductosAdmin.getSelectionModel().selectedItemProperty().addListener((observable, oldProduct, selectedProduct) -> {
            if (selectedProduct != null) {
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

                if (selectedProduct.getImagenB64() != null && !selectedProduct.getImagenB64().isEmpty()) {
                    imagenActualizar.setImage(base64ToImage(selectedProduct.getImagenB64()));
                } else {
                    imagenActualizar.setImage(null);
                }
                
                validarCampoNombre();
                validarCampoPrecio();
                validarCampoStock();
                validarCampoDescripcion();
                validarCampoCategoria();
            }
        });
        
        tfpNombre.setOnKeyReleased(e -> validarCampoNombre());
        tfpPrecio.setOnKeyReleased(e -> {
            validarCampoPrecio();
            calcularPrecioConIva();
        });
        tfpDescripcion.setOnKeyReleased(e -> validarCampoDescripcion());
        tfpStock.setOnKeyReleased(e -> validarCampoStock());
        tfpCategoria.setOnKeyReleased(e -> validarCampoCategoria());



    }

    
    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
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
    private ComboBox<String> tfuPermisos;
    private TextField tfuNombre;
    private TextField tfuApellidos;
    private TextField tfuTelefono;
    private TextField tfuDireccion;
    private TextField tfuFechaRegistro;
    private CheckBox checkEditarPassword;
    private TextField tfuPassword;
    

    private final Tooltip tooltipCorreo = new Tooltip("Introduce un correo electrónico válido.");
    private final Tooltip tooltipPermisos = new Tooltip("Los permisos deben ser 'admin' o 'user'.");
    private final Tooltip tooltipNombreUsuario = new Tooltip("El nombre no puede estar vacío.");
    private final Tooltip tooltipApellidos = new Tooltip("Los apellidos no pueden estar vacíos.");
    private final Tooltip tooltipTelefono = new Tooltip("Introduce un número de teléfono válido (9 dígitos).");
    private final Tooltip tooltipDireccion = new Tooltip("La dirección no puede estar vacía.");

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

        // Inicializar campos de usuario
        tfuId = new TextField();
        tfuCorreo = new TextField();
        tfuPermisos = new ComboBox();
        tfuNombre = new TextField();
        tfuApellidos = new TextField();
        tfuTelefono = new TextField();
        tfuDireccion = new TextField();
        tfuFechaRegistro = new TextField();
        tfuPassword = new TextField();
        tfuPassword.setPromptText("Nueva contraseña");
        tfuPassword.setVisible(false);
        
        checkEditarPassword = new CheckBox("Editar contraseña");
        checkEditarPassword.setOnAction(e -> {
            tfuPassword.setVisible(checkEditarPassword.isSelected());
        });

        tfuId.setEditable(false);
        tfuFechaRegistro.setEditable(false);

        
        Label titulo = new Label("Editar usuario:");
        titulo.setStyle("-fx-font-size: 20px;");
        tfuPermisos.getItems().addAll("administrador", "cliente", "empleado");

        vboxEditarItem.getChildren().addAll(
            titulo,
            new Label("ID:"), tfuId,
            new Label("Correo:"), tfuCorreo,
            new Label("Permisos:"), tfuPermisos,
            new Label("Nombre:"), tfuNombre,
            new Label("Apellidos:"), tfuApellidos,
            new Label("Teléfono:"), tfuTelefono,
            new Label("Dirección:"), tfuDireccion,
            new Label("Fecha de registro:"), tfuFechaRegistro,
            checkEditarPassword,
            tfuPassword
        );

        VBox.setMargin(titulo, new Insets(0, 0, 20, 0));
        vboxEditarItem.setPadding(new Insets(20, 20, 20, 20));
        vboxEditarItem.setSpacing(10);
        vboxEditarItem.setPrefWidth(Region.USE_COMPUTED_SIZE);

       
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((observable, oldUsuario, selectedUsuario) -> {
            if (selectedUsuario != null && tfuId != null) {
                tfuId.setText(String.valueOf(selectedUsuario.getId()));
                tfuCorreo.setText(String.valueOf(selectedUsuario.getCorreo()));
                tfuPermisos.setValue(String.valueOf(selectedUsuario.getRol()));
                tfuNombre.setText(selectedUsuario.getNombre());
                tfuApellidos.setText(selectedUsuario.getApellido());
                tfuTelefono.setText(String.valueOf(selectedUsuario.getTelefono()));
                tfuDireccion.setText(selectedUsuario.getDireccion());
                tfuFechaRegistro.setText(String.valueOf(selectedUsuario.getFecha_registro()));

                validarCampoCorreo();
                validarCampoPermisos();
                validarCampoNombreUsuario();
                validarCampoApellidos();
                validarCampoTelefono();
                validarCampoDireccion();
            }
        });

        if (tfuCorreo != null) tfuCorreo.setOnKeyReleased(e -> validarCampoCorreo());
        if (tfuPermisos != null) tfuPermisos.setOnAction(e -> validarCampoPermisos());
        if (tfuNombre != null) tfuNombre.setOnKeyReleased(e -> validarCampoNombreUsuario());
        if (tfuApellidos != null) tfuApellidos.setOnKeyReleased(e -> validarCampoApellidos());
        if (tfuTelefono != null) tfuTelefono.setOnKeyReleased(e -> validarCampoTelefono());
        if (tfuDireccion != null) tfuDireccion.setOnKeyReleased(e -> validarCampoDireccion());
        if (checkEditarPassword != null) {
            checkEditarPassword.setOnAction(e -> {
                boolean isSelected = checkEditarPassword.isSelected();
                tfuPassword.setVisible(isSelected);
                if (isSelected) validarCampoPassword();
            });
        }
        if (tfuPassword != null) {
            tfuPassword.setOnKeyReleased(e -> validarCampoPassword());
        }
    }
    
    private void validarCampoPassword() {
        String password = tfuPassword.getText();

        if (password.isEmpty()) {
            tfuPassword.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            tfuPassword.setTooltip(new Tooltip("La contraseña no puede estar vacía."));
        } else if (password.length() < 8) {
            tfuPassword.setStyle("-fx-border-color: orange; -fx-border-width: 1px;");
            tfuPassword.setTooltip(new Tooltip("La contraseña debe tener al menos 8 caracteres."));
        } else {
            tfuPassword.setStyle("");
            tfuPassword.setTooltip(null);
        }
    }

    private boolean validarCampoCorreo() {
        if (tfuCorreo == null) return false;

        if (!tfuCorreo.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            tfuCorreo.setStyle("-fx-border-color: red;");
            tfuCorreo.setTooltip(tooltipCorreo);
            return false;
        } else {
            tfuCorreo.setStyle(null);
            tfuCorreo.setTooltip(null);
            return true;
        }
    }

    private boolean validarCampoPermisos() {
        if (tfuPermisos == null) return false;

        String permisos = tfuPermisos.getValue().toLowerCase();
        if (!permisos.equals("administrador") && !permisos.equals("empleado") && !permisos.equals("cliente")) {
            tfuPermisos.setStyle("-fx-border-color: red;");
            tfuPermisos.setTooltip(tooltipPermisos);
            return false;
        } else {
            tfuPermisos.setStyle(null);
            tfuPermisos.setTooltip(null);
            return true;
        }
    }
    

    private boolean validarCampoNombreUsuario() {
        if (tfuNombre == null) return false;

        if (tfuNombre.getText().trim().isEmpty()) {
            tfuNombre.setStyle("-fx-border-color: red;");
            tfuNombre.setTooltip(tooltipNombreUsuario);
            return false;
        } else {
            tfuNombre.setStyle(null);
            tfuNombre.setTooltip(null);
            return true;
        }
    }

    private boolean validarCampoApellidos() {
        if (tfuApellidos == null) return false;

        if (tfuApellidos.getText().trim().isEmpty()) {
            tfuApellidos.setStyle("-fx-border-color: red;");
            tfuApellidos.setTooltip(tooltipApellidos);
            return false;
        } else {
            tfuApellidos.setStyle(null);
            tfuApellidos.setTooltip(null);
            return true;
        }
    }

    private boolean validarCampoTelefono() {
        if (tfuTelefono == null) return false;

        if (!tfuTelefono.getText().matches("\\d{9}")) {
            tfuTelefono.setStyle("-fx-border-color: red;");
            tfuTelefono.setTooltip(tooltipTelefono);
            return false;
        } else {
            tfuTelefono.setStyle(null);
            tfuTelefono.setTooltip(null);
            return true;
        }
    }

    private boolean validarCampoDireccion() {
        if (tfuDireccion == null) return false;

        if (tfuDireccion.getText().trim().isEmpty()) {
            tfuDireccion.setStyle("-fx-border-color: red;");
            tfuDireccion.setTooltip(tooltipDireccion);
            return false;
        } else {
            tfuDireccion.setStyle(null);
            tfuDireccion.setTooltip(null);
            return true;
        }
    }

    private boolean validarTodosLosCamposUsuario() {
        return validarCampoCorreo() &&
               validarCampoPermisos() &&
               validarCampoNombreUsuario() &&
               validarCampoApellidos() &&
               validarCampoTelefono() &&
               validarCampoDireccion();
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
    private Button añadirProducto;
    private Button eliminarProducto;
    private ComboBox<Producto> cbProductosDisponibles;
    private Producto productoSeleccionadoAañadir;
    private int idVenta;
    private List<DetalleVenta> listaProductosVentaSeleccionada;
    private List<Producto> listaProductosDisponibles;
    ListView<Producto> listViewProductosVenta;
    
    private final Tooltip tooltipCliente = new Tooltip("Selecciona un cliente.");
    private final Tooltip tooltipEmpleado = new Tooltip("Selecciona un empleado.");
    private final Tooltip tooltipFecha = new Tooltip("Selecciona una fecha válida.");
    private final Tooltip tooltipDescripcionVenta = new Tooltip("La descripción no puede estar vacía.");
    private final Tooltip tooltipTotal = new Tooltip("El total debe ser un número válido (ej: 10.99).");
    private final Tooltip tooltipMetodoPago = new Tooltip("El método de pago no puede estar vacío.");
    private final Tooltip tooltipTipoVenta = new Tooltip("El tipo de venta no puede estar vacío.");
    private final Tooltip tooltipEstado = new Tooltip("El estado no puede estar vacío.");
    private final Tooltip tooltipProductos = new Tooltip("Debe haber al menos un producto en la venta.");

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
            cbCliente = new ComboBox<>();
            cbEmpleado = new ComboBox<>();
            dpFecha = new DatePicker();
            tfvDescripcion = new TextField();
            tfvTotal = new TextField();
            tfvMetodoPago = new TextField();
            tfvTipoVenta = new TextField();
            tfvEstado = new TextField();
            tfvId.setEditable(false);
            cbProductosDisponibles = new ComboBox<>();
            listViewProductosVenta = new ListView<>();
            añadirProducto = new Button("Añadir producto");
            eliminarProducto = new Button("Eliminar producto");

            listaProductosVentaSeleccionada = new ArrayList<>();
            listaProductosDisponibles = new ArrayList<>();

            Label titulo = new Label("Editar venta: ");
            titulo.setStyle("-fx-font-size: 20px;");

            Label labelId = new Label("ID:");
            Label labelCliente = new Label("Cliente:");
            Label labelEmpleado = new Label("Empleado");
            Label labelFecha = new Label("Fecha:");
            Label labelDescripcion = new Label("Descripción:");
            Label labelTotal = new Label("Total:");
            Label labelMetodoPago = new Label("Método de pago:");
            Label labelTipoVenta = new Label("Tipo de venta:");
            Label labelEstado = new Label("Estado:");
            Label labelInsertarProductoAventa = new Label("Insertar producto a la venta: ");
            Label labelQuitarProductoVenta = new Label("Eliminar un producto de la venta: ");

            vboxEditarItem.getChildren().addAll(
                titulo, labelId, tfvId, labelCliente, cbCliente,
                labelEmpleado, cbEmpleado, labelFecha, dpFecha,
                labelDescripcion, tfvDescripcion, labelTotal, tfvTotal,
                labelMetodoPago, tfvMetodoPago, labelTipoVenta, tfvTipoVenta,
                labelEstado, tfvEstado, labelInsertarProductoAventa,
                cbProductosDisponibles, añadirProducto, labelQuitarProductoVenta,
                listViewProductosVenta, eliminarProducto
            );

            VBox.setMargin(titulo, new Insets(0, 0, 20, 0));
            vboxEditarItem.setPadding(new Insets(20, 20, 20, 20));
            vboxEditarItem.setSpacing(10);
            vboxEditarItem.setPrefWidth(Region.USE_COMPUTED_SIZE);
        tablaVentas.getSelectionModel().selectedItemProperty().addListener((observable, oldVenta, selectedVenta) -> {
            if (selectedVenta != null) {
                // Cargar datos primero
                listaProductosVentaSeleccionada.clear();
                listViewProductosVenta.getItems().clear();

                tfvId.setText(String.valueOf(selectedVenta.getId()));

            
                for (Usuario usuario : lstUsuarios) {
                    if (usuario.getId() == selectedVenta.getCliente_id()) {
                        cbCliente.setValue(usuario.getNombre() + " " + usuario.getApellido());
                    }
                    if (usuario.getId() == selectedVenta.getEmpleado_id()) {
                        cbEmpleado.setValue(usuario.getNombre() + " " + usuario.getApellido());
                    }
                }

           
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                dpFecha.setValue(LocalDate.parse(selectedVenta.getFecha().toString(), formatter));


                tfvDescripcion.setText(selectedVenta.getDescripcion());
                tfvTotal.setText(String.format(""+selectedVenta.getTotal()));
                tfvMetodoPago.setText(selectedVenta.getMetodo_pago());
                tfvTipoVenta.setText(selectedVenta.getTipo_venta());
                tfvEstado.setText(selectedVenta.getEstado());

                idVenta = selectedVenta.getId();

         
                for (DetalleVenta dv : lstDetalleVenta) {
                    if (dv.getVenta_id() == selectedVenta.getId()) {
                        listaProductosVentaSeleccionada.add(dv);
                        for (Producto p : lstProductos) {
                            if (dv.getProducto_id() == p.getId()) {
                                listViewProductosVenta.getItems().add(p);
                                break;
                            }
                        }
                    }
                }

                cbCliente.setStyle(null);
                cbEmpleado.setStyle(null);
                dpFecha.setStyle(null);
                tfvDescripcion.setStyle(null);
                tfvTotal.setStyle(null);
                tfvMetodoPago.setStyle(null);
                tfvTipoVenta.setStyle(null);
                tfvEstado.setStyle(null);
                listViewProductosVenta.setStyle(null);
                cbProductosDisponibles.setStyle(null);
                actualizarTotalVenta();
                
                validarCampoCliente();
                validarCampoEmpleado();
                validarCampoFecha();
                validarCampoDescripcionVenta();
                validarCampoTotal();
                validarCampoMetodoPago();
                validarCampoTipoVenta();
                validarCampoEstado();
                validarCampoProductos();
            }
        });

        añadirProducto.setOnAction(evento -> {
            Producto producto = cbProductosDisponibles.getSelectionModel().getSelectedItem();

            if (producto != null) {
                agregarProductoAVenta(producto);
                actualizarTotalVenta();
                validarCampoProductos();
            } else {
                mostrarAlerta("Advertencia", "Seleccione un producto para añadir");
                cbProductosDisponibles.setStyle("-fx-border-color: red;");
            }
        });

        eliminarProducto.setOnAction(evento -> {
            Producto producto = listViewProductosVenta.getSelectionModel().getSelectedItem();

            if (producto != null) {
                eliminarProductoDeVenta(producto);
                actualizarTotalVenta();
                validarCampoProductos();
            } else {
                mostrarAlerta("Advertencia", "Seleccione un producto para eliminar");
                listViewProductosVenta.setStyle("-fx-border-color: red;");
            }
        });

        
            
            
            
            if (cbCliente != null) cbCliente.setOnAction(e -> validarCampoCliente());
            if (cbEmpleado != null) cbEmpleado.setOnAction(e -> validarCampoEmpleado());
            if (dpFecha != null) dpFecha.setOnAction(e -> validarCampoFecha());
            if (tfvDescripcion != null) tfvDescripcion.setOnKeyReleased(e -> validarCampoDescripcionVenta());
            if (tfvTotal != null) tfvTotal.setOnKeyReleased(e -> validarCampoTotal());
            if (tfvMetodoPago != null) tfvMetodoPago.setOnKeyReleased(e -> validarCampoMetodoPago());
            if (tfvTipoVenta != null) tfvTipoVenta.setOnKeyReleased(e -> validarCampoTipoVenta());
            if (tfvEstado != null) tfvEstado.setOnKeyReleased(e -> validarCampoEstado());
            
            if (añadirProducto != null) añadirProducto.setOnAction(e -> {
                if (cbProductosDisponibles.getValue() != null) {
                    //listViewProductosVenta.getItems().add(cbProductosDisponibles.getValue());
                    Producto producto = cbProductosDisponibles.getSelectionModel().getSelectedItem();

                    if (producto != null) {
                        agregarProductoAVenta(producto);
                    } else {
                        mostrarAlerta("Advertencia", "Seleccione un producto para añadir");
                    }
                    actualizarTotalVenta();
                    validarCampoProductos();
                }
            });
            if (eliminarProducto != null) eliminarProducto.setOnAction(e -> {
                if (listViewProductosVenta.getSelectionModel().getSelectedItem() != null) {
                    Producto producto = listViewProductosVenta.getSelectionModel().getSelectedItem();
                    if (producto != null) {
                    eliminarProductoDeVenta(producto);
                    } else {
                        mostrarAlerta("Advertencia", "Seleccione un producto para eliminar");
                    }
                    actualizarTotalVenta();
                    validarCampoProductos();
                }
            });
            
            List<String> listaClientes = new ArrayList<>();
            List<String> listaEmpleados = new ArrayList<>();

            for(Usuario usuario : lstUsuarios) {
                if(usuario.getRol().equalsIgnoreCase("cliente")) {
                    listaClientes.add(usuario.getNombre() + " " + usuario.getApellido());
                } else {
                    listaEmpleados.add(usuario.getNombre() + " " + usuario.getApellido());
                }
            }

            cbCliente.getItems().addAll(listaClientes);
            cbEmpleado.getItems().addAll(listaEmpleados);

            actualizarListaProductosDisponibles();
        }
        
        
        private boolean validarCampoCliente() {
            if (cbCliente == null) return false;

            if (cbCliente.getValue() == null || cbCliente.getValue().isEmpty()) {
                cbCliente.setStyle("-fx-border-color: red;");
                cbCliente.setTooltip(tooltipCliente);
                return false;
            } else {
                cbCliente.setStyle(null);
                cbCliente.setTooltip(null);
                return true;
            }
        }

        private boolean validarCampoEmpleado() {
            if (cbEmpleado == null) return false;

            if (cbEmpleado.getValue() == null || cbEmpleado.getValue().isEmpty()) {
                cbEmpleado.setStyle("-fx-border-color: red;");
                cbEmpleado.setTooltip(tooltipEmpleado);
                return false;
            } else {
                cbEmpleado.setStyle(null);
                cbEmpleado.setTooltip(null);
                return true;
            }
        }

        private boolean validarCampoFecha() {
            if (dpFecha == null) return false;

            if (dpFecha.getValue() == null) {
                dpFecha.setStyle("-fx-border-color: red;");
                dpFecha.setTooltip(tooltipFecha);
                return false;
            } else {
                dpFecha.setStyle(null);
                dpFecha.setTooltip(null);
                return true;
            }
        }

        private boolean validarCampoDescripcionVenta() {
            if (tfvDescripcion == null) return false;

            if (tfvDescripcion.getText().trim().isEmpty()) {
                tfvDescripcion.setStyle("-fx-border-color: red;");
                tfvDescripcion.setTooltip(tooltipDescripcion);
                return false;
            } else {
                tfvDescripcion.setStyle(null);
                tfvDescripcion.setTooltip(null);
                return true;
            }
        }

        private boolean validarCampoTotal() {
            if (tfvTotal == null) return false;

            if (!tfvTotal.getText().matches("\\d+(\\.\\d{1,2})?")) {
                tfvTotal.setStyle("-fx-border-color: red;");
                tfvTotal.setTooltip(tooltipTotal);
                return false;
            } else {
                tfvTotal.setStyle(null);
                tfvTotal.setTooltip(null);
                return true;
            }
        }

        private boolean validarCampoMetodoPago() {
            if (tfvMetodoPago == null) return false;

            if (tfvMetodoPago.getText().trim().isEmpty()) {
                tfvMetodoPago.setStyle("-fx-border-color: red;");
                tfvMetodoPago.setTooltip(tooltipMetodoPago);
                return false;
            } else {
                tfvMetodoPago.setStyle(null);
                tfvMetodoPago.setTooltip(null);
                return true;
            }
        }

        private boolean validarCampoTipoVenta() {
            if (tfvTipoVenta == null) return false;

            if (tfvTipoVenta.getText().trim().isEmpty()) {
                tfvTipoVenta.setStyle("-fx-border-color: red;");
                tfvTipoVenta.setTooltip(tooltipTipoVenta);
                return false;
            } else {
                tfvTipoVenta.setStyle(null);
                tfvTipoVenta.setTooltip(null);
                return true;
            }
        }

        private boolean validarCampoEstado() {
            if (tfvEstado == null) return false;

            if (tfvEstado.getText().trim().isEmpty()) {
                tfvEstado.setStyle("-fx-border-color: red;");
                tfvEstado.setTooltip(tooltipEstado);
                return false;
            } else {
                tfvEstado.setStyle(null);
                tfvEstado.setTooltip(null);
                return true;
            }
        }

        private boolean validarCampoProductos() {
            if (listViewProductosVenta == null) return false;

            if (listViewProductosVenta.getItems().isEmpty()) {
                listViewProductosVenta.setStyle("-fx-border-color: red;");
                listViewProductosVenta.setTooltip(tooltipProductos);
                return false;
            } else {
                listViewProductosVenta.setStyle(null);
                listViewProductosVenta.setTooltip(null);
                return true;
            }
        }

        private boolean validarTodosLosCamposVenta() {
            return validarCampoCliente() &&
                   validarCampoEmpleado() &&
                   validarCampoFecha() &&
                   validarCampoDescripcionVenta() &&
                   validarCampoTotal() &&
                   validarCampoMetodoPago() &&
                   validarCampoTipoVenta() &&
                   validarCampoEstado() &&
                   validarCampoProductos();
        }
        

    private void actualizarTotalVenta() {
        double total = 0.0;
        for (DetalleVenta dv : listaProductosVentaSeleccionada) {
            total += dv.getPrecio_unitario();
        }
        tfvTotal.setText(String.format(""+total));
        validarCampoTotal();
    }
    
        private void agregarProductoAVenta(Producto producto) {
            if (producto.getStock() <= 0) {
                mostrarAlerta("Error", "No hay stock disponible para este producto");
                return;
            }

            Producto productoActualizado = productoDAO.obtenerProductoPorId(producto.getId());
            if (productoActualizado == null) {
                mostrarAlerta("Error", "No se pudo obtener la información actualizada del producto");
                return;
            }

            if (productoActualizado.getStock() <= 0) {
                mostrarAlerta("Error", "No hay stock disponible para este producto");
                return;
            }

            DetalleVenta detalle = new DetalleVenta(
                0,
                idVenta,
                productoActualizado.getId(),
                1,
                productoActualizado.getPrecio_con_iva(),
                productoActualizado.getPrecio_con_iva()
            );

            try {

                if (!productoDAO.actualizarProducto(productoActualizado)) {
                    mostrarAlerta("Error", "No se pudo actualizar el stock del producto");
                    return;
                }

                detalleVentaDAO.insertarDetalleVenta(detalle);

                lstDetalleVenta.add(detalle);
                listaProductosVentaSeleccionada.add(detalle);

                listViewProductosVenta.getItems().add(productoActualizado);
                actualizarTotalVenta();
                actualizarListaProductosDisponibles();
                actualizarTablaVentas();

            } catch (SQLException ex) {
                System.err.println("Error al agregar producto a venta: " + ex.getMessage());
                ex.printStackTrace();

                productoActualizado.setStock(productoActualizado.getStock() + 1);
                productoDAO.actualizarProducto(productoActualizado);

                mostrarAlerta("Error", "No se pudo agregar el producto a la venta: " + ex.getMessage());
            }
        }

        private void actualizarProductoEnLista(Producto productoActualizado) {
            for (int i = 0; i < lstProductos.size(); i++) {
                if (lstProductos.get(i).getId() == productoActualizado.getId()) {
                    lstProductos.set(i, productoActualizado);
                    break;
                }
            }

            boolean encontrado = false;
            for (int i = 0; i < listaProductosDisponibles.size(); i++) {
                if (listaProductosDisponibles.get(i).getId() == productoActualizado.getId()) {
                    if (productoActualizado.getStock() > 0) {
                        listaProductosDisponibles.set(i, productoActualizado);
                    } else {
                        listaProductosDisponibles.remove(i);
                    }
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado && productoActualizado.getStock() > 0) {
                listaProductosDisponibles.add(productoActualizado);
            }
        }

        private void eliminarProductoDeVenta(Producto producto) {
            DetalleVenta detalleAEliminar = listaProductosVentaSeleccionada.stream()
                .filter(d -> d.getProducto_id() == producto.getId())
                .findFirst()
                .orElse(null);

            if (detalleAEliminar != null) {
                try {
                    Producto productoActual = productoDAO.obtenerProductoPorId(producto.getId());
                    if (productoActual == null) {
                        mostrarAlerta("Error", "No se pudo obtener el producto actualizado");
                        return;
                    }

                    productoActual.setStock(productoActual.getStock() + detalleAEliminar.getCantidad()-1);

                    if (!productoDAO.actualizarProducto(productoActual)) {
                        mostrarAlerta("Error", "No se pudo actualizar el stock del producto");
                        return;
                    }

                    detalleVentaDAO.eliminarDetalleVenta(detalleAEliminar);
                    listaProductosVentaSeleccionada.remove(detalleAEliminar);
                    lstDetalleVenta.remove(detalleAEliminar);

                    listViewProductosVenta.getItems().remove(producto);
                    actualizarTotalVenta();
                    actualizarListaProductosDisponibles();
                    actualizarTablaVentas();

                    actualizarProductoEnLista(productoActual);

                } catch (SQLException ex) {
                    System.err.println("Error al eliminar producto de venta: " + ex.getMessage());
                    ex.printStackTrace();
                    mostrarAlerta("Error", "No se pudo eliminar el producto de la venta: " + ex.getMessage());
                }
            }
        }

        

        private void cargarDatosVentaSeleccionada(Venta venta) {
            
        }

        

        private void actualizarListaProductosDisponibles() {
            listaProductosDisponibles.clear();
            cbProductosDisponibles.getItems().clear();

            for (Producto producto : lstProductos) {
                if (producto.getStock() > 0) {
                    listaProductosDisponibles.add(producto);
                }
            }

            cbProductosDisponibles.getItems().addAll(listaProductosDisponibles);
        }

        private void actualizarTablaVentas() {
            tablaVentas.getItems().clear();
            tablaVentas.setItems(obtenerListaVentas());
        }

        private void mostrarAlerta(String titulo, String mensaje) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }

    
        private int obtenerIdEmpleado(String nombreCompleto) {
           if (nombreCompleto == null || nombreCompleto.isEmpty()) {
               return -1;
           }

           for (Usuario usuario : lstUsuarios) {
               if ((!usuario.getRol().equalsIgnoreCase("cliente")) && 
                   (usuario.getNombre() + " " + usuario.getApellido()).equals(nombreCompleto)) {
                   return usuario.getId();
               }
           }

           System.err.println("Empleado no encontrado: " + nombreCompleto);
           return -1;
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
            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(data, BarcodeFormat.CODE_128, 300, 150);

            WritableImage writableImage = new WritableImage(
                bitMatrix.getWidth(), 
                bitMatrix.getHeight()
            );

            PixelWriter pixelWriter = writableImage.getPixelWriter();

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
    
    private ObservableList<Venta> obtenerListaVentas() {
        if (conexion != null) {
            String query = "SELECT * FROM ventas";
            try {
                rs = st.executeQuery(query);
                lstVentas.clear(); 

                while (rs.next()) { 
                    java.sql.Date fechaSql = rs.getDate("fecha");
                    LocalDate fecha = (fechaSql != null) ? fechaSql.toLocalDate() : null;

                    Venta ven = new Venta(
                        rs.getInt("id"), 
                        rs.getInt("cliente_id"),
                        rs.getInt("empleado_id"),
                        fecha, 
                        rs.getString("descripcion"),
                        rs.getDouble("total"),
                        rs.getString("metodo_pago"),
                        rs.getString("tipo_venta"),
                        rs.getString("estado"));

                    lstVentas.add(ven);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: "+e.getMessage());
                e.printStackTrace();
            }
            return lstVentas;
        }
        return FXCollections.observableArrayList(); 
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
    
    private ObservableList obtenerListaDetalleVenta() {
        if (conexion != null) {
            String query = "SELECT * FROM detalle_venta";
            try {
                rs = st.executeQuery(query);
                DetalleVenta dv;
                while (rs.next()) { 
                    dv = new DetalleVenta(
                            rs.getInt("id"),
                            rs.getInt("venta_id"),
                            rs.getInt("producto_id"),
                            rs.getInt("cantidad"),
                            rs.getDouble("precio_unitario"),
                            rs.getDouble("subtotal"));
                    lstDetalleVenta.add(dv);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: "+e.getMessage());
            }
            return lstDetalleVenta;
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
                Producto producto = cellData.getValue();
                ImageView imageView = new ImageView();

                try {
                    String base64 = producto.getImagenB64();
                    if (base64 != null && !base64.isEmpty()) {
                        Image image = base64ToImage(base64);
                        if (image != null) {
                            imageView.setImage(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setPreserveRatio(true);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error al cargar imagen: " + e.getMessage());
                }

                return new SimpleObjectProperty<>(imageView);
            });

            tc_imagen.setCellFactory(column -> new TableCell<Producto, ImageView>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(ImageView item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getImage() == null) {
                        setGraphic(null);
                    } else {
                        imageView.setImage(item.getImage());
                        imageView.setFitHeight(60);
                        imageView.setFitWidth(60);
                        imageView.setPreserveRatio(true);
                        setGraphic(imageView);
                    }
                }
            });

            tablaProductos.setItems(lstProductosEscaneados);

        } catch (Exception ex) {
            System.out.println("Error en inicializarTablasProductos: " + ex.getMessage());
            ex.printStackTrace();
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
            System.out.println("Error en inicializarTablaVentas: " + ex.getMessage());
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
            System.out.println("Error en inicializarTablaSesiones: " + ex.getMessage());
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
            System.out.println("Error en inicializarTablaCodigoBarras: " + ex.getMessage());
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
    
    private Usuario usuarioLogueado;
    double totalCompra = 0;

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
        configurarPermisos(); 
    }

    private void configurarPermisos() {
        if (usuarioLogueado != null && usuarioLogueado.getRol() != null) {
            tabAdmin.setDisable(!usuarioLogueado.getRol().equals("administrador"));
        } else {
            System.err.println("Usuario no disponible para configurar permisos");
        }
    }
    
    private WSClient clienteWebSocket;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
            imgCabezera.setImage(new Image(getClass().getResourceAsStream("/logo_sin_letras.png")));
            imgBotonSalir.setImage(new Image(getClass().getResourceAsStream("/salir.png")));
            this.usuarioLogueado = SessionManager.getInstance().getUsuarioLogueado();
            if(!usuarioLogueado.getRol().equals("administrador")){
                tabAdmin.setDisable(true);
            }
            
            
            tabPanePrincipal.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldTab, newTab) -> {
                        if (newTab == tabVenta) {
                            Platform.runLater(() -> campoCodigoOculto.requestFocus());
                        }
                    }
            );
            
            if (tabPanePrincipal.getSelectionModel().getSelectedItem() == tabVenta) {
                Platform.runLater(() -> campoCodigoOculto.requestFocus());
            }
            
            vboxEditarItem.setVisible(false);
            
            
            try {
                conexion = ConexionSingleton.obtenerConexion();
                this.productoDAO = new ProductoDAO(conexion);
                this.usuarioDAO = new UsuarioDAO(conexion);
                this.ventaDAO = new VentaDAO(conexion);
                this.detalleVentaDAO = new DetalleVentaDAO(conexion);
                if (conexion != null) {
                    this.st = conexion.createStatement();
                }
                
                System.out.println("Se ha conectado a la base de datos");
            } catch (SQLException e) {
                System.out.println("No se ha conectado a la base de datos");
            }
            
            btnDescargar = new Button("Descargar");
            
            btnDescargar.setOnAction(event -> descargarImagen());
            
            
            obtenerListaProductos();
            obtenerListaUsuarios();
            obtenerListaVentas();
            obtenerListaSesiones();
            obtenerListaCodigoBarras();
            obtenerListaDetalleVenta();
            inicializarTablasProductos();
            inicializarTablaUsuarios();
            inicializarTablaVentas();
            inicializarTablaSesiones();
            inicializarTablasProductosAdmin();
            inicializarTablaCodigosBarras();
            
            
            configurarComponentesVenta();
            
            System.out.println(codigoProducto);
            
            // Cuando llega el mensaje desde WebSocket
            try {
                clienteWebSocket = new WSClient("ws://54.173.46.205:3001", mensaje -> {
                    System.out.println("Mensaje recibido desde WebSocket: " + mensaje);
                    if ("ventas_actualizadas".equals(mensaje)) {
                        Platform.runLater(() -> {
                            obtenerListaDetalleVenta();
                            tablaVentas.setItems(obtenerListaVentas());
                        });
                    }
                });

                clienteWebSocket.connect();

            } catch (Exception ex) {
                System.out.println("Error en el websocket: " + ex.getMessage());
            }

        
    }
    

    private void configurarComponentesVenta() {
        if (lstProductosEscaneados == null) {
            lstProductosEscaneados = FXCollections.observableArrayList();
        }
        configurarCampoCodigoBarras();

        lblPrecio.setText("0.00");

        try {
            Image imagenCodigoBarras = new Image(getClass().getResourceAsStream("/images/barcode.png"));
            imagenCodigo.setImage(imagenCodigoBarras);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen por defecto del código de barras");
        }
    }

    private void configurarTablaProductosVenta() {
        // Configurar las columnas de la tabla
        tc_imagen.setCellValueFactory(new PropertyValueFactory<>("imagenB64"));
        tc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tc_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tc_precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        tc_precioIVA.setCellValueFactory(new PropertyValueFactory<>("precio_con_iva"));

        tc_imagen.setCellFactory(column -> new TableCell<Producto, ImageView>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(ImageView item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getImage() == null) {
                        setGraphic(null);
                    } else {
                        imageView.setImage(item.getImage());
                        imageView.setFitHeight(60);
                        imageView.setFitWidth(60);
                        imageView.setPreserveRatio(true);
                        setGraphic(imageView);
                    }
                }
            });

        tc_precio.setCellFactory(column -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("€%.2f", item));
                    setAlignment(Pos.CENTER);
                }
            }
        });

        tc_precioIVA.setCellFactory(column -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("€%.2f", item * 1.21)); // IVA del 21%
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void configurarCampoCodigoBarras() {
        campoCodigoOculto.setFocusTraversable(false);

        campoCodigoOculto.setOnAction(event -> {
            String codigo = campoCodigoOculto.getText().trim();

            if (!codigo.isEmpty()) {
                Optional<Producto> productoEncontrado = lstProductos.stream()
                    .filter(p -> p.getCodigo_barras().equals(codigo))
                    .findFirst();

                if (productoEncontrado.isPresent()) {
                    Producto producto = productoEncontrado.get();
                    lstProductosEscaneados.add(producto);
                    totalCompra += producto.getPrecio_con_iva();
                    lblPrecio.setText(totalCompra+"€");

                   
                } else {
                    mostrarAlertaProductoNoEncontrado(codigo);
                }
            }

            campoCodigoOculto.clear();
            Platform.runLater(() -> campoCodigoOculto.requestFocus());
        });

        campoCodigoOculto.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                Platform.runLater(() -> campoCodigoOculto.requestFocus());
            }
        });

        Platform.runLater(() -> campoCodigoOculto.requestFocus());
    }

    private void mostrarAlertaProductoNoEncontrado(String codigo) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Producto no encontrado");
        alert.setHeaderText(null);
        alert.setContentText("El código de barras " + codigo + " no corresponde a ningún producto");
        alert.showAndWait();
    }
    
    
    
}
