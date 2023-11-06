package com.example.apprestaurante;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static com.example.apprestaurante.MesasSalones.cambiarMesa;
import static com.example.apprestaurante.MesasSalones.idMesaAnterior;
import static com.example.apprestaurante.MesasSalones.idPedidoCambioMesa;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComandaGestion extends AppCompatActivity implements  PedidosAdapter.OnItemClickListener{

    int idMesa, idPedido = 0;
    TextView tvTicket, textProductos, tvMesa;
    RecyclerView rcvPedidos;
    private LinearLayoutManager layoutManager;
    LinearLayout llFamilias, llProductos, llAcciones;
    List<Familia> lstFamilias;
    public static List<Integer> lstPedidosEnMesa = null;
    public static List<Producto> lstProductos;
    Producto producto;
    Pedido nuevoPedido = null;
    Button btnCuentas;
    public static List<PedidoDetalle> lstPedidos = new ArrayList<>();
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
        btnCuentas = findViewById(R.id.btnCuentas);
        btnCuentas.setVisibility(View.GONE);

        //Obteniendo idMesa
        Intent intent = getIntent();
        if (intent != null) {
            idMesa = intent.getIntExtra("idMesa", 0); // El segundo parámetro (0) es el valor predeterminado si no se encuentra "idMesa"
            tvMesa.setText("#Mesa: " + idMesa);

            View.OnClickListener accionClickListener = view -> {
                // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
                String tag = String.valueOf(view.getTag());
                ProgramarAcciones(tag);
            };

            CrearBotones(accionClickListener);
        }

        // Define un OnClickListener común para los botones de productos
        View.OnClickListener productoClickListener = view -> {
            // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
            String tag = String.valueOf(view.getTag());

            int cantidad = 1;
            BuscarProductoPorId(tag, cantidad);

        };

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

        btnCuentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Infla el diseño XML personalizado
                View customLayout = getLayoutInflater().inflate(R.layout.item_dialog, null);

                // Crea un AlertDialog con el diseño personalizado
                AlertDialog.Builder builder = new AlertDialog.Builder(ComandaGestion.this);
                builder.setView(customLayout);

                // Declara una variable para almacenar el número seleccionado
                final AlertDialog alertDialog = builder.create();

                for (final Integer integer : lstPedidosEnMesa) {
                    Button button = new Button(ComandaGestion.this);
                    button.setText(integer.toString());
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Almacena el número entero seleccionado en la variable
                            idPedido = integer;
                            ObtenerProductosEnMesa(String.valueOf(idMesa), String.valueOf(idPedido));
                            tvTicket.setText("#Ticket: " + idPedido);
                            // Cierra el AlertDialog
                            alertDialog.dismiss();
                        }
                    });
                    ((LinearLayout) customLayout).addView(button);
                }

                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.show();
            }
        });

    }

    private void CargarPedidosEnMesa() {
        PedidoService pedidoService = new PedidoService();
        pedidoService.obtenerPedidosEnMesa(String.valueOf(idMesa), new CallBackApi<Integer>() {
            @Override
            public void onResponse(Integer response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {

            }

            @Override
            public void onResponseList(List<Integer> response) {
                lstPedidosEnMesa = response;
                if(lstPedidosEnMesa.size() > 1){
                    btnCuentas.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void ProgramarAcciones(String tag) {
        if(tag.equals("1")){
            //Comanda
            Toast.makeText(this, "Click en comandas", Toast.LENGTH_SHORT).show();
        }else if(tag.equals("2")){
            //Extra
            Intent intent = new Intent(ComandaGestion.this, DividirPedidos.class);
            intent.putExtra("idPedido", idPedido);
            intent.putExtra("idMesa", idMesa);
            startActivity(intent);
        }else if(tag.equals("3")){
            //Cambiar mesa
            cambiarMesa = true;
            idMesaAnterior = idMesa;
            idPedidoCambioMesa = idPedido;
            finish();
        }else if(tag.equals("4")){
            //Mesero
            Toast.makeText(this, "Click en mesero", Toast.LENGTH_SHORT).show();
        }else if(tag.equals("5")){
            //Cliente
            Toast.makeText(this, "Click en cliente", Toast.LENGTH_SHORT).show();
        }
    }

    private void CrearBotones(View.OnClickListener accionClickListener) {
        llAcciones.removeAllViews();
        String[] valores = {"comanda", "Extras", "Cambiar mesa", "Mesero", "Cliente"};
        int[] imagen = {R.drawable.comanda, R.drawable.extras, R.drawable.cambio_mesa, R.drawable.mesero, R.drawable.cliente};
        int tamano = lstPedidos.size();
        for (int i = 0; i < valores.length; i++) {
            Button btnAccion = new Button(ComandaGestion.this);
            btnAccion.setText(valores[i]);
            btnAccion.setTag(i+1);
            if(tamano > 0){
                btnAccion.setEnabled(true);
            } else{
                btnAccion.setEnabled(false);
            }
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

    private void ObtenerProductosEnMesa(String id, String idPed){
        Call<List<PedidoDetalle>> call = ApiClient.getClient().create(PedidoDetalleApi.class).productosEnMesa(id, idPed);
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
        View.OnClickListener accionClickListener = view -> {
            // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
            String tag = String.valueOf(view.getTag());
            ProgramarAcciones(tag);
        };

        CrearBotones(accionClickListener);
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
            //Saber si ya existe algun producto igual en los detalles
            for (PedidoDetalle pDetalle: lstPedidos) {
                if (pDetalle.getIdProducto() == Integer.parseInt(idProducto))
                {
                    cantidad = cantidad + pDetalle.getCantidad();
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
                pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, producto.getPrecio()));
                ActualizarCompra(pedidoDetalle);
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
                pedidoDetalle.setUsuario("1");
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
                    ObtenerProductosEnMesa(String.valueOf(idMesa), String.valueOf(idPedido));
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
                        ObtenerProductosEnMesa(String.valueOf(idMesa), String.valueOf(idPedido));

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
        pedidoService.InsertarPedido(pedido, new CallBackApi<Integer>() {
            @Override
            public void onResponse(Integer response) {
                if(response.intValue() > 0){
                    int idPedido = response.intValue();
                        // El pedido se insertó con éxito
                        //Agregamos detalles al pedido
                        PedidoDetalle pedidoDetalle = new PedidoDetalle();
                        pedidoDetalle.setCocinando(true);
                        pedidoDetalle.setExtras("");
                        pedidoDetalle.setHoraEntregado(fecha);
                        pedidoDetalle.setHoraPedido(fecha);
                        pedidoDetalle.setPrecio(producto.getPrecio());
                        //pedidoDetalle.setIdCocinero(null);
                        pedidoDetalle.setIdProducto(Integer.parseInt(idProducto));
                        //Obtenemos el ultimo pedido y asignamos idpedio a pedidodetalle
                        pedidoDetalle.setIdPedido(idPedido);
                        pedidoDetalle.setCantidad(cantidad);
                        pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, producto.getPrecio()));
                        pedidoDetalle.setGrupo("0");
                        pedidoDetalle.setUsuario(null);
                        //pedidoDetalle.Fecha = null;

                        InsertarPedidoDetalle(pedidoDetalle);

                }else {
                    // Hubo un error en la inserción del pedido
                    Toast.makeText(ComandaGestion.this, "Hubo un error al insertar pedido", Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onResume() {
        super.onResume();
        CargarPedidosEnMesa();
        ObtenerProductosEnMesa(String.valueOf(idMesa), String.valueOf(idPedido));
        cambiarMesa = false;
    }
}