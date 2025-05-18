package com.proyectobase.modelo;

public class SessionManager {
    private static final SessionManager instance = new SessionManager();
    private Usuario usuarioLogueado;

    private SessionManager() {}

    public static SessionManager getInstance() {
        return instance;
    }

    public Usuario getUsuarioLogueado() {
        if (usuarioLogueado == null) {
            throw new IllegalStateException("Usuario no ha iniciado sesión");
        }
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no puede ser null");
        }
        this.usuarioLogueado = usuario;
    }
    
    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }
}
