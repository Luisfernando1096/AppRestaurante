package com.example.apprestaurante.viewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurante.R;

public class ViewHolderPedidoCuenta1 extends RecyclerView.ViewHolder{

    private TextView tvCantidad;
    private TextView tvProducto;
    private TextView tvSubTotal;

    public TextView getTvCantidad() {
        return tvCantidad;
    }

    public TextView getTvProducto() {
        return tvProducto;
    }

    public TextView getTvSubTotal() {
        return tvSubTotal;
    }

    public ViewHolderPedidoCuenta1(@NonNull View itemView) {
        super(itemView);
        this.tvCantidad = itemView.findViewById(R.id.tvCantidadDiv1);
        this.tvProducto = itemView.findViewById(R.id.tvProductoDiv1);
        this.tvSubTotal = itemView.findViewById(R.id.tvSubTotalDiv1);
    }
}
