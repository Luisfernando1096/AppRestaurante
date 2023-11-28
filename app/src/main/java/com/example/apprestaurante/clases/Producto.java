package com.example.apprestaurante.clases;

public class Producto {
    int idProducto;
    String nombre;
    String descripcion;
    double precio;
    String foto;
    double costo;
    int inventariable;
    int conIngrediente;
    int stock;
    int stockMinimo;
    int activo;
    int idFamilia;
    String familia;
    String grupoPrinter;

    public Producto() {
    }

    public Producto(int idProducto, String nombre, String descripcion, String foto, double precio, double costo, int inventariable, int conIngrediente, int stock, int stockMinimo, int activo, int idFamilia, String familia, String grupoPrinter) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.costo = costo;
        this.foto = foto;
        this.inventariable = inventariable;
        this.conIngrediente = conIngrediente;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.activo = activo;
        this.idFamilia = idFamilia;
        this.familia = familia;
        this.grupoPrinter = grupoPrinter;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }
    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getInventariable() {
        return inventariable;
    }

    public void setInventariable(int inventariable) {
        this.inventariable = inventariable;
    }

    public int getConIngrediente() {
        return conIngrediente;
    }

    public void setConIngrediente(int conIngrediente) {
        this.conIngrediente = conIngrediente;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public int getIdFamilia() {
        return idFamilia;
    }

    public void setIdFamilia(int idFamilia) {
        this.idFamilia = idFamilia;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public String getGrupoPrinter() {
        return grupoPrinter;
    }

    public void setGrupoPrinter(String grupoPrinter) {
        this.grupoPrinter = grupoPrinter;
    }
}
