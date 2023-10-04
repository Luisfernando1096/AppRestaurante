package com.example.apprestaurante.adapters;

import static com.example.apprestaurante.ComandaGestion.lstPedidos;
import static com.google.gson.internal.$Gson$Types.arrayOf;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurante.R;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.viewHolders.ViewHolderPedidos;

import java.util.ArrayList;
import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<ViewHolderPedidos>{
    private List<PedidoDetalle> datos;
    private OnItemClickListener onItemClickListener;

    public PedidosAdapter(List<PedidoDetalle> datos, OnItemClickListener itemClickListener) {
        this.datos = datos;
        this.onItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolderPedidos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mostrar_pedidos, parent,false);
        return new ViewHolderPedidos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPedidos holder, int position) {
        final PedidoDetalle pedidoDetalleItem = datos.get(position);
        System.out.println("Cantidad : " + datos.get(position).getCantidad());
        holder.getTvCantidad().setText(datos.get(position).getCantidad() + "");
        holder.getTvProducto().setText(datos.get(position).getNombre() + "");
        holder.getTvSubTotal().setText(datos.get(position).getSubTotal() + "");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(pedidoDetalleItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public interface OnItemClickListener {
        void onItemClick(PedidoDetalle pedidoDetalle);
    }
}
