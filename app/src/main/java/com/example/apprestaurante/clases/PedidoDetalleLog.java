package com.example.apprestaurante.clases;

public class PedidoDetalleLog {
    int idDeleted, idDetalle, idCocinero, idProducto, idPedido, cantidad, usuarioDelete;
    Boolean cocinando;
    String extras, horaEntregado, horaPedido, grupo, fechaDelete;
    Double precio, subTotal;

    public PedidoDetalleLog() {
    }

    public int getIdDeleted() {
        return idDeleted;
    }

    public void setIdDeleted(int idDeleted) {
        this.idDeleted = idDeleted;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Boolean getCocinando() {
        return cocinando;
    }

    public void setCocinando(Boolean cocinando) {
        this.cocinando = cocinando;
    }

    public int getIdCocinero() {
        return idCocinero;
    }

    public void setIdCocinero(int idCocinero) {
        this.idCocinero = idCocinero;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
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

    public int getUsuarioDelete() {
        return usuarioDelete;
    }

    public void setUsuarioDelete(int usuarioDelete) {
        this.usuarioDelete = usuarioDelete;
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

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public String getFechaDelete() {
        return fechaDelete;
    }

    public void setFechaDelete(String fechaDelete) {
        this.fechaDelete = fechaDelete;
    }
}
