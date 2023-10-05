package com.example.apprestaurante.utils;

import com.example.apprestaurante.clases.PedidoDetalle;

import java.util.List;

public class Calculos {
    double total;
    double subTotal;

    public double CalcularTotal(List<PedidoDetalle> lstPedidos)
    {
        if (lstPedidos != null)
        {
            for (PedidoDetalle pDetalle: lstPedidos) {
                total += pDetalle.getSubTotal();
            }
        }
        return total;
    }

    public double CalcularSubTotal(int cantidad, double precio)
    {
        subTotal = precio * cantidad;
        return subTotal;
    }

}
