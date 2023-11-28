package com.example.apprestaurante;

import static com.example.apprestaurante.MesasSalones.cambiarMesa;
import static com.example.apprestaurante.MesasSalones.enviarListaTask;
import static com.example.apprestaurante.MesasSalones.idMesaAnterior;
import static com.example.apprestaurante.MesasSalones.idPedidoCambioMesa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurante.adapters.PedidosAdapter;
import com.example.apprestaurante.clases.Cliente;
import com.example.apprestaurante.clases.Empleado;
import com.example.apprestaurante.clases.Familia;
import com.example.apprestaurante.clases.Ingrediente;
import com.example.apprestaurante.clases.Mesa;
import com.example.apprestaurante.clases.Pedido;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.PedidoDetalleApi;
import com.example.apprestaurante.interfaces.ProductoApi;
import com.example.apprestaurante.network.ApiClient;
import com.example.apprestaurante.services.ClienteService;
import com.example.apprestaurante.services.EmpleadoService;
import com.example.apprestaurante.services.FamiliaService;
import com.example.apprestaurante.services.IngredienteService;
import com.example.apprestaurante.services.MesaService;
import com.example.apprestaurante.services.PedidoDetalleService;
import com.example.apprestaurante.services.PedidoService;
import com.example.apprestaurante.services.ProductoService;
import com.example.apprestaurante.utils.EnviarListaTask;
import com.example.apprestaurante.utils.Load;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComandaGestion extends AppCompatActivity implements  PedidosAdapter.OnItemClickListener {

    Load progressDialog;
    int idMesa, idPedido = 0;
    public static String salon;
    String cliente, mesero, mesa;
    TextView tvTicket, textProductos, tvMesa, tvCliente, tvMesero;
    RecyclerView rcvPedidos;
    private LinearLayoutManager layoutManager;
    LinearLayout llFamilias, llProductos, llAcciones;
    List<Familia> lstFamilias;
    public static List<Integer> lstPedidosEnMesa = null;
    public static List<Cliente> lstClientes = null;
    public static List<Empleado> lstEmpleados = null;

    public static List<Producto> lstProductos;
    Producto producto;
    Pedido nuevoPedido = null;
    Button btnCuentas;
    PedidoDetalle pDetalle;
    public static List<PedidoDetalle> lstPedidos = new ArrayList<>();
    List<PedidoDetalle> lstPD = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new Load(this, "Cargando...");

        setContentView(R.layout.activity_comanda_gestion);
        llFamilias = findViewById(R.id.llFamilias);
        llProductos = findViewById(R.id.llProductos);
        textProductos = findViewById(R.id.textProductos);
        rcvPedidos = findViewById(R.id.rcvPedidos);
        tvTicket = findViewById(R.id.tvTicket);
        tvMesa = findViewById(R.id.tvMesa);
        tvCliente = findViewById(R.id.tvCliente);
        tvMesero = findViewById(R.id.tvMesero);
        llAcciones = findViewById(R.id.llAcciones);
        btnCuentas = findViewById(R.id.btnCuentas);
        btnCuentas.setVisibility(View.GONE);

        //Obteniendo idMesa
        Intent intent = getIntent();
        if (intent != null) {
            idMesa = intent.getIntExtra("idMesa", 0); // El segundo parámetro (0) es el valor predeterminado si no se encuentra "idMesa"

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

                LinearLayout linearLayoutScroll = customLayout.findViewById(R.id.llClientesScroll);

                // Obtén la referencia al ScrollView y LinearLayout dentro del AlertDialog
                Button btnAgregar = customLayout.findViewById(R.id.btnAgregar);
                btnAgregar.setVisibility(View.GONE);

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
                    linearLayoutScroll.addView(button);
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
        progressDialog.show();
        PedidoService pedidoService = new PedidoService();
        pedidoService.obtenerPedidosEnMesa(String.valueOf(idMesa), new CallBackApi<Integer>() {
            @Override
            public void onResponseList(List<Integer> response) {
                lstPedidosEnMesa = response;
                if (lstPedidosEnMesa.size() > 1) {
                    btnCuentas.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void CargarDialogoMeseros(){
        // Infla el diseño XML personalizado
        View customLayout = getLayoutInflater().inflate(R.layout.item_dialog, null);
        // Crea un AlertDialog con el diseño personalizado
        AlertDialog.Builder builder = new AlertDialog.Builder(ComandaGestion.this);
        builder.setView(customLayout);
        // Declara una variable para almacenar el número seleccionado
        final AlertDialog alertDialog = builder.create();

        // Obtén la referencia al ScrollView y LinearLayout dentro del AlertDialog
        Button btnAgregar = customLayout.findViewById(R.id.btnAgregar);
        btnAgregar.setVisibility(View.GONE);
        LinearLayout linearLayoutScroll = customLayout.findViewById(R.id.llClientesScroll);

        for (final Empleado empl : lstEmpleados) {
            Button button = new Button(ComandaGestion.this);
            button.setText( empl.getNombres());
            button.setTag(empl.getIdEmpleado());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Almacena el número entero seleccionado en la variable
                    String tag = String.valueOf(view.getTag());
                    mesero = empl.getNombres();
                    //Establecer nombre en textview
                    tvMesero.setText("Mesero: " + mesero);
                    //Crear el objeto para actualizar
                    Pedido pedEmpleado = new Pedido();
                    //Guardar en la base de datos ActualizarMesero
                    if (pedEmpleado != null) {
                        pedEmpleado.setIdPedido(idPedido);
                        pedEmpleado.setIdMesa(idMesa);
                        pedEmpleado.setIdMesero(Integer.parseInt(tag));
                        ActualizarMesero(pedEmpleado, tag);
                    }else{
                        //Toast.makeText(ComandaGestion.this, "Error no entro", Toast.LENGTH_SHORT).show();
                    }
                    // Cierra el AlertDialog
                    alertDialog.dismiss();
                }
            });
            linearLayoutScroll.addView(button);
        }
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    private void CargarDialogoClientes(){
        // Infla el diseño XML personalizado
        View customLayout = getLayoutInflater().inflate(R.layout.item_dialog, null);

        // Crea un AlertDialog con el diseño personalizado
        AlertDialog.Builder builder = new AlertDialog.Builder(ComandaGestion.this);
        builder.setView(customLayout);

        // Declara una variable para almacenar el número seleccionado
        final AlertDialog alertDialog = builder.create();

        // Obtén la referencia al ScrollView y LinearLayout dentro del AlertDialog
        Button btnAgregar = customLayout.findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent(ComandaGestion.this, AgregarCliente.class);
            startActivityForResult(intent, 1);//Para ejecutar algo justo despues de cerrar la activity
        });
        LinearLayout linearLayoutScroll = customLayout.findViewById(R.id.llClientesScroll);

        for (final Cliente client : lstClientes) {
            Button button = new Button(ComandaGestion.this);
            button.setText(client.getNombre());
            button.setTag(client.getIdCliente());
            button.setOnClickListener(view -> {
                // Almacena el número entero seleccionado en la variable
                cliente = client.getNombre();

                //Establecer nombre en textview
                tvCliente.setText("Cliente: " + cliente);
                //Guardar en la base de datos ActualizarCliente
                Pedido pedido = new Pedido();
                pedido.setIdPedido(idPedido);
                pedido.setIdCliente(client.getIdCliente());
                ActualizarCliente(pedido);
                // Cierra el AlertDialog
                alertDialog.dismiss();
            });

            // Agrega el botón directamente al LinearLayout dentro del ScrollView
            linearLayoutScroll.addView(button);
        }

        // Luego de agregar todos los botones, muestra el AlertDialog
        alertDialog.show();
    }

    private void CargarClientes() {
        ClienteService clienteService = new ClienteService();
        clienteService.obtenerClientes(new CallBackApi<Cliente>() {
            @Override
            public void onResponseList(List<Cliente> response) {
                lstClientes = response;
                CargarDialogoClientes();
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void CargarEmpleados() {
        EmpleadoService empleadoService = new EmpleadoService();
        empleadoService.obtenerEmpleados(new CallBackApi<Empleado>() {
            @Override
            public void onResponseList(List<Empleado> response) {
                lstEmpleados = response;
                CargarDialogoMeseros();
            }
            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }

    private void ActualizarMesero(Pedido pedido, String tag) {
        PedidoService pedidoService = new PedidoService();
        pedidoService.ActualizarMesero(pedido, new CallBackApi<Boolean>() {
            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {
                    //nuevoPedido = null;
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }

    private void ProgramarAcciones(String tag) {
        if (tag.equals("1")) {
            //Comanda
            EnviarListaCompleta();
        } else if (tag.equals("2")) {
            //Extra
            Intent intent = new Intent(ComandaGestion.this, DividirPedidos.class);
            intent.putExtra("idPedido", idPedido);
            intent.putExtra("idMesa", idMesa);
            startActivity(intent);
        } else if (tag.equals("3")) {
            //Cambiar mesa
            cambiarMesa = true;
            idMesaAnterior = idMesa;
            idPedidoCambioMesa = idPedido;
            finish();
        } else if (tag.equals("4")) {
            //Mesero
            CargarEmpleados();
        } else if (tag.equals("5")) {
            //Cliente
            CargarClientes();
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
            btnAccion.setTag(i + 1);
            if (tamano > 0) {
                btnAccion.setEnabled(true);
            } else {
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

    private void CrearAlertaDialogo(String tag, View view) {
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

    private void ObtenerProductosEnMesa(String id, String idPed) {
        progressDialog.show();
        Call<List<PedidoDetalle>> call = ApiClient.getClient().create(PedidoDetalleApi.class).productosEnMesa(id, idPed);
        call.enqueue(new Callback<List<PedidoDetalle>>() {
            @Override
            public void onResponse(Call<List<PedidoDetalle>> call, Response<List<PedidoDetalle>> response) {
                // Si hay respuesta
                try {

                    if (response.isSuccessful()) {
                        lstPedidos = response.body();
                        if (lstPedidos.size() > 0) {
                            PedidoDetalle pedido = lstPedidos.get(0);
                            idPedido = pedido.getIdPedido();

                            ObtenerPedidoPorId(String.valueOf(idPedido));

                            tvTicket.setText("#Ticket: " + idPedido);
                            if (nuevoPedido != null) {
                                nuevoPedido.setIdPedido(idPedido);
                                //Vamos a actualizar el total del pedido cuando es nuevo
                                nuevoPedido.setIdMesa(idMesa);
                                double total = CalcularTotal();
                                nuevoPedido.setTotal(total);
                                ActualizarTotalPedido();

                            }
                            CargarPedidos();
                            CargarPedidosEnMesa();
                        }
                        progressDialog.dismiss();
                    } else {
                        System.out.println("Fallo el isSuccessful");
                    }

                } catch (Exception e) {
                    progressDialog.dismiss();
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

    private void CargarPedidos() {
        if (lstPedidos.size() > 0) {
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
        // Crear un objeto AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Configurar el título y el mensaje
        builder.setTitle("Eliminacion.");
        builder.setMessage("Se disminuira el producto: \n" + pedidoDetalle.getNombre() + "\n ¿Desea continuar?");

        // Configurar el botón positivo (o aceptar)
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Acciones a realizar cuando se hace clic en Aceptar
                pedidoDetalle.setCantidad(pedidoDetalle.getCantidad()-1);
                pedidoDetalle.setSubTotal(CalcularSubTotal(pedidoDetalle.getCantidad(),pedidoDetalle.getPrecio()));

                if(pedidoDetalle.getCantidad() != 0)
                {
                    ActualizarCompra(pedidoDetalle);
                }
                else
                {
                    //Se eliminara el producto
                    EliminarPedidoDetalle(String.valueOf(pedidoDetalle.getIdDetalle()));

                    if (lstPedidos.size() == 1)
                    {
                        Pedido pedido = new Pedido();
                        pedido.setIdPedido(idPedido);
                        //EliminarPedido(pedido);
                        ObtenerProductosEnMesa(String.valueOf(idMesa), String.valueOf(idPedido));

                    }
                    else
                    {
                        ObtenerProductosEnMesa(String.valueOf(idMesa), String.valueOf(idPedido));
                    }
                }

                CargarPedidosEnMesa();
                if (lstPD.size() > 0)
                {
                    for (PedidoDetalle item : lstPD)
                    {
                        if (pedidoDetalle.getIdProducto() == item.getIdProducto())
                        {
                            item.setIdPedido(item.getIdPedido());
                            item.setIdProducto(item.getIdProducto());
                            item.setCantidad(item.getCantidad()-1);
                            if (item.getCantidad() == 0)
                            {
                                lstPD.remove(item);
                            }
                            break;
                        }
                    }
                }

                //Obtener el producto por el id
                BuscarProductoPorId(String.valueOf(pedidoDetalle.getIdProducto()), 0);

            }

        });

        // Configurar el botón negativo (o cancelar)
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Acciones a realizar cuando se hace clic en Cancelar
                // Puedes dejar este bloque vacío si no necesitas realizar ninguna acción específica
            }
        });

        // Mostrar el AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void BuscarProductoPorId(String id, int cantidad) {
        progressDialog.show();
        ProductoService productoService = new ProductoService();
        productoService.buscarProductoPorId(id, cantidad, new CallBackApi<Producto>() {
            @Override
            public void onResponse(Producto response) {
                producto = response;
                // Procesa la respuesta del producto aquí
                if(cantidad>0){
                    ProcesarComanda(cantidad, producto);
                }else{
                    ActualizarStockProductosIngredientes(String.valueOf(producto.getIdProducto()), 1, producto, false);
                }

            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ComandaGestion.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ProcesarComanda(int cantidad, Producto prod) {

        pDetalle = new PedidoDetalle();
        int cant = cantidad;
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        pDetalle.setCantidad(cantidad);
        if (!tvTicket.getText().equals(""))
        {
            pDetalle.setIdPedido(idPedido);
        }
        pDetalle.setMesa(tvMesa.getText().toString());
        pDetalle.setIdProducto(prod.getIdProducto());
        pDetalle.setFecha(fecha);
        pDetalle.setSalon(salon);
        if(cliente != null){
            pDetalle.setCliente(cliente);
        }else{
            pDetalle.setCliente("");
        }
        if(mesero != null){
            pDetalle.setMesero(mesero);
        }else{
            pDetalle.setMesero("");
        }

        pDetalle.setNombre(prod.getNombre());
        pDetalle.setGrupo(prod.getGrupoPrinter());
        Boolean encontrado = false;

        if (lstPD.size() > 0)
        {
            for (PedidoDetalle item : lstPD)
            {
                if (prod.getIdProducto() == item.getIdProducto())
                {
                    encontrado = true;
                    item.setCantidad(cantidad + item.getCantidad());
                    break;
                }
            }
            if (!encontrado)
            {
                lstPD.add(pDetalle);
            }
        }else if (lstPedidos.size() > 0)
        {
            lstPD.add(pDetalle);
        }

        if (lstPedidos.size() > 0) {
            nuevoPedido = new Pedido();
            boolean aumentarUnProducto = false;
            //Saber si ya existe algun producto igual en los detalles
            for (PedidoDetalle pDet : lstPedidos) {
                if (pDet.getIdProducto() == prod.getIdProducto()) {
                    cantidad = cantidad + pDet.getCantidad();
                    aumentarUnProducto = true;
                }
            }

            PedidoDetalle pedidoDetalle = new PedidoDetalle();
            if (aumentarUnProducto) {
                //Ya existe un producto igual en el datgrid, hay que aumentar
                pedidoDetalle.setIdPedido(idPedido);
                pedidoDetalle.setIdProducto(prod.getIdProducto());
                pedidoDetalle.setCantidad(cantidad);
                pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, producto.getPrecio()));
                ActualizarCompra(pedidoDetalle);
            } else {
                //No existe un producto igual en el datgrid, hay que crearlo
                pedidoDetalle.setCocinando(true);
                pedidoDetalle.setExtras("");
                pedidoDetalle.setFecha(fecha);
                pedidoDetalle.setHoraPedido(fecha);
                pedidoDetalle.setHoraEntregado(fecha);
                //pedidoDetalle.IdCocinero = null;
                pedidoDetalle.setIdProducto(prod.getIdProducto());
                pedidoDetalle.setIdPedido(idPedido);
                pedidoDetalle.setCantidad(cantidad);
                pedidoDetalle.setPrecio(producto.getPrecio());
                pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, producto.getPrecio()));
                pedidoDetalle.setGrupo("0");
                pedidoDetalle.setUsuario("1");
                InsertarPedidoDetalle(pedidoDetalle);

            }

        } else {
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
            InsertarPedido(pedido, fecha, cantidad, String.valueOf(prod.getIdProducto()));
        }

        ActualizarStockProductosIngredientes(String.valueOf(prod.getIdProducto()), cant, producto, true);
    }

    private void ActualizarStockProductosIngredientes(String idProducto, int cant, Producto producto, boolean aumentar) {
        IngredienteService ingredienteService = new IngredienteService();
        ingredienteService.buscarngredientesDeProducto(idProducto, new CallBackApi<Ingrediente>() {
            @Override
            public void onResponseList(List<Ingrediente> response) {
                List<Ingrediente> ingredientes = response;
                Ingrediente ingrediente;
                Producto prod;


                if (aumentar)//Si seleccionamos un producto
                {
                    if (ingredientes.size() > 0) {
                        for (Ingrediente item : ingredientes) {
                            ingrediente = new Ingrediente();
                            ingrediente.setIdIngrediente(item.getIdIngrediente());
                            ingrediente.setStock_ingrediente(item.getStock_ingrediente() - CalcularCantidad(cant, item.getCantidad()));
                            ActualizarStockIngrediente(ingrediente);
                        }
                    } else {
                        //El producto no tiene ingredientes
                        prod = new Producto();
                        prod.setIdProducto(Integer.parseInt(idProducto));
                        prod.setStock(producto.getStock()- cant);
                        ActualizarStockProducto(prod);
                    }
                } else//Si disminuimos
                {
                    if (ingredientes.size() > 0) {
                        for(Ingrediente item : ingredientes)
                        {
                            ingrediente = new Ingrediente();
                            ingrediente.setIdIngrediente(item.getIdIngrediente());
                            ingrediente.setStock_ingrediente(item.getStock_ingrediente() + CalcularCantidad(cant, item.getCantidad()));
                            ActualizarStockIngrediente(ingrediente);
                        }
                    } else {
                        //El producto no tiene ingredientes
                        prod = new Producto();
                        prod.setIdProducto(Integer.parseInt(idProducto));
                        prod.setStock(producto.getStock() + cant);
                        ActualizarStockProducto(prod);
                    }
                }
            }
            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void ActualizarStockIngrediente(Ingrediente ingrediente) {
        IngredienteService ingredienteService = new IngredienteService();
        ingredienteService.ActualizarStockIngrediente(ingrediente, new CallBackApi<Boolean>() {
            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {

                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta actualizar producto: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void ActualizarStockProducto(Producto prod) {
        ProductoService productoService = new ProductoService();
        productoService.ActualizarStockProducto(prod, new CallBackApi<Boolean>() {
            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {

                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta actualizar producto: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                Toast.makeText(ComandaGestion.this, "Error en la respuesta: " + errorMessage.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private int CalcularCantidad(int cant, int cantidad) {
        return cant*cantidad;
    }


    private void ActualizarCompra(PedidoDetalle pedidoDetalle) {
        PedidoDetalleService pedidoDetalleService = new PedidoDetalleService();
        pedidoDetalleService.ActualizarCompra(pedidoDetalle, new CallBackApi<Boolean>() {
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
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void ActualizarCliente(Pedido pd){
        PedidoService pedidoService = new PedidoService();
        pedidoService.ActualizarCliente(pd, new CallBackApi<Boolean>() {
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
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void ActualizarTotalPedido() {
        PedidoService pedidoService = new PedidoService();
        pedidoService.ActualizarTotal(nuevoPedido, new CallBackApi<Boolean>() {
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
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void ActualizarEstadoMesa(Mesa mesa) {
        MesaService mesaService = new MesaService();
        mesaService.ActualizarEstadoMesa(mesa, new CallBackApi<Boolean>() {
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
            public void onFailure(String errorMessage) {
                Toast.makeText(ComandaGestion.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InsertarPedidoDetalle(PedidoDetalle pedidoDetalle) {
        PedidoDetalleService pedidoDetalleService = new PedidoDetalleService();
        pedidoDetalleService.InsertarPedidoDetalle(pedidoDetalle, new CallBackApi<Boolean>() {
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
                    ObtenerPedidoPorId(String.valueOf(idPedido));//Justo despues de agregar lo consultamos
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
                        pDetalle.setIdPedido(idPedido);
                        lstPD.add(pDetalle);
                        pedidoDetalle.setCantidad(cantidad);
                        pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, producto.getPrecio()));
                        pedidoDetalle.setGrupo("0");
                        pedidoDetalle.setUsuario("1");
                        //pedidoDetalle.setFecha(fecha);

                        InsertarPedidoDetalle(pedidoDetalle);

                }else {
                    // Hubo un error en la inserción del pedido
                    Toast.makeText(ComandaGestion.this, "Hubo un error al insertar pedido", Toast.LENGTH_SHORT).show();
                }
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
                                // Recuperar la imagen y asignarla directamente al botón
                                ProductoService productoService = new ProductoService();
                                productoService.recuperarImagen(producto.getFoto(), new ProductoService.OnImageLoadListener() {
                                    @Override
                                    public void onImageLoad(Bitmap bitmap) {
                                        // Redimensionar el Bitmap al tamaño deseado
                                        int width = 200; // Ancho en píxeles
                                        int height = 200; // Alto en píxeles
                                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

                                        // Asignar el Bitmap como icono del botón
                                        btnProducto.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(getResources(), resizedBitmap), null, null);
                                    }

                                    @Override
                                    public void onImageLoadError(String errorMessage) {// Manejar el caso en el que la recuperación de la imagen falló
                                        // Redimensionar la imagen por defecto al tamaño deseado
                                        int defaultWidth = 200; // Ancho en píxeles
                                        int defaultHeight = 200; // Alto en píxeles
                                        Drawable defaultDrawable = getResources().getDrawable(R.drawable.productos);
                                        // Ajustar el tamaño de la imagen por defecto
                                        defaultDrawable.setBounds(0, 0, defaultWidth, defaultHeight);
                                        // Asignar la imagen por defecto encima del texto en el botón
                                        btnProducto.setCompoundDrawables(null, defaultDrawable, null, null);

                                    }
                                });
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Enviar();
        }
        return super.onKeyDown(keyCode, event);

    }
    private void Enviar(){
        enviarListaTask.enviarLista(lstPD);
    }
    private void EnviarListaCompleta(){
        for (PedidoDetalle pDetalle : lstPedidos) {
            if(cliente != null){
                pDetalle.setCliente(cliente);
            }else{
                pDetalle.setCliente("");
            }
            if(mesero != null){
                pDetalle.setMesero(mesero);
            }else{
                pDetalle.setMesero("");
            }
            if(salon != null){
                pDetalle.setSalon(salon);
            }else{
                pDetalle.setSalon("");
            }
            if(mesa != null){
                pDetalle.setMesa(mesa);
            }else{
                pDetalle.setMesa("");
            }
            pDetalle.setFecha(formatoFecha(pDetalle.getFecha()));
        }
        EnviarListaTask enviarListaTask2 = new EnviarListaTask(ComandaGestion.this);
        enviarListaTask2.enviarListaCompleta(lstPedidos);
    }

    // Método para convertir el formato de la fecha
    private static String formatoFecha(String fechaString) {
        DateFormat formatoOriginal = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        DateFormat formatoNuevo = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        try {
            Date fecha = formatoOriginal.parse(fechaString);
            return formatoNuevo.format(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // o manejar el error de alguna otra manera
        }
    }
    private void ObtenerPedidoPorId(String id){
        PedidoService pedidoService = new PedidoService();

        pedidoService.obtenerPedidoPorId(id, new CallBackApi<Pedido>() {
            @Override
            public void onResponse(Pedido response) {
                cliente = response.getNombre();
                mesero = response.getNombres();
                mesa = response.getMesa();
                tvMesa.setText(mesa);
                if(mesero!=null){
                    tvMesero.setText("Mesero: " + mesero);
                }
                if(cliente!=null){
                    tvCliente.setText("Cliente: " + cliente);
                }


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
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta al eliminar detallepedido: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { // Verifica que el código de solicitud sea el mismo que el que usaste al llamar startActivityForResult
            if (resultCode == Activity.RESULT_OK) {
                // Aquí puedes ejecutar el método que deseas después de cerrar la Activity
                // Este bloque de código se ejecutará después de cerrar AgregarCliente
                CargarClientes();
            }
        }
    }
}