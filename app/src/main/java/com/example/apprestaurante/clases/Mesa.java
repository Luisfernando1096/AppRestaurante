package com.example.apprestaurante.clases;

public class Mesa {
    int idMesa;
    int numero;
    String nombre;
    int capacidad;
    Boolean disponible;
    int idSalon;

    String salon;

    public Mesa() {
    }

    public Mesa(int idMesa, int numero, String nombre, int capacidad, Boolean disponible, int idSalon) {
        this.idMesa = idMesa;
        this.numero = numero;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.disponible = disponible;
        this.idSalon = idSalon;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public int getIdSalon() {
        return idSalon;
    }

    public void setIdSalon(int idSalon) {
        this.idSalon = idSalon;
    }

    public String getSalon() {
        return salon;
    }

    public void setSalon(String salon) {
        this.salon = salon;
    }
}
