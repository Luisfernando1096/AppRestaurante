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
import com.example.apprestaurante.clases.Pedido;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.PedidoApi;
import com.example.apprestaurante.interfaces.PedidoDetalleApi;
import com.example.apprestaurante.interfaces.ProductoApi;
import com.example.apprestaurante.network.ApiClient;
import com.example.apprestaurante.services.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComandaGestion extends AppCompatActivity implements  PedidosAdapter.OnItemClickListener{

    int idMesa, idPedido;
    TextView tvTicket, textProductos;
    RecyclerView rcvPedidos;
    private LinearLayoutManager layoutManager;
    LinearLayout llFamilias, llProductos;

    List<Familia> lstFamilias;
    List<Producto> lstProductos;
    Producto producto;
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

        //Obteniendo idMesa
        Intent intent = getIntent();
        if (intent != null) {
            idMesa = intent.getIntExtra("idMesa", 0); // El segundo parámetro (0) es el valor predeterminado si no se encuentra "idMesa"
            ObtenerProductosEnMesa(String.valueOf(idMesa));
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
                            Toast.makeText(ComandaGestion.this, "Ingreso perfectamente el idPedido", Toast.LENGTH_SHORT).show();
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

    private void InsertarPedido(Pedido pedido) {

        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        PedidoApi pedidoApi = ApiClient.getClient().create(PedidoApi.class);

        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = pedidoApi.insertarPedido(pedido);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(ComandaGestion.this, "Producto insertado con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    // Ocurrió un error en la inserción
                    Toast.makeText(ComandaGestion.this, "Error al insertar el producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // Si hay un error en la conexión de red
                Toast.makeText(ComandaGestion.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            //Ya existe un pedido
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
                //pedidoDetalle.ActualizarCompra()
                Toast.makeText(this, "Se aumentara el producto: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
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
                pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, 0));
                pedidoDetalle.setGrupo("0");
                pedidoDetalle.setUsuario(null);
                //pedidoDetalle.Insertar();
                Toast.makeText(this, "Se creara el producto: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            //Creamos un nuevo pedido

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

            //pedido.Insertar()
            InsertarPedido(pedido);


            //Agregamos detalles al pedido
            PedidoDetalle pedidoDetalle = new PedidoDetalle();
            pedidoDetalle.setIdDetalle(0);
            pedidoDetalle.setCocinando(true);
            pedidoDetalle.setExtras("");
            pedidoDetalle.setHoraEntregado(fecha);
            pedidoDetalle.setHoraPedido(fecha);
            //pedidoDetalle.setIdCocinero(null);
            pedidoDetalle.setIdProducto(Integer.parseInt(idProducto));
            //DataTable up = DataManager.DBConsultas.UltimoPedido();
            //pedidoDetalle.setIdPedido(idPedido);
            pedidoDetalle.setCantidad(cantidad);
            pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, producto.getPrecio()));
            pedidoDetalle.setGrupo("0");
            pedidoDetalle.setUsuario(null);
            //pedidoDetalle.Fecha = null;

            /*if (pedidoDetalle.Insertar())
            {
                Mesa mesa = new Mesa();
                mesa.setIdMesa(idMesa);
                mesa.setDisponible(false);
                if (mesa.ActualizarEstado())
                {
                    //MessageBox.Show("SE ACTUALIZO CON EXITO");
                    btnComanda.Enabled = true;
                    btnDisminuir.Enabled = true;
                    btnExtras.Enabled = true;
                }
                else
                {
                    //MessageBox.Show("ERROR AL ACTUALIZAR");
                }
                //MessageBox.Show("SE INSERTO CON EXITO");
            }
            else
            {
                //MessageBox.Show("ERROR AL INSERTAR");
            }*/
            Toast.makeText(this, "Se creo el producto: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
        }
        //Vamos a actualizar el total del pedido
        Pedido pedido2 = new Pedido();
        pedido2.setIdMesa(idMesa);
        double total = CalcularTotal();
        //pedido2.ActualizarTotal(total);

        //Actualizar al final el datagrid
        //CargarProductosPorMesa(lblMesa.Tag.ToString());

    }

    private double CalcularTotal() {
        double total = 0;
        if (lstPedidos != null)
        {
            for (PedidoDetalle pDetalle: lstPedidos) {
                total += pDetalle.getSubTotal();
            }
        }
        return total;
    }

    private double CalcularSubTotal(int cantidad, double precio)
    {
        double subTotal;
        subTotal = precio*cantidad;
        return subTotal;
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

                                // Obtén la imagen que deseas usar desde los recursos drawable
                                @SuppressLint("UseCompatLoadingForDrawables") Drawable originalDrawable = getResources().getDrawable(R.drawable.productos); // Reemplaza "tu_imagen" con el nombre de tu imagen en res/drawable

                                // Redimensiona la imagen al tamaño deseado
                                int width = 100; // Ancho en píxeles
                                int height = 100; // Alto en píxeles
                                Bitmap bitmap = ((BitmapDrawable) originalDrawable).getBitmap();
                                Drawable icono = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, width, height, true));

                                // Establece la posición del icono en el botón (izquierda, arriba, derecha, abajo)
                                btnProducto.setCompoundDrawablesWithIntrinsicBounds(null, icono, null, null);

                                // Agrega el botón al GridLayout
                                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                                params.setMargins(5, 5, 5, 5); // Espacio entre los botones
                                btnProducto.setLayoutParams(params);

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