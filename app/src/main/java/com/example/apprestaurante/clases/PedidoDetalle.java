package com.example.apprestaurante.clases;

public class PedidoDetalle implements Cloneable{
    int idDetalle;
    boolean cocinando;
    String extras;
    String horaEntregado;
    String horaPedido;
    int idCocinero;
    String cocinero;
    int idProducto;
    String nombre;
    int idPedido;
    int cantidad;
    double precio;
    double subTotal;
    String grupo;
    String usuario;
    String fecha;
    String mesa;
    String cliente;
    String salon;
    String mesero;
    String foto;

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getMesero() {
        return mesero;
    }

    public void setMesero(String mesero) {
        this.mesero = mesero;
    }

    public String getSalon() {
        return salon;
    }

    public void setSalon(String salon) {
        this.salon = salon;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public boolean getCocinando() {
        return cocinando;
    }

    public void setCocinando(boolean cocinando) {
        this.cocinando = cocinando;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getHoraEntregado() {
        return horaEntregado;
    }

    public void setHoraEntregado(String horaEntregado) {
        this.horaEntregado = horaEntregado;
    }

    public String getHoraPedido() {
        return horaPedido;
    }

    public void setHoraPedido(String horaPedido) {
        this.horaPedido = horaPedido;
    }

    public int getIdCocinero() {
        return idCocinero;
    }

    public void setIdCocinero(int idCocinero) {
        this.idCocinero = idCocinero;
    }

    public String getCocinero() {
        return cocinero;
    }

    public void setCocinero(String cocinero) {
        this.cocinero = cocinero;
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

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isCocinando() {
        return cocinando;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    @Override
    public Object clone() {
        try {
            return super.clone(); // Llama al método clone() de la superclase
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
