package com.example.apprestaurante;

import static com.example.apprestaurante.ComandaGestion.lstPedidos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apprestaurante.adapters.PedidosAdapterCuenta1;
import com.example.apprestaurante.adapters.PedidosAdapterCuenta2;
import com.example.apprestaurante.clases.PedidoDetalle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DividirPedidos extends AppCompatActivity {
    private String origenClic; // Variable para rastrear el origen del clic

    RecyclerView rcvPedidosActual, rcvPedidosSiguiente;
    private LinearLayoutManager layoutManagerActual, layoutManagerSiguiente;
    List<PedidoDetalle> lstPedidosActual;
    List<PedidoDetalle> lstPedidosSiguiente;
    List<PedidoDetalle> lstPedidoGuardadosR1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dividir_pedidos);
        lstPedidoGuardadosR1 = new ArrayList<>(); // Inicializa la lista lstPedidoGuardados

        CargarPedidosDetalle1BD();
        CargarPedidosDetalle2();
    }

    private void CargarPedidosDetalle1BD(){
        if(lstPedidos.size() > 0){
            // Configurando adaptador
            lstPedidosActual = new ArrayList<>(lstPedidos);
            PedidosAdapterCuenta1 pedidosAdapter = new PedidosAdapterCuenta1(lstPedidosActual);
            layoutManagerActual = new LinearLayoutManager(this);
            rcvPedidosActual = findViewById(R.id.rcvPedidos1);
            rcvPedidosActual.setAdapter(pedidosAdapter);
            rcvPedidosActual.setLayoutManager(layoutManagerActual);
            rcvPedidosActual.setHasFixedSize(true);

            if (rcvPedidosActual != null){
                PedidosAdapterCuenta1 pedidosAdapterC1 = (PedidosAdapterCuenta1) rcvPedidosActual.getAdapter();
                pedidosAdapterC1.setOnItemClickListener(new PedidosAdapterCuenta1.OnItemClickListener() {
                    @Override
                    public void onItemClick(PedidoDetalle pedidoDetalle) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DividirPedidos.this);
                        TextView title = new TextView(DividirPedidos.this);
                        title.setText("Ingrese la cantidad del producto: " + pedidoDetalle.getNombre());
                        title.setTextSize(16);
                        title.setPadding(20, 20, 20, 20);
                        title.setGravity(Gravity.CENTER);
                        builder.setCustomTitle(title);
                        final EditText cantidadInput = new EditText(DividirPedidos.this);
                        cantidadInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                        builder.setView(cantidadInput);

                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String cantidadStr = cantidadInput.getText().toString();
                                int cantidadUsuario = Integer.parseInt(cantidadStr);
                                int positionActutal = lstPedidosActual.indexOf(pedidoDetalle);
                                int cantidadActual = pedidoDetalle.getCantidad();

                                if ( !cantidadStr.isEmpty() && cantidadUsuario >= 1 && cantidadUsuario <= cantidadActual) {
                                    boolean found = false;
                                    for (PedidoDetalle detalleR2 : lstPedidoGuardadosR1) {
                                        //SI EXISTE EL PRODCUTO EM LA LISTA
                                        if (detalleR2.getNombre().equals(pedidoDetalle.getNombre())) {
                                            found = true;
                                            if (cantidadActual == cantidadUsuario) {
                                                Toast.makeText(DividirPedidos.this, "R12", Toast.LENGTH_SHORT).show();
                                                // Actualiza la cantidad en el RecyclerView 2
                                                for (PedidoDetalle Detalle : lstPedidoGuardadosR1){
                                                    if (Detalle.getNombre().equals(pedidoDetalle.getNombre())){
                                                        Detalle.setCantidad(cantidadUsuario + detalleR2.getCantidad());
                                                        Detalle.setSubTotal(Detalle.getPrecio() * Detalle.getCantidad());
                                                        break;
                                                    }
                                                }
                                                CargarPedidosDetalle2();
                                                //Eliminar el elemento de la posicion acyual del recyler 1
                                                int positionToRemove = -1; // Inicializa una variable para rastrear la posición del elemento a eliminar
                                                for (int i = 0; i < lstPedidos.size(); i++) {
                                                    PedidoDetalle detalleActual = lstPedidos.get(i);
                                                    if (pedidoDetalle.getNombre().equals(detalleActual.getNombre())) {
                                                        positionToRemove = i; // Almacena la posición del elemento a eliminar
                                                        break; // Sale del bucle una vez que se ha encontrado el elemento
                                                    }
                                                }
                                                if (positionToRemove != -1) {
                                                    lstPedidos.remove(positionToRemove); // Elimina el elemento de la lista
                                                    rcvPedidosActual.getAdapter().notifyItemRemoved(positionToRemove); // Notifica al adaptador del RecyclerView1 que se ha eliminado un elemento
                                                }
                                                // Limpiar lstPedidosSiguiente (opcional)
                                                lstPedidosActual.clear();
                                                // Copiar elementos de lstPedidos a lstPedidoActuales
                                                for (PedidoDetalle detalle : lstPedidos) {
                                                    lstPedidosActual.add(detalle);
                                                }
                                                // Notificar al adaptador que los datos han cambiado
                                                if (rcvPedidosActual.getAdapter() != null) {
                                                    rcvPedidosActual.getAdapter().notifyDataSetChanged();
                                                }

                                                CargarPedidosDetalle1BD();
                                                lstPedidos.size();
                                                lstPedidoGuardadosR1.size();
                                            } else { //FUNCIONANDO
                                                Toast.makeText(DividirPedidos.this, "R12", Toast.LENGTH_SHORT).show();
                                                // Actualiza la cantidad en el RecyclerView1
                                                int nuevaCantidadR1 = cantidadActual - cantidadUsuario;
                                                pedidoDetalle.setCantidad(nuevaCantidadR1);
                                                pedidoDetalle.setSubTotal(pedidoDetalle.getCantidad() * pedidoDetalle.getPrecio());
                                                //pedidoDetalle.setSubTotal(nuevaCantidadR1 * pedidoDetalle.getPrecio());
                                                rcvPedidosActual.getAdapter().notifyItemChanged(positionActutal); // Notifica al adaptador del RecyclerView1 que la cantidad ha cambiado
                                                CargarPedidosDetalle2();

                                                // Busca la posición del elemento en el RecyclerView 2
                                                for (PedidoDetalle Detalle : lstPedidoGuardadosR1){
                                                    if (Detalle.getNombre().equals(pedidoDetalle.getNombre())){
                                                        Detalle.setCantidad(cantidadUsuario + detalleR2.getCantidad());
                                                        Detalle.setSubTotal(Detalle.getCantidad() * Detalle.getPrecio());
                                                        break;
                                                    }
                                                    CargarPedidosDetalle2();
                                                }
                                                CargarPedidosDetalle1BD();
                                                //lstPedidos.size();
                                                //lstPedidoGuardadosR1.size();
                                            }
                                            break;
                                        }
                                    }

                                    if (!found) {
                                        // SI NO EXISTE EL PRODUCTSO EM LA LISTA
                                        if (cantidadActual == cantidadUsuario) { //FUNCIONADO
                                            // Agregamos al recycler 2
                                            lstPedidoGuardadosR1.add(pedidoDetalle);
                                            CargarPedidosDetalle2();

                                            int positionToRemove = -1; // Inicializa una variable para rastrear la posición del elemento a eliminar
                                            for (int i = 0; i < lstPedidos.size(); i++) {
                                                PedidoDetalle detalleActual = lstPedidos.get(i);
                                                if (pedidoDetalle.getNombre().equals(detalleActual.getNombre())) {
                                                    positionToRemove = i; // Almacena la posición del elemento a eliminar
                                                    break; // Sale del bucle una vez que se ha encontrado el elemento
                                                }
                                            }
                                            if (positionToRemove != -1) {
                                                lstPedidos.remove(positionToRemove); // Elimina el elemento de la lista
                                                rcvPedidosActual.getAdapter().notifyItemRemoved(positionToRemove); // Notifica al adaptador del RecyclerView1 que se ha eliminado un elemento
                                            }
                                            // Limpiar lstPedidosSiguiente (opcional)
                                            lstPedidosActual.clear();
                                            // Copiar elementos de lstPedidos a lstPedidoActuales
                                            for (PedidoDetalle detalle : lstPedidos) {
                                                lstPedidosActual.add(detalle);
                                            }
                                            // Notificar al adaptador que los datos han cambiado
                                            if (rcvPedidosActual.getAdapter() != null) {
                                                rcvPedidosActual.getAdapter().notifyDataSetChanged();
                                            }

                                            CargarPedidosDetalle1BD();
                                            lstPedidos.size();
                                            lstPedidoGuardadosR1.size();
                                        } else { //FUNCIONANDO
                                            // Actualiza la cantidad en el RecyclerView1
                                            int nuevaCantidadR1 = cantidadActual - cantidadUsuario;
                                            pedidoDetalle.setCantidad(nuevaCantidadR1);
                                            pedidoDetalle.setSubTotal(pedidoDetalle.getPrecio() * pedidoDetalle.getCantidad());
                                            rcvPedidosActual.getAdapter().notifyItemChanged(positionActutal);

                                            // Crea una copia de pedidoDetalle con la cantidad actualizada para agregar al RecyclerView2
                                            PedidoDetalle copiaPedidoDetalle = new PedidoDetalle();
                                            copiaPedidoDetalle.setIdDetalle(pedidoDetalle.getIdDetalle());
                                            copiaPedidoDetalle.setCocinando(pedidoDetalle.getCocinando());
                                            copiaPedidoDetalle.setExtras(pedidoDetalle.getExtras());
                                            copiaPedidoDetalle.setHoraEntregado(pedidoDetalle.getHoraEntregado());
                                            copiaPedidoDetalle.setHoraPedido(pedidoDetalle.getHoraPedido());
                                            copiaPedidoDetalle.setIdCocinero(pedidoDetalle.getIdCocinero());
                                            copiaPedidoDetalle.setCocinero(pedidoDetalle.getCocinero());
                                            copiaPedidoDetalle.setIdProducto(pedidoDetalle.getIdProducto());
                                            copiaPedidoDetalle.setNombre(pedidoDetalle.getNombre());
                                            copiaPedidoDetalle.setIdPedido(pedidoDetalle.getIdPedido());
                                            copiaPedidoDetalle.setCantidad(cantidadUsuario);
                                            copiaPedidoDetalle.setPrecio(pedidoDetalle.getPrecio());
                                            copiaPedidoDetalle.setSubTotal(copiaPedidoDetalle.getPrecio() * copiaPedidoDetalle.getCantidad());
                                            copiaPedidoDetalle.setGrupo(pedidoDetalle.getGrupo());
                                            copiaPedidoDetalle.setUsuario(pedidoDetalle.getUsuario());
                                            copiaPedidoDetalle.setFecha(pedidoDetalle.getFecha());

                                            // Agrega el elemento modificado al RecyclerView2
                                            lstPedidoGuardadosR1.add(copiaPedidoDetalle);
                                            CargarPedidosDetalle2();
                                            lstPedidoGuardadosR1.size();
                                            lstPedidos.size();
                                        }
                                    }
                                } else {
                                    Toast.makeText(DividirPedidos.this, "Cantidad no válida", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(DividirPedidos.this, "Entrada cancelada", Toast.LENGTH_SHORT).show();
                            }
                        });

                        builder.show();
                    }
                });
            }
        }
    }
    private void CargarPedidosDetalle2() {
        if (lstPedidoGuardadosR1.size() > 0) {
            // Configurar el adaptador para la lista de pedidos guardados en R1
            lstPedidosSiguiente = new ArrayList<>(lstPedidoGuardadosR1);
            PedidosAdapterCuenta2 pedidosAdapter = new PedidosAdapterCuenta2(lstPedidosSiguiente);
            layoutManagerSiguiente = new LinearLayoutManager(this);
            rcvPedidosSiguiente = findViewById(R.id.rcvPedidos2);
            rcvPedidosSiguiente.setAdapter(pedidosAdapter);
            rcvPedidosSiguiente.setLayoutManager(layoutManagerSiguiente);
            rcvPedidosSiguiente.setHasFixedSize(true);

            if (rcvPedidosSiguiente != null) {
                PedidosAdapterCuenta2 pedidosAdapterC2 = (PedidosAdapterCuenta2) rcvPedidosSiguiente.getAdapter();
                pedidosAdapterC2.setOnItemClickListener(new PedidosAdapterCuenta2.OnItemClickListener() {
                    @Override
                    public void onItemClick(PedidoDetalle pedidoDetalle) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DividirPedidos.this);
                        TextView title = new TextView(DividirPedidos.this);
                        title.setText("Ingrese la cantidad del producto: " + pedidoDetalle.getNombre());
                        title.setTextSize(16);
                        title.setPadding(20, 20, 20, 20);
                        title.setGravity(Gravity.CENTER);
                        builder.setCustomTitle(title);
                        final EditText cantidadInput = new EditText(DividirPedidos.this);
                        cantidadInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                        builder.setView(cantidadInput);

                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String cantidadStr = cantidadInput.getText().toString();
                                int cantidadUsuario = Integer.parseInt(cantidadStr);
                                int positionActutal = lstPedidosActual.indexOf(pedidoDetalle);
                                int cantidadActual = pedidoDetalle.getCantidad();

                                if ( !cantidadStr.isEmpty() && cantidadUsuario >= 1 && cantidadUsuario <= cantidadActual) {
                                    boolean found = false;
                                    for (PedidoDetalle detalleR2 : lstPedidos) {
                                        //SI EXISTE EL PRODCUTO EM LA LISTA
                                        if (detalleR2.getNombre().equals(pedidoDetalle.getNombre())) { ///FUNCIONADO
                                            found = true;
                                            if (pedidoDetalle.getCantidad() == cantidadUsuario) {
                                                Toast.makeText(DividirPedidos.this, "R12", Toast.LENGTH_SHORT).show();
                                                // Actualiza la cantidad en el RecyclerView1
                                                for (PedidoDetalle Detalle : lstPedidos){
                                                    if (Detalle.getNombre().equals(pedidoDetalle.getNombre())){
                                                        Detalle.setCantidad(cantidadUsuario + detalleR2.getCantidad());
                                                        Detalle.setSubTotal(Detalle.getCantidad() * Detalle.getPrecio());
                                                        break;
                                                    }
                                                }
                                                CargarPedidosDetalle1BD();
                                                //Eliminar el elemento de la posicion acyual
                                                int positionToRemove = -1; // Inicializa una variable para rastrear la posición del elemento a eliminar
                                                for (int i = 0; i < lstPedidoGuardadosR1.size(); i++) {
                                                    PedidoDetalle detalleActual = lstPedidoGuardadosR1.get(i);
                                                    if (pedidoDetalle.getNombre().equals(detalleActual.getNombre())) {
                                                        positionToRemove = i; // Almacena la posición del elemento a eliminar
                                                        break; // Sale del bucle una vez que se ha encontrado el elemento
                                                    }
                                                }
                                                if (positionToRemove != -1) {
                                                    lstPedidoGuardadosR1.remove(positionToRemove); // Elimina el elemento de la lista
                                                    rcvPedidosSiguiente.getAdapter().notifyItemRemoved(positionToRemove); // Notifica al adaptador del RecyclerView1 que se ha eliminado un elemento
                                                }
                                                // Limpiar lstPedidosSiguiente (opcional)
                                                lstPedidosSiguiente.clear();
                                                // Copiar elementos de lstPedidosGuardadosR1 a lstPedidosSiguiente
                                                for (PedidoDetalle detalle : lstPedidoGuardadosR1) {
                                                    lstPedidosSiguiente.add(detalle);
                                                }
                                                // Notificar al adaptador que los datos han cambiado
                                                if (rcvPedidosSiguiente.getAdapter() != null) {
                                                    rcvPedidosSiguiente.getAdapter().notifyDataSetChanged();
                                                }

                                                CargarPedidosDetalle2();
                                            } else { ///FUNCIONANDO
                                                Toast.makeText(DividirPedidos.this, "R12", Toast.LENGTH_SHORT).show();
                                                // Actualiza la cantidad en el RecyclerView2
                                                int nuevaCantidadR1 = cantidadActual - cantidadUsuario;
                                                pedidoDetalle.setCantidad(nuevaCantidadR1);
                                                pedidoDetalle.setSubTotal(pedidoDetalle.getCantidad() * pedidoDetalle.getPrecio());
                                                //pedidoDetalle.setSubTotal(nuevaCantidadR1 * pedidoDetalle.getPrecio());
                                                rcvPedidosSiguiente.getAdapter().notifyItemChanged(positionActutal); // Notifica al adaptador del RecyclerView1 que la cantidad ha cambiado
                                                CargarPedidosDetalle1BD();

                                                // Busca la posición del elemento en el RecyclerView1
                                                for (PedidoDetalle Detalle : lstPedidos){
                                                    if (Detalle.getNombre().equals(pedidoDetalle.getNombre())){
                                                        Detalle.setCantidad(cantidadUsuario + detalleR2.getCantidad());
                                                        Detalle.setSubTotal(Detalle.getCantidad() * Detalle.getPrecio());
                                                        break;
                                                    }
                                                    CargarPedidosDetalle1BD();
                                                }
                                                CargarPedidosDetalle2();
                                                //lstPedidos.size();
                                                //lstPedidoGuardadosR1.size();
                                            }
                                            break;
                                        }
                                    }

                                    if (!found) {
                                        // SI NO EXISTE EL PRODUCTSO EM LA LISTA
                                        if (cantidadActual == cantidadUsuario) { ///FUNCIONANDO
                                            // Agregamos al recycler 1
                                            lstPedidos.add(pedidoDetalle);
                                            CargarPedidosDetalle1BD();

                                            int positionToRemove = -1; // Inicializa una variable para rastrear la posición del elemento a eliminar
                                            for (int i = 0; i < lstPedidoGuardadosR1.size(); i++) {
                                                PedidoDetalle detalleActual = lstPedidoGuardadosR1.get(i);
                                                if (pedidoDetalle.getNombre().equals(detalleActual.getNombre())) {
                                                    positionToRemove = i; // Almacena la posición del elemento a eliminar
                                                    break; // Sale del bucle una vez que se ha encontrado el elemento
                                                }
                                            }
                                            CargarPedidosDetalle2();
                                            if (positionToRemove != -1) {
                                                lstPedidoGuardadosR1.remove(positionToRemove); // Elimina el elemento de la lista
                                                rcvPedidosSiguiente.getAdapter().notifyItemRemoved(positionToRemove); // Notifica al adaptador del RecyclerView1 que se ha eliminado un elemento
                                            }
                                            // Limpiar lstPedidosSiguiente (opcional)
                                            lstPedidosSiguiente.clear();
                                            // Copiar elementos de lstPedidosGuardadosR1 a lstPedidosSiguiente
                                            for (PedidoDetalle detalle : lstPedidoGuardadosR1) {
                                                lstPedidosSiguiente.add(detalle);
                                            }
                                            // Notificar al adaptador que los datos han cambiado
                                            if (rcvPedidosSiguiente.getAdapter() != null) {
                                                rcvPedidosSiguiente.getAdapter().notifyDataSetChanged();
                                            }

                                            CargarPedidosDetalle2();
                                            //lstPedidoGuardadosR1.size();
                                            //lstPedidosSiguiente.size();
                                        } else { ///FUNCIONANDO
                                            // Crea una copia del elemento actual
                                            PedidoDetalle copiaElemento = new PedidoDetalle();
                                            copiaElemento.setIdDetalle(pedidoDetalle.getIdDetalle());
                                            copiaElemento.setCocinando(pedidoDetalle.getCocinando());
                                            copiaElemento.setExtras(pedidoDetalle.getExtras());
                                            copiaElemento.setHoraEntregado(pedidoDetalle.getHoraEntregado());
                                            copiaElemento.setHoraPedido(pedidoDetalle.getHoraPedido());
                                            copiaElemento.setIdCocinero(pedidoDetalle.getIdCocinero());
                                            copiaElemento.setCocinero(pedidoDetalle.getCocinero());
                                            copiaElemento.setIdProducto(pedidoDetalle.getIdProducto());
                                            copiaElemento.setNombre(pedidoDetalle.getNombre());
                                            copiaElemento.setIdPedido(pedidoDetalle.getIdPedido());
                                            copiaElemento.setCantidad(cantidadUsuario);
                                            copiaElemento.setPrecio(pedidoDetalle.getPrecio());
                                            copiaElemento.setSubTotal(copiaElemento.getPrecio() * copiaElemento.getCantidad());
                                            copiaElemento.setGrupo(pedidoDetalle.getGrupo());
                                            copiaElemento.setUsuario(pedidoDetalle.getUsuario());
                                            copiaElemento.setFecha(pedidoDetalle.getFecha());

                                            // Agrega la copia a la nueva lista
                                            lstPedidos.add(copiaElemento);
                                            // Actualiza la cantidad del elemento original en 'lstPedidoGuardadosR1'
                                            pedidoDetalle.setCantidad(pedidoDetalle.getCantidad() - cantidadUsuario);
                                            pedidoDetalle.setSubTotal(pedidoDetalle.getPrecio() * pedidoDetalle.getCantidad());

                                            CargarPedidosDetalle1BD();
                                            CargarPedidosDetalle2();
                                            lstPedidos.size();
                                            lstPedidoGuardadosR1.size();
                                        }
                                    }
                                } else {
                                    Toast.makeText(DividirPedidos.this, "Cantidad no válida", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(DividirPedidos.this, "Entrada cancelada", Toast.LENGTH_SHORT).show();
                            }
                        });

                        builder.show();
                    }
                });
            }
        }
    }
}
