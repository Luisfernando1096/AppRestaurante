package com.example.apprestaurante.clases;

public class Salon {
    /*DECLARAR LAS VARIABLES*/
    int idSalon;
    String nombre;
    String fondo;
    int nMesas;

    public Salon() {
    }

    public Salon(int idSalon, String nombre, String fondo, int nMesas) {
        this.idSalon = idSalon;
        this.nombre = nombre;
        this.fondo = fondo;
        this.nMesas = nMesas;
    }

    public int getIdSalon() {
        return idSalon;
    }

    public void setIdSalon(int idSalon) {
        this.idSalon = idSalon;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFondo() {
        return fondo;
    }

    public void setFondo(String fondo) {
        this.fondo = fondo;
    }

    public int getnMesas() {
        return nMesas;
    }

    public void setnMesas(int nMesas) {
        this.nMesas = nMesas;
    }
}
