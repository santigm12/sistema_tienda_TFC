package com.proyectobase.controlador;

import com.proyectobase.modelo.ConexionSingleton;
import com.proyectobase.modelo.Seguridad;
import com.proyectobase.modelo.SessionManager;
import com.proyectobase.modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

    

public class ControladorLogin implements Initializable {
    
    Connection conexion;
    Statement st;
    ResultSet rs;
    ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();;
    Usuario usuarioLogin;
    
    @FXML
    private VBox vboxPrincipal;
    
    @FXML
    private Button btnIniciarSesion;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private TextField txtUsuario;
    
    private void loginAutomatico() {
}

    private static final boolean MODO_DESARROLLO = false;

@FXML
void iniciarSesion(ActionEvent event) {
    if (MODO_DESARROLLO) {
        Usuario usuarioFalso = new Usuario(
            1,
            "dev@ejemplo.com",
            "$2a$10$devHashedPassword1234567890",
            "administrador",
            "NombreDev",
            "ApellidoDev",
            "600123456",
            "Calle Falsa 123",
            new java.util.Date(),
            1
        );

        SessionManager.getInstance().setUsuarioLogueado(usuarioFalso);
        abrirVentanaVenta();
        return;
    }

    String correo = txtUsuario.getText().trim();
    String password = txtContrasena.getText().trim();

    if (correo.isEmpty() || password.isEmpty()) {
        mostrarAlertaError("Error", "Por favor ingrese ambos campos: usuario y contraseña");
        return;
    }

    try {
        if (!obtenerUsuario(correo)) {
            mostrarAlertaError("Error de autenticación", "Usuario no encontrado");
            return;
        }

        String hash = usuarioLogin.getPassword_hash().replace("$2y$", "$2a$");
        if (!BCrypt.checkpw(password, hash)) {
            mostrarAlertaError("Error de autenticación", "Contraseña incorrecta");
            return;
        }

        // Validar rol permitido
        String rol = usuarioLogin.getRol();
        if (!rol.equalsIgnoreCase("empleado") && !rol.equalsIgnoreCase("administrador")) {
            mostrarAlertaError("Acceso denegado", "Su rol no tiene permiso para iniciar sesión");
            return;
        }

        SessionManager.getInstance().setUsuarioLogueado(usuarioLogin);
        abrirVentanaVenta();

    } catch (Exception e) {
        mostrarAlertaError("Error inesperado", "Ocurrió un error durante el inicio de sesión");
        e.printStackTrace();
    }
}



    
    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private ObservableList obtenerListaUsuarios() {
        if (conexion != null) {
            listaUsuarios.clear();
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
                    listaUsuarios.add(usu);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: "+e.getMessage());
            }
            return listaUsuarios;
        }
        return null;
    }
    
    
    private boolean obtenerUsuario(String correo) {
        if (conexion == null) {
            System.out.println("No hay conexión a la base de datos");
            return false;
        }

        String query = "SELECT * FROM usuarios WHERE correo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, correo); 

            try (ResultSet rs = ps.executeQuery()) {  
                if (rs.next()) {
                    usuarioLogin = new Usuario(
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
                    return true;
                }

            }
        } catch (SQLException e) {
            System.out.println("Error al obtener usuario: " + e.getMessage());
        }

        usuarioLogin = null;
        return false;
    }
    
    
    public void abrirVentanaVenta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectobase/vista/ventanaVenta.fxml"));
            Parent root = loader.load();

            ControladorVenta controladorVenta = loader.getController();
            controladorVenta.setUsuarioLogueado(SessionManager.getInstance().getUsuarioLogueado());

            Stage stageActual = (Stage) btnIniciarSesion.getScene().getWindow();
            stageActual.close();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("NovaMarket");
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/logoApp.png")));
            stage.setMaximized(true);

            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar el archivo FXML: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Error al cargar los recursos CSS: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            conexion = ConexionSingleton.obtenerConexion();
            if (conexion != null) {
                this.st = conexion.createStatement();
            }

            System.out.println("Se ha conectado a la base de datos");
        } catch (SQLException e) {
            System.out.println("No se ha conectado a la base de datos");
        }

        
        String password = "usuario";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println(hashedPassword);
        obtenerListaUsuarios();
        System.out.println(listaUsuarios.size());
        System.out.println(listaUsuarios.get(0).getFecha_registro());
    }
}
