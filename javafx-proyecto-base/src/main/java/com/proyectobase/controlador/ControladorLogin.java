package com.proyectobase.controlador;

import com.proyectobase.modelo.ConexionSingleton;
import com.proyectobase.modelo.Seguridad;
import com.proyectobase.modelo.SessionManager;
import com.proyectobase.modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
    private TextField txtContrasena;

    @FXML
    private TextField txtUsuario;

    @FXML
        void iniciarSesion(ActionEvent event) {
        String correo = txtUsuario.getText().trim();
        String password = txtContrasena.getText().trim();

        if (!obtenerUsuario(correo)) {
            System.out.println("Usuario no encontrado");
            return;
        }

        if (!BCrypt.checkpw(password, usuarioLogin.getPassword_hash())) {
            System.out.println("Contraseña incorrecta");
            return;
        }

        // Solo llamar una vez a abrirVentanaVenta()
        SessionManager.getInstance().setUsuarioLogueado(usuarioLogin);
        abrirVentanaVenta();
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

            // Obtener el controlador y configurar el usuario
            ControladorVenta controladorVenta = loader.getController();
            controladorVenta.setUsuarioLogueado(SessionManager.getInstance().getUsuarioLogueado());

            Stage stageActual = (Stage) btnIniciarSesion.getScene().getWindow();
            stageActual.close();

            Stage stage = new Stage();
            stage.setTitle("Venta");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar el archivo FXML: " + e.getMessage());
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
