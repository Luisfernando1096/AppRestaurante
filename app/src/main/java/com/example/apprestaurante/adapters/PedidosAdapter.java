package com.example.apprestaurante.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurante.R;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.services.ProductoService;
import com.example.apprestaurante.viewHolders.ViewHolderPedidos;

import java.text.DecimalFormat;
import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<ViewHolderPedidos>{
    private List<PedidoDetalle> datos;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private PedidosAdapter.OnItemLongClickListener onItemLongClickListener;

    public PedidosAdapter(Context context, List<PedidoDetalle> datos, OnItemClickListener itemClickListener) {
        this.context = context;
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
        // Crear un ImageView
        ImageView imageView = holder.getIvImagen();

        // Recuperar la imagen y asignarla directamente al botón
        ProductoService productoService = new ProductoService();
        productoService.recuperarImagen(datos.get(position).getFoto() + "", new ProductoService.OnImageLoadListener() {
            @Override
            public void onImageLoad(Bitmap bitmap) {
                // Redimensionar el Bitmap al tamaño deseado
                int width = 200; // Ancho en píxeles
                int height = 200; // Alto en píxeles
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

                // Asignar el Bitmap como fuente del ImageView
                imageView.setImageBitmap(resizedBitmap);

                // Ajustar la escala del ImageView según el tamaño del Bitmap
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

            @Override
            public void onImageLoadError(String errorMessage) {// Manejar el caso en el que la recuperación de la imagen falló
                // Redimensionar la imagen por defecto al tamaño deseado
                int defaultWidth = 200; // Ancho en píxeles
                int defaultHeight = 200; // Alto en píxeles
                Drawable defaultDrawable = context.getResources().getDrawable(R.drawable.productos);
                // Ajustar el tamaño de la imagen por defecto
                defaultDrawable.setBounds(0, 0, defaultWidth, defaultHeight);
                // Asignar la imagen por defecto encima del texto en el botón
                imageView.setImageDrawable(defaultDrawable);

            }
        });

        holder.getTvCantidad().setText(datos.get(position).getCantidad() + "");
        holder.getTvProducto().setText(datos.get(position).getNombre() + "");
        holder.getBtnDisminuir().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(pedidoDetalleItem);
                }
            }
        });

        DecimalFormat df = new DecimalFormat("#.00");

        holder.getTvSubTotal().setText(df.format(datos.get(position).getSubTotal()) + "");

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
    public interface OnItemLongClickListener {
        void onItemLongClick(PedidoDetalle pedidoDetalle);
    }

    public void setOnItemLongClickListener(PedidosAdapter.OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }
}
