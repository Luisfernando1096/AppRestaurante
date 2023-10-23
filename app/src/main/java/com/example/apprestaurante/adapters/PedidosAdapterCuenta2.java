package com.example.apprestaurante.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurante.R;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.viewHolders.ViewHolderPedidoCuenta2;

import java.util.ArrayList;
import java.util.List;

public class PedidosAdapterCuenta2 extends RecyclerView.Adapter<ViewHolderPedidoCuenta2>{
    private List<PedidoDetalle> datos;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public PedidosAdapterCuenta2(List<PedidoDetalle> datos) {
        this.datos = datos;
    }

    @NonNull
    @Override
    public ViewHolderPedidoCuenta2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dividir_pedidos, parent,false);
        return new ViewHolderPedidoCuenta2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPedidoCuenta2 holder, int position) {
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
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemLongClickListener != null){
                    onItemLongClickListener.onItemLongClick(pedidoDetalleItem);
                }
                return true;
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
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(PedidoDetalle pedidoDetalle);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }
}
