package com.example.apprestaurante;

import static com.example.apprestaurante.ComandaGestion.lstPedidos;
import static com.example.apprestaurante.ComandaGestion.Permiso;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurante.adapters.PedidosAdapterCuenta1;
import com.example.apprestaurante.adapters.PedidosAdapterCuenta2;
import com.example.apprestaurante.clases.Pedido;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.services.PedidoDetalleService;
import com.example.apprestaurante.services.PedidoService;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class DividirPedidos extends AppCompatActivity {
    public static Boolean cargarLista = false;
    RecyclerView rcvPedidosActual, rcvPedidosSiguiente;
    private LinearLayoutManager layoutManagerActual, layoutManagerSiguiente;
    List<PedidoDetalle> lstPedidosActual;
    List<PedidoDetalle> lstPedidosSiguiente;
    List<PedidoDetalle> lstPedidoGuardadosR1;
    List<PedidoDetalle> lstInicial;
    Button btnDividir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dividir_pedidos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lstPedidoGuardadosR1 = new ArrayList<>(); // Inicializa la lista lstPedidoGuardados

        btnDividir = findViewById(R.id.btnDividir);

        lstInicial = new ArrayList<>();
        for (PedidoDetalle elemento : lstPedidos) {
            lstInicial.add((PedidoDetalle) elemento.clone()); // Asumiendo que TuTipoDeElemento implementa el método clone()
        }

        btnDividir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcesoDividirPedido();
            }
        });

        CargarPedidosDetalle1BD();
        CargarPedidosDetalle2();
    }

    private void ProcesoDividirPedido() {

        int idPedido = getIntent().getIntExtra("idPedido", 0);
        int idMesa = getIntent().getIntExtra("idMesa", 0);

        if (lstPedidosSiguiente != null)
        {
            //Programado para el pedido principal
            PedidoDetalle pedidoDetalle;
            if (lstInicial == lstPedidosActual)
            {
                //Solo actualizacion
                for (PedidoDetalle siguiente : lstPedidosSiguiente)
                {
                    for (PedidoDetalle actual : lstPedidosActual)
                    {
                        if (siguiente.getIdProducto() == actual.getIdProducto())
                        {
                            pedidoDetalle = new PedidoDetalle();
                            pedidoDetalle.setIdDetalle(actual.getIdDetalle());
                            pedidoDetalle.setIdPedido(idPedido);
                            pedidoDetalle.setIdProducto(actual.getIdProducto());
                            pedidoDetalle.setCantidad(actual.getCantidad());
                            pedidoDetalle.setSubTotal(actual.getSubTotal());
                            ActualizarCompra(pedidoDetalle);

                        }
                    }
                }

            }
            else
            {
                boolean eliminar = false;
                //Habra eliminacion y posible actualizacion
                for (PedidoDetalle siguiente : lstPedidosSiguiente)
                {
                    for (PedidoDetalle actual : lstPedidosActual)
                    {
                        if (siguiente.getIdProducto() == actual.getIdProducto())
                        {
                            pedidoDetalle = new PedidoDetalle();
                            pedidoDetalle.setIdDetalle(actual.getIdDetalle());
                            pedidoDetalle.setIdPedido(idPedido);
                            pedidoDetalle.setIdProducto(actual.getIdProducto());
                            pedidoDetalle.setCantidad(actual.getCantidad());
                            pedidoDetalle.setSubTotal(actual.getSubTotal());
                            ActualizarCompra(pedidoDetalle);
                            eliminar = false;
                            break;
                        }
                        else
                        {
                            eliminar = true;
                        }
                    }

                    if (eliminar)
                    {
                        EliminarPedidoDetalle(String.valueOf(siguiente.getIdDetalle()));
                    }
                    //Vamos a actualizar el total del pedido
                    Pedido pedido2 = new Pedido();
                    pedido2.setIdPedido(idPedido);
                    pedido2.setIdMesa(idMesa);
                    double total = CalcularTotal(lstPedidosActual);
                    pedido2.setTotal(total);
                    ActualizarTotalPedido(pedido2);
                }

            }

            // Obtiene la fecha y hora actual
            Date fechaActual = new Date();

            // Crea un objeto SimpleDateFormat con el formato deseado
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Formatea la fecha y hora actual
            String fechaFormateada = formato.format(fechaActual);

            //Creamos un nuevo pedido y despues los detalles
            Pedido pedido = new Pedido();
            pedido.setIdMesa(idMesa);
            pedido.setIdCliente(10);
            pedido.setCancelado(false);
            pedido.setFecha(fechaFormateada);
            pedido.setListo(false);
            pedido.setTotal(0);
            pedido.setDescuento(0);
            pedido.setIva(0);
            pedido.setPropina(0);
            pedido.setTotalPago(0);
            pedido.setSaldo(0);
            pedido.setnFactura("0");
            pedido.setAnular(false);
            pedido.setEfectivo(0);
            pedido.setCredito(0);
            pedido.setBtc(0);
            pedido.setExento(0.0);

            InsertarPedido(pedido, fechaFormateada);
        }
        else
        {
            Toast.makeText(this, "No hay nada que separar", Toast.LENGTH_SHORT).show();
        }
    }

    private void InsertarPedido(Pedido pedido, String fechaFormateada) {
        PedidoService pedidoService = new PedidoService();
        pedidoService.InsertarPedido(pedido, new CallBackApi<Integer>() {
            @Override
            public void onResponse(Integer response) {
                if(response.intValue() > 0){
                    int idPedido = response.intValue();
                    // El pedido se insertó con éxito
                    //Insertamos en la base de datos el pedido
                    for (PedidoDetalle siguiente : lstPedidosSiguiente)
                    {
                        PedidoDetalle pedidoDetalle2;
                        pedidoDetalle2 = new PedidoDetalle();
                        pedidoDetalle2.setIdDetalle(0);
                        pedidoDetalle2.setCocinando(true);
                        pedidoDetalle2.setExtras("");
                        pedidoDetalle2.setHoraEntregado(fechaFormateada);
                        pedidoDetalle2.setHoraPedido(fechaFormateada);
                        //pedidoDetalle2.IdCocinero = null;
                        pedidoDetalle2.setIdProducto(siguiente.getIdProducto());
                        pedidoDetalle2.setIdPedido(idPedido);
                        pedidoDetalle2.setCantidad(siguiente.getCantidad());
                        pedidoDetalle2.setPrecio(siguiente.getPrecio());
                        pedidoDetalle2.setSubTotal(siguiente.getSubTotal());
                        pedidoDetalle2.setGrupo("0");
                        pedidoDetalle2.setUsuario("1");
                        //pedidoDetalle2.Fecha = null;
                        InsertarPedidoDetalle(pedidoDetalle2);
                    }
                    double total = CalcularTotal(lstPedidosSiguiente);
                    pedido.setIdPedido(idPedido);
                    pedido.setTotal(total);
                    ActualizarTotalPedido(pedido);
                    cargarLista = Permiso;
                    finish();
                }else {
                    // Hubo un error en la inserción del pedido
                    Toast.makeText(DividirPedidos.this, "Hubo un error al insertar pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseBool(Response<Boolean> response) {

            }

            @Override
            public void onResponseList(List<Integer> response) {

            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(DividirPedidos.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InsertarPedidoDetalle(PedidoDetalle pedidoDetalle) {
        PedidoDetalleService pedidoDetalleService = new PedidoDetalleService();
        pedidoDetalleService.InsertarPedidoDetalle(pedidoDetalle, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean insertado = response.body();
                    if (insertado != null && insertado) {
                        // El pedido se insertó con éxito

                    } else {
                        // Hubo un error en la inserción del pedido
                        Toast.makeText(DividirPedidos.this, "Hubo un error al insertar pedido detalle", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(DividirPedidos.this, "Error en la respuesta al insertar detallepedido: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(DividirPedidos.this, "Error al insertar detalle pedido: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ActualizarTotalPedido(Pedido nuevoPedido) {
        PedidoService pedidoService = new PedidoService();
        pedidoService.ActualizarTotal(nuevoPedido, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(ComandaGestion.this, "Estado mesa actualizado", Toast.LENGTH_SHORT).show();

                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(DividirPedidos.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void EliminarPedidoDetalle(String id) {
        PedidoDetalleService pedidoDetalleService = new PedidoDetalleService();
        pedidoDetalleService.EliminarPedidoDetalle(id, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean eliminado = response.body();
                    if (eliminado != null && eliminado) {
                        // El pedido se insertó con éxito
                    } else {
                        // Hubo un error en la inserción del pedido
                        //Toast.makeText(DividirPedidos.this, "Hubo un error al eliminar pedido detalle", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(DividirPedidos.this, "Error en la respuesta al eliminar detallepedido: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void CargarPedidosDetalle1BD(){
        if(lstPedidos.size() > 0){
            // Configurando adaptador
            lstPedidosActual = new ArrayList<>(lstPedidos);
            PedidosAdapterCuenta1 pedidosAdapter = new PedidosAdapterCuenta1(this, lstPedidosActual);
            layoutManagerActual = new LinearLayoutManager(this);
            rcvPedidosActual = findViewById(R.id.rcvPedidos1);
            rcvPedidosActual.setAdapter(pedidosAdapter);
            rcvPedidosActual.setLayoutManager(layoutManagerActual);
            rcvPedidosActual.setHasFixedSize(true);

            if (rcvPedidosActual != null){
                PedidosAdapterCuenta1 pedidosAdapterC1 = (PedidosAdapterCuenta1) rcvPedidosActual.getAdapter();
                pedidosAdapterC1.setOnItemLongClickListener(new PedidosAdapterCuenta1.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(PedidoDetalle pedidoDetalle) {
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
                                            copiaPedidoDetalle.setFoto(pedidoDetalle.getFoto());

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
            PedidosAdapterCuenta2 pedidosAdapter = new PedidosAdapterCuenta2(this, lstPedidosSiguiente);
            layoutManagerSiguiente = new LinearLayoutManager(this);
            rcvPedidosSiguiente = findViewById(R.id.rcvPedidos2);
            rcvPedidosSiguiente.setAdapter(pedidosAdapter);
            rcvPedidosSiguiente.setLayoutManager(layoutManagerSiguiente);
            rcvPedidosSiguiente.setHasFixedSize(true);

            if (rcvPedidosSiguiente != null) {
                PedidosAdapterCuenta2 pedidosAdapterC2 = (PedidosAdapterCuenta2) rcvPedidosSiguiente.getAdapter();
                pedidosAdapterC2.setOnItemLongClickListener(new PedidosAdapterCuenta2.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(PedidoDetalle pedidoDetalle) {
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

    private void ActualizarCompra(PedidoDetalle pedidoDetalle) {
        PedidoDetalleService pedidoDetalleService = new PedidoDetalleService();
        pedidoDetalleService.ActualizarCompra(pedidoDetalle, new CallBackApi<Boolean>() {


            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(ComandaGestion.this, "Estado mesa actualizado", Toast.LENGTH_SHORT).show();
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(DividirPedidos.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

    }

    private double CalcularTotal(List<PedidoDetalle> lst) {
        double total = 0;
        DecimalFormat df = new DecimalFormat("#.00");
        if (lst != null)
        {
            for (PedidoDetalle pDetalle: lst) {
                total += pDetalle.getSubTotal();
            }
        }
        return Double.parseDouble(df.format(total));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
