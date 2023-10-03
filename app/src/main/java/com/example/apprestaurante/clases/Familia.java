package com.example.apprestaurante.clases;

public class Familia {
    int idFamilia;
    int activo;
    String nombre;
    String grupoPrinter;

    public Familia() {
    }

    public Familia(int idFamilia, int activo, String nombre, String grupoPrinter) {
        this.idFamilia = idFamilia;
        this.activo = activo;
        this.nombre = nombre;
        this.grupoPrinter = grupoPrinter;
    }

    public int getIdFamilia() {
        return idFamilia;
    }

    public void setIdFamilia(int idFamilia) {
        this.idFamilia = idFamilia;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGrupoPrinter() {
        return grupoPrinter;
    }

    public void setGrupoPrinter(String grupoPrinter) {
        this.grupoPrinter = grupoPrinter;
    }
}
