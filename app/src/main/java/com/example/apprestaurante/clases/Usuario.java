package com.example.apprestaurante.clases;

public class Usuario {
    int idEmpleado;
    int idUsuario;
    String rol;
    int idRol;
    String username;

    public Usuario(int idEmpleado, int idUsuario, String rol, int idRol, String username) {
        this.idEmpleado = idEmpleado;
        this.idUsuario = idUsuario;
        this.rol = rol;
        this.idRol = idRol;
        this.username = username;
    }

    public Usuario() {
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
