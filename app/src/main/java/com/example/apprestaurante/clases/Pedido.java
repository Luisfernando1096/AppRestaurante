package com.example.apprestaurante.clases;

public class Pedido {
    int idPedido;
    int idCliente;
    int idMesa;
    int idCuenta;
    int idMesero;
    boolean cancelado;
    String fecha;
    boolean listo;
    double total;
    double descuento;
    double iva;
    double propina;
    double totalPago;
    double saldo;
    int idTiraje;
    String nFactura;
    boolean anular;
    double efectivo;
    double credito;
    double btc;
    String nombre;
    String nombres;
    String mesa;

    public Pedido() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public int getIdMesero() {
        return idMesero;
    }

    public void setIdMesero(int idMesero) {
        this.idMesero = idMesero;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isListo() {
        return listo;
    }

    public void setListo(boolean listo) {
        this.listo = listo;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getPropina() {
        return propina;
    }

    public void setPropina(double propina) {
        this.propina = propina;
    }

    public double getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(double totalPago) {
        this.totalPago = totalPago;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public int getIdTiraje() {
        return idTiraje;
    }

    public void setIdTiraje(int idTiraje) {
        this.idTiraje = idTiraje;
    }

    public String getnFactura() {
        return nFactura;
    }

    public void setnFactura(String nFactura) {
        this.nFactura = nFactura;
    }

    public boolean isAnular() {
        return anular;
    }

    public void setAnular(boolean anular) {
        this.anular = anular;
    }

    public double getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(double efectivo) {
        this.efectivo = efectivo;
    }

    public double getCredito() {
        return credito;
    }

    public void setCredito(double credito) {
        this.credito = credito;
    }

    public double getBtc() {
        return btc;
    }

    public void setBtc(double btc) {
        this.btc = btc;
    }
}
