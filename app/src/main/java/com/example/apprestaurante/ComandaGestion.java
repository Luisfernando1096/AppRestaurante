package com.example.apprestaurante;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apprestaurante.adapters.PedidosAdapter;
import com.example.apprestaurante.clases.Familia;
import com.example.apprestaurante.clases.Mesa;
import com.example.apprestaurante.clases.Pedido;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.PedidoDetalleApi;
import com.example.apprestaurante.interfaces.ProductoApi;
import com.example.apprestaurante.network.ApiClient;
import com.example.apprestaurante.services.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComandaGestion extends AppCompatActivity implements  PedidosAdapter.OnItemClickListener{

    Button btnMesero;
    int idMesa, idPedido;
    TextView tvTicket, textProductos, tvMesa;
    RecyclerView rcvPedidos;
    private LinearLayoutManager layoutManager;
    LinearLayout llFamilias, llProductos, llAcciones;
    List<Familia> lstFamilias;
    List<Producto> lstProductos;
    Producto producto;
    Pedido nuevoPedido = null;
    public static List<PedidoDetalle> lstPedidos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comanda_gestion);
        llFamilias = findViewById(R.id.llFamilias);
        llProductos = findViewById(R.id.llProductos);
        textProductos = findViewById(R.id.textProductos);
        rcvPedidos = findViewById(R.id.rcvPedidos);
        tvTicket = findViewById(R.id.tvTicket);
        tvMesa = findViewById(R.id.tvMesa);
        llAcciones = findViewById(R.id.llAcciones);


        //Obteniendo idMesa
        Intent intent = getIntent();
        if (intent != null) {
            idMesa = intent.getIntExtra("idMesa", 0); // El segundo parámetro (0) es el valor predeterminado si no se encuentra "idMesa"
            tvMesa.setText("#Mesa: " + idMesa);
            ObtenerProductosEnMesa(String.valueOf(idMesa));
        }

        // Define un OnClickListener común para los botones de productos
        View.OnClickListener productoClickListener = view -> {
            // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
            String tag = String.valueOf(view.getTag());

            int cantidad = 1;
            BuscarProductoPorId(tag, cantidad);

        };

        View.OnClickListener accionClickListener = view -> {
            // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
            String tag = String.valueOf(view.getTag());
            ProgramarAcciones(tag);
        };

        CrearBotones(accionClickListener);

        View.OnLongClickListener productoLongClickListener = view -> {
            // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
            String tag = String.valueOf(view.getTag());
            CrearAlertaDialogo(tag, view);
            return true;
        };


        // Define un OnClickListener común para los botones de Familias
        View.OnClickListener familiaClickListener = view -> {
            // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
            BuscarProductoPorFamilia(productoClickListener, view.getTag().toString(), productoLongClickListener);
            textProductos.setText("Productos : " + ((Button) view).getText().toString());
        };

        BuscarFamilias(familiaClickListener);

    }

    private void ProgramarAcciones(String tag) {
        if(tag.equals("1")){
            //Comanda
            Toast.makeText(this, "Click en comanda", Toast.LENGTH_SHORT).show();
        }else if(tag.equals("2")){
            //Extra
            Toast.makeText(this, "Click en extra", Toast.LENGTH_SHORT).show();
        }else if(tag.equals("3")){
            //Cambiar mesa
            Toast.makeText(this, "Click en cambio mesa", Toast.LENGTH_SHORT).show();
        }else if(tag.equals("4")){
            //Mesero
            Toast.makeText(this, "Click en mesero", Toast.LENGTH_SHORT).show();
        }else if(tag.equals("5")){
            //Cliente
            Toast.makeText(this, "Click en cliente", Toast.LENGTH_SHORT).show();
        }
    }

    private void CrearBotones(View.OnClickListener accionClickListener) {
        String[] valores = {"comanda", "Extras", "Cambiar mesa", "Mesero", "Cliente"};
        int[] imagen = {R.drawable.comanda, R.drawable.extras, R.drawable.cambio_mesa, R.drawable.mesero, R.drawable.cliente};
        for (int i = 0; i < valores.length; i++) {
            Button btnAccion = new Button(ComandaGestion.this);
            btnAccion.setText(valores[i]);
            btnAccion.setTag(i+1);
            btnAccion.setOnClickListener(accionClickListener);

            btnAccion.setTextSize(8);

            DarEstiloBoton(btnAccion, imagen[i], 50, 50);

            llAcciones.addView(btnAccion);
        }
    }

    private void DarEstiloBoton(Button btn, int imagen, int ancho, int alto) {
        // Obtén la imagen que deseas usar desde los recursos drawable
        @SuppressLint("UseCompatLoadingForDrawables") Drawable originalDrawable = getResources().getDrawable(imagen); // Reemplaza "tu_imagen" con el nombre de tu imagen en res/drawable

        // Redimensiona la imagen al tamaño deseado
        int width = ancho; // Ancho en píxeles
        int height = alto; // Alto en píxeles
        Bitmap bitmap = ((BitmapDrawable) originalDrawable).getBitmap();
        Drawable icono = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, width, height, true));

        // Establece la posición del icono en el botón (izquierda, arriba, derecha, abajo)
        btn.setCompoundDrawablesWithIntrinsicBounds(null, icono, null, null);

        // Agrega el botón al GridLayout
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(5, 5, 5, 5); // Espacio entre los botones
        btn.setLayoutParams(params);
    }

    private void CrearAlertaDialogo(String tag, View view){
        // Crear el AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setTitle("¿Cuantos productos desea agregar?");

        // Crear un EditText en el AlertDialog
        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setPositiveButton("Aceptar", (dialog, which) -> {
            try {
                int number = Integer.parseInt(input.getText().toString());
                // Hacer algo con el número ingresado
                // Puedes usar 'number' aquí como lo necesites

                BuscarProductoPorId(tag, number);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "No ingreso ningun valor o valor no valido.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void BuscarFamilias(View.OnClickListener familiaClickListener) {

        FamiliaService familiaService = new FamiliaService();
        familiaService.BuscarFamilias(new CallBackApi<Familia>() {
            @Override
            public void onResponse(Familia response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {

            }

            @Override
            public void onResponseList(List<Familia> response) {
                lstFamilias = response;
                for (Familia familia : lstFamilias) {
                    Button btnFamilia = new Button(ComandaGestion.this);
                    btnFamilia.setTag(familia.getIdFamilia());
                    btnFamilia.setText(familia.getNombre().toString());
                    btnFamilia.setOnClickListener(familiaClickListener);
                    llFamilias.addView(btnFamilia);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ComandaGestion.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ObtenerProductosEnMesa(String id){
        Call<List<PedidoDetalle>> call = ApiClient.getClient().create(PedidoDetalleApi.class).productosEnMesa(id);
        call.enqueue(new Callback<List<PedidoDetalle>>() {
            @Override
            public void onResponse(Call<List<PedidoDetalle>> call, Response<List<PedidoDetalle>> response) {
                // Si hay respuesta
                try {

                    if (response.isSuccessful()) {
                        lstPedidos = response.body();
                        if(lstPedidos.size()>0){
                            PedidoDetalle pedido = lstPedidos.get(0);
                            idPedido = pedido.getIdPedido();
                            tvTicket.setText("#Ticket: " + idPedido);
                            if(nuevoPedido != null){
                                nuevoPedido.setIdPedido(idPedido);
                                //Vamos a actualizar el total del pedido cuando es nuevo
                                nuevoPedido.setIdMesa(idMesa);
                                double total = CalcularTotal();
                                nuevoPedido.setTotal(total);
                                Toast.makeText(ComandaGestion.this, "Esto tiene total: " + total, Toast.LENGTH_SHORT).show();
                                ActualizarTotalPedido();

                            }
                            CargarPedidos();
                        }
                    }else{
                        System.out.println("Fallo el isSuccessful");
                    }

                } catch (Exception e) {
                    Toast.makeText(ComandaGestion.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PedidoDetalle>> call, Throwable t) {
                // Si hay un error
                Toast.makeText(ComandaGestion.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexión de red: " + t.getMessage());
            }
        });
    }

    private void CargarPedidos(){
        if(lstPedidos.size() > 0){
            // Configurando adaptador
            PedidosAdapter pedidosAdapter = new PedidosAdapter(lstPedidos, ComandaGestion.this);
            layoutManager = new LinearLayoutManager(this);
            rcvPedidos = findViewById(R.id.rcvPedidos);
            rcvPedidos.setAdapter(pedidosAdapter);
            rcvPedidos.setLayoutManager(layoutManager);
            rcvPedidos.setHasFixedSize(true);
        }

    }

    @Override
    public void onItemClick(PedidoDetalle pedidoDetalle) {
        //Programar aqui cuando se quiere disminuir un pedido
        Toast.makeText(this, "Esta es un prueba: " + pedidoDetalle.getNombre(), Toast.LENGTH_SHORT).show();
    }

    private void BuscarProductoPorId(String id, int cantidad) {
        ProductoService productoService = new ProductoService();
        productoService.buscarProductoPorId(id, cantidad, new CallBackApi<Producto>() {
            @Override
            public void onResponse(Producto response) {
                // Procesa la respuesta del producto aquí
                producto = response;
                ProcesarComanda(cantidad, id);
            }

            @Override
            public void onResponseBool(Response<Boolean> response) {

            }

            @Override
            public void onResponseList(List<Producto> response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ComandaGestion.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ProcesarComanda(int cantidad, String idProducto){

        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (lstPedidos.size() > 0)
        {
            nuevoPedido = new Pedido();
            boolean aumentarUnProducto = false;
            double precio = 0;
            //Saber si ya existe algun producto igual en los detalles
            for (PedidoDetalle pDetalle: lstPedidos) {
                if (pDetalle.getIdProducto() == Integer.parseInt(idProducto))
                {
                    cantidad = cantidad + pDetalle.getCantidad();
                    precio = pDetalle.getPrecio();
                    aumentarUnProducto = true;
                }
            }

            PedidoDetalle pedidoDetalle = new PedidoDetalle();
            if (aumentarUnProducto)
            {
                //Ya existe un producto igual en el datgrid, hay que aumentar
                pedidoDetalle.setIdPedido(idPedido);
                pedidoDetalle.setIdProducto(Integer.parseInt(idProducto));
                pedidoDetalle.setCantidad(cantidad);
                pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, precio));
                ActualizarCompra(pedidoDetalle);
                Toast.makeText(this, "Se aumentara el producto: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "idPedido: " + idPedido + "Cantidad: " + cantidad + "SubTotal: " + CalcularSubTotal(cantidad, precio), Toast.LENGTH_LONG).show();
            }
            else
            {
                //No existe un producto igual en el datgrid, hay que crearlo
                pedidoDetalle.setCocinando(true);
                pedidoDetalle.setExtras("");
                pedidoDetalle.setFecha(fecha);
                pedidoDetalle.setHoraPedido(fecha);
                //pedidoDetalle.IdCocinero = null;
                pedidoDetalle.setIdProducto(Integer.parseInt(idProducto));
                pedidoDetalle.setIdPedido(idPedido);
                pedidoDetalle.setCantidad(cantidad);
                pedidoDetalle.setPrecio(producto.getPrecio());
                pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, producto.getPrecio()));
                pedidoDetalle.setGrupo("0");
                pedidoDetalle.setUsuario(null);
                InsertarPedidoDetalle(pedidoDetalle);

            }

        }
        else
        {
            //Creamos un nuevo pedido
            nuevoPedido = new Pedido();
            //No hay productos, se inicia el pedido
            //Creamos el pedido
            Pedido pedido = new Pedido();
            pedido.setIdMesa(idMesa);
            pedido.setIdCuenta(1);
            pedido.setCancelado(false);
            pedido.setFecha(fecha);
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

            //Actualizar estado de la mesa
            Mesa mesa = new Mesa();
            mesa.setIdMesa(idMesa);
            mesa.setDisponible(false);
            ActualizarEstadoMesa(mesa);

            //Creamos el pedido
            InsertarPedido(pedido, fecha, cantidad, idProducto);

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
                    ObtenerProductosEnMesa(String.valueOf(idMesa));
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
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

    private void ActualizarTotalPedido() {
        PedidoService pedidoService = new PedidoService();
        pedidoService.ActualizarTotal(nuevoPedido, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {
                    nuevoPedido = null;
                    //Toast.makeText(ComandaGestion.this, "Estado mesa actualizado", Toast.LENGTH_SHORT).show();

                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
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

    private void ActualizarEstadoMesa(Mesa mesa) {
        MesaService mesaService = new MesaService();
        mesaService.ActualizarEstadoMesa(mesa, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {

                    //Toast.makeText(ComandaGestion.this, "Estado mesa actualizado", Toast.LENGTH_SHORT).show();

                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ComandaGestion.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ObtenerUltimoPedido(PedidoDetalle pedidoDetalle, int cantidad) {
        PedidoService pedidoService = new PedidoService();
        pedidoService.obtenerUltimoPedido(new CallBackApi<Pedido>() {
            @Override
            public void onResponse(Pedido response) {
                // Procesa la respuesta del producto aquí
                pedidoDetalle.setIdPedido(response.getIdPedido());
                pedidoDetalle.setCantidad(cantidad);
                pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, producto.getPrecio()));
                pedidoDetalle.setGrupo("0");
                pedidoDetalle.setUsuario(null);
                //pedidoDetalle.Fecha = null;

               InsertarPedidoDetalle(pedidoDetalle);
            }
            @Override
            public void onResponseBool(Response<Boolean> response) {

            }
            @Override
            public void onResponseList(List<Pedido> response) {

            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ComandaGestion.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ComandaGestion.this, "Insertado con éxito pedidodetalle", Toast.LENGTH_SHORT).show();
                        ObtenerProductosEnMesa(String.valueOf(idMesa));

                    } else {
                        // Hubo un error en la inserción del pedido
                        Toast.makeText(ComandaGestion.this, "Hubo un error al insertar pedido detalle", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta al insertar detallepedido: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ComandaGestion.this, "Error al insertar detalle pedido: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InsertarPedido(Pedido pedido, String fecha, int cantidad, String idProducto) {
        PedidoService pedidoService = new PedidoService();
        pedidoService.InsertarPedido(pedido, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean insertado = response.body();
                    if (insertado != null && insertado) {
                        // El pedido se insertó con éxito
                        //Agregamos detalles al pedido
                        PedidoDetalle pedidoDetalle = new PedidoDetalle();
                        pedidoDetalle.setCocinando(true);
                        pedidoDetalle.setExtras("");
                        pedidoDetalle.setHoraEntregado(fecha);
                        pedidoDetalle.setHoraPedido(fecha);
                        //pedidoDetalle.setIdCocinero(null);
                        pedidoDetalle.setIdProducto(Integer.parseInt(idProducto));
                        //Obtenemos el ultimo pedido y asignamos idpedio a pedidodetalle
                        ObtenerUltimoPedido(pedidoDetalle, cantidad);

                    } else {
                        // Hubo un error en la inserción del pedido
                        Toast.makeText(ComandaGestion.this, "Hubo un error al insertar pedido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ComandaGestion.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double CalcularTotal() {
        double total = 0;
        DecimalFormat df = new DecimalFormat("#.00");
        if (lstPedidos != null)
        {
            for (PedidoDetalle pDetalle: lstPedidos) {
                total += pDetalle.getSubTotal();
            }
        }
        return Double.parseDouble(df.format(total));
    }

    public double CalcularSubTotal(int cantidad, double precio)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        double subTotal;
        subTotal = precio*cantidad;
        return Double.parseDouble(df.format(subTotal));
    }

    private void BuscarProductoPorFamilia(View.OnClickListener productoClickListener, String idFamilia, View.OnLongClickListener productoLongClickListener) {

        Call<List<Producto>> call = ApiClient.getClient().create(ProductoApi.class).productoPorFamilia(idFamilia);
        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                // Si hay respuesta
                try {

                    if (response.isSuccessful()) {
                        llProductos.removeAllViews();
                        lstProductos = response.body();
                        if (lstProductos != null) {

                            for (Producto producto : lstProductos) {
                                Button btnProducto = new Button(ComandaGestion.this);
                                btnProducto.setTag(producto.getIdProducto());
                                btnProducto.setText(producto.getNombre());
                                btnProducto.setOnClickListener(productoClickListener);
                                btnProducto.setOnLongClickListener(productoLongClickListener);

                                btnProducto.setTextSize(8);

                                DarEstiloBoton(btnProducto, R.drawable.productos, 100, 100);

                                llProductos.addView(btnProducto);

                            }
                        }
                    }else{
                        System.out.println("Fallo el isSuccessful");
                    }

                } catch (Exception e) {
                    Toast.makeText(ComandaGestion.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                // Si hay un error
                Toast.makeText(ComandaGestion.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexión de red: " + t.getMessage());
            }
        });

    }

}