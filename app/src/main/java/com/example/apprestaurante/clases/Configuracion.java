package com.example.apprestaurante.clases;

public class Configuracion {
    private int idConfiguracion;
    private int controlStock;
    private int incluirPropina;
    private double propina;
    private int incluirImpuesto;
    private double iva;
    private double mesaVIP;
    private int autorizarDescProp;
    private String printerComanda;
    private String printerFactura;
    private String printerInformes;
    private int alertaCaja;
    private int multisesion;
    private int numSesiones;
    private int muchosProductos;

    public int getIdConfiguracion() {
        return idConfiguracion;
    }

    public void setIdConfiguracion(int idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }

    public int getControlStock() {
        return controlStock;
    }

    public void setControlStock(int controlStock) {
        this.controlStock = controlStock;
    }

    public int getIncluirPropina() {
        return incluirPropina;
    }

    public void setIncluirPropina(int incluirPropina) {
        this.incluirPropina = incluirPropina;
    }

    public double getPropina() {
        return propina;
    }

    public void setPropina(double propina) {
        this.propina = propina;
    }

    public int getIncluirImpuesto() {
        return incluirImpuesto;
    }

    public void setIncluirImpuesto(int incluirImpuesto) {
        this.incluirImpuesto = incluirImpuesto;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getMesaVIP() {
        return mesaVIP;
    }

    public void setMesaVIP(double mesaVIP) {
        this.mesaVIP = mesaVIP;
    }

    public int getAutorizarDescProp() {
        return autorizarDescProp;
    }

    public void setAutorizarDescProp(int autorizarDescProp) {
        this.autorizarDescProp = autorizarDescProp;
    }

    public String getPrinterComanda() {
        return printerComanda;
    }

    public void setPrinterComanda(String printerComanda) {
        this.printerComanda = printerComanda;
    }

    public String getPrinterFactura() {
        return printerFactura;
    }

    public void setPrinterFactura(String printerFactura) {
        this.printerFactura = printerFactura;
    }

    public String getPrinterInformes() {
        return printerInformes;
    }

    public void setPrinterInformes(String printerInformes) {
        this.printerInformes = printerInformes;
    }

    public int getAlertaCaja() {
        return alertaCaja;
    }

    public void setAlertaCaja(int alertaCaja) {
        this.alertaCaja = alertaCaja;
    }

    public int getMultisesion() {
        return multisesion;
    }

    public void setMultisesion(int multisesion) {
        this.multisesion = multisesion;
    }

    public int getNumSesiones() {
        return numSesiones;
    }

    public void setNumSesiones(int numSesiones) {
        this.numSesiones = numSesiones;
    }

    public int getMuchosProductos() {
        return muchosProductos;
    }

    public void setMuchosProductos(int muchosProductos) {
        this.muchosProductos = muchosProductos;
    }
}
