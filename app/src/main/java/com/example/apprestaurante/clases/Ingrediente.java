package com.example.apprestaurante.clases;

public class Ingrediente {
    int idProducto;
    int idIngrediente;
    int cantidad;
    int stock_ingrediente;
    int stock_producto;

    public Ingrediente() {
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(int idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getStock_ingrediente() {
        return stock_ingrediente;
    }

    public void setStock_ingrediente(int stock_ingrediente) {
        this.stock_ingrediente = stock_ingrediente;
    }

    public int getStock_producto() {
        return stock_producto;
    }

    public void setStock_producto(int stock_producto) {
        this.stock_producto = stock_producto;
    }
}
