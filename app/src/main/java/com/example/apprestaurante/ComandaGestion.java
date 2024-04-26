package com.example.apprestaurante;

import static com.example.apprestaurante.AgregarPedidos.Formulario;
import static com.example.apprestaurante.AgregarPedidos.PermisoBorra;
import static com.example.apprestaurante.AgregarPedidos.PermisoLista;
import static com.example.apprestaurante.AgregarPedidos.cuentaUnica;
import static com.example.apprestaurante.DividirPedidos.cargarLista;
import static com.example.apprestaurante.MainActivity.usuario;
import static com.example.apprestaurante.MesasSalones.cambiarMesa;
import static com.example.apprestaurante.MesasSalones.enviarListaTask;
import static com.example.apprestaurante.MesasSalones.idMesaAnterior;
import static com.example.apprestaurante.MesasSalones.idPedidoCambioMesa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apprestaurante.adapters.PedidosAdapter;
import com.example.apprestaurante.clases.Cliente;
import com.example.apprestaurante.clases.Configuracion;
import com.example.apprestaurante.clases.Empleado;
import com.example.apprestaurante.clases.Familia;
import com.example.apprestaurante.clases.Ingrediente;
import com.example.apprestaurante.clases.Mesa;
import com.example.apprestaurante.clases.Pedido;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.clases.PedidoDetalleLog;
import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.clases.Usuario;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.PedidoDetalleApi;
import com.example.apprestaurante.interfaces.ProductoApi;
import com.example.apprestaurante.interfaces.UsuarioApi;
import com.example.apprestaurante.network.ApiClient;
import com.example.apprestaurante.services.ClienteService;
import com.example.apprestaurante.services.ConfiguracionService;
import com.example.apprestaurante.services.EmpleadoService;
import com.example.apprestaurante.services.FamiliaService;
import com.example.apprestaurante.services.IngredienteService;
import com.example.apprestaurante.services.MesaService;
import com.example.apprestaurante.services.PedidoDetalleLogService;
import com.example.apprestaurante.services.PedidoDetalleService;
import com.example.apprestaurante.services.PedidoService;
import com.example.apprestaurante.services.ProductoService;
import com.example.apprestaurante.utils.EnviarListaTask;
import com.example.apprestaurante.utils.Load;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComandaGestion extends AppCompatActivity implements  PedidosAdapter.OnItemClickListener {

    Load progressDialog;
    public int idMesa, idPedido = 0;
    int idPedido2 = 0;
    public static Boolean Permiso = false;
    Boolean estado = false;
    public static String salon;
    String cliente, mesero, mesa;
    TextView tvTicket, textProductos, tvMesa, tvCliente, tvMesero;
    RecyclerView rcvPedidos;
    private LinearLayoutManager layoutManager;
    LinearLayout llFamilias, llProductos, llAcciones;
    List<Familia> lstFamilias;
    public static List<Integer> lstPedidosEnMesa = null;
    public static List<Integer> lstPedidosVacios = null;
    public static List<Cliente> lstClientes = null;
    public static List<Empleado> lstEmpleados = null;
    public static List<Configuracion> lstConfiguracion = null;

    public static List<Producto> lstProductos;
    Producto producto;
    Pedido nuevoPedido = null;
    Button btnCuentas, btnEstilo;
    PedidoDetalle pDetalle;
    public static List<PedidoDetalle> lstPedidos = new ArrayList<>();
    List<PedidoDetalle> lstPD = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new Load(this, "Cargando...");

        setContentView(R.layout.activity_comanda_gestion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        btnEstilo = findViewById(R.id.btnEstilo);
        btnEstilo.setVisibility(View.GONE);
        Drawable background = btnEstilo.getBackground();

        //Cargar la configuracion
        CargarConfiguracion();

        //Obteniendo idMesa
        Intent intent = getIntent();
        if (intent != null) {
            idMesa = intent.getIntExtra("idMesa", 0); // El segundo parámetro (0) es el valor predeterminado si no se encuentra "idMesa"
            if (Formulario) {
                Permiso = PermisoLista;
                idPedido2 = intent.getIntExtra("idPedido", 0); // El segundo parámetro (0) es el valor predeterminado si no se encuentra "idMesa"
                ActualizarEncabezado(idPedido2);
            }
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
                if (estado) {
                    for (final Integer integer : lstPedidosVacios) {
                        Button button = new Button(ComandaGestion.this);
                        button.setText(integer.toString());
                        String texto = button.getText().toString();
                        int id = Integer.parseInt(texto);
                        if (id == idPedido2) {
                            button.setBackground(background);
                            button.setTextColor(Color.BLACK);
                        }
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (lstPedidosVacios.size() > 0) {
                                    CharSequence texto = tvTicket.getText();
                                    String textoString = texto.toString();
                                    int id = lstPedidosVacios.get(0);
                                    int can = rcvPedidos.getAdapter().getItemCount();
                                    String nombre = "#Ticket: " + id;
                                    Log.d("nombre de tvTicket: ", textoString + " Ticket de lista: " + nombre);

                                    if (can == 0 && textoString.equals(nombre)) {
                                        Toast.makeText(getApplicationContext(), "No se puede cambiar de cuenta, porque esta vacia", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Almacena el número entero seleccionado en la variable
                                        idPedido2 = integer;
                                        cliente = null;
                                        mesero = null;
                                        CargarPedidoPorId(String.valueOf(idPedido2));
                                        alertDialog.dismiss();
                                    }
                                }
                            }
                        });
                        linearLayoutScroll.addView(button);
                    }
                } else {
                    for (final Integer integer : lstPedidosEnMesa) {
                        Button button = new Button(ComandaGestion.this);
                        button.setText(integer.toString());
                        String texto = button.getText().toString();
                        int id = Integer.parseInt(texto);
                        if (id == idPedido2) {
                            button.setBackground(background);
                            button.setTextColor(Color.BLACK);
                        }
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Almacena el número entero seleccionado en la variable
                                idPedido = integer;
                                cliente = null;
                                mesero = null;
                                ObtenerProductosEnMesa(String.valueOf(idMesa), String.valueOf(idPedido));
                                // Cierra el AlertDialog
                                alertDialog.dismiss();
                            }
                        });
                        linearLayoutScroll.addView(button);
                    }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            EnviarListaParcial();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void CargarPedidoPorId(String id) {
        PedidoService pedidoService = new PedidoService();
        pedidoService.obtenerPedidoPorId(id, new CallBackApi<Pedido>() {
            @Override
            public void onResponse(Pedido response) {
                cliente = response.getNombre();
                mesero = response.getNombres();
                mesa = response.getMesa();
                tvTicket.setText("#Ticket: " + id);
                tvMesa.setText(mesa);
                if (mesero != null) {
                    tvMesero.setText("Mesero: " + mesero);
                } else {
                    tvMesero.setText("Mesero: ");
                }
                if (cliente != null) {
                    tvCliente.setText("Cliente: " + cliente);
                } else {
                    tvCliente.setText("Cliente: ");
                }
                ObtenerProductosEnMesa2(String.valueOf(idMesa), String.valueOf(id));
            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
            }

            @Override
            public void onResponseList(List<Pedido> response) {
            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
            }
        });
    }

    private void ActualizarEncabezado(int idP) {
        tvTicket.setText("#Ticket: " + idP);
        CargarPedidosVacios(false);
    }

    private void CargarPedidosVacios(Boolean eliminar) {
        PedidoService pedidoService = new PedidoService();
        pedidoService.obtenerPedidosVacios(String.valueOf(idMesa), new CallBackApi<Integer>() {
            @Override
            public void onResponse(Integer response) {
            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
            }

            @Override
            public void onResponseList(List<Integer> response) {
                lstPedidosVacios = response;
                if (lstPedidosVacios.size() > 0) {
                    btnCuentas.setVisibility(View.VISIBLE);
                    estado = true;
                    cargarLista = false;
                }
                if (eliminar) {
                    if (lstPedidosVacios.size() == 0) {
                        //Vamos a eliminar el pedido y por lo tanto actualizar estado de mesa
                        Mesa mesa = new Mesa();
                        mesa.setIdMesa(idMesa);
                        mesa.setDisponible(true);
                        ActualizarEstadoMesa(mesa);
                        idPedido = 0;
                        tvTicket.setText("#Ticket: ");
                        tvMesero.setText("Mesero: ");
                        tvCliente.setText("Cliente: ");
                        finish();
                    } else {
                        for (int idNuevo : lstPedidosVacios) {
                            idPedido2 = idNuevo;
                            ActualizarEncabezado(idPedido2);
                        }
                    }
                    ActualizarVista();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }

    private void ActualizarVista() {
        PedidoService pedidoService = new PedidoService();
        pedidoService.obtenerPedidosVacios(String.valueOf(idMesa), new CallBackApi<Integer>() {
            @Override
            public void onResponse(Integer response) {
            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
            }

            @Override
            public void onResponseList(List<Integer> response) {
                lstPedidosVacios = response;
                if (lstPedidosVacios.size() == 1) {
                    btnCuentas.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }

    private void BorrarPedidosVacios() {
        PedidoService pedidoService = new PedidoService();
        pedidoService.obtenerPedidosVacios(String.valueOf(idMesa), new CallBackApi<Integer>() {
            @Override
            public void onResponse(Integer response) {
            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
            }

            @Override
            public void onResponseList(List<Integer> response) {
                lstPedidosVacios = response;
                if (lstPedidosVacios.size() > 0) {
                    for (int idPedid : lstPedidosVacios) {
                        EliminarPedido(idPedid);
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }

    private void CargarPedidosEnMesa(boolean eliminar) {
        progressDialog.show();
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
                if (lstPedidosEnMesa.size() > 1) {
                    btnCuentas.setVisibility(View.VISIBLE);
                }
                if (eliminar) {
                    if (lstPedidosEnMesa.size() == 0) {
                        //Vamos a eliminar el pedido y por lo tanto actualizar estado de mesa
                        Mesa mesa = new Mesa();
                        mesa.setIdMesa(idMesa);
                        mesa.setDisponible(true);
                        ActualizarEstadoMesa(mesa);
                        idPedido = 0;
                        tvTicket.setText("#Ticket: ");
                        tvMesero.setText("Mesero: ");
                        tvCliente.setText("Cliente: ");
                        finish();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
            }
        });
    }

    private void CargarDialogoMeseros() {
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
            button.setText(empl.getNombres());
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
                        ActualizarMesero(pedEmpleado);
                    } else {
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

    private void CargarDialogoClientes() {
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
            public void onResponse(Cliente response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {

            }

            @Override
            public void onResponseList(List<Cliente> response) {
                lstClientes = response;
                CargarDialogoClientes();
            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
            }
        });
    }

    private void CargarEmpleados() {
        EmpleadoService empleadoService = new EmpleadoService();
        empleadoService.obtenerEmpleados(new CallBackApi<Empleado>() {
            @Override
            public void onResponse(Empleado response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {

            }

            @Override
            public void onResponseList(List<Empleado> response) {
                lstEmpleados = response;
                CargarDialogoMeseros();
            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
            }
        });
    }

    private void CargarConfiguracion() {
        ConfiguracionService configuracionService = new ConfiguracionService();
        configuracionService.obetnerConfiguracion(new CallBackApi<Configuracion>() {
            @Override
            public void onResponse(Configuracion response) {
            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
            }

            @Override
            public void onResponseList(List<Configuracion> response) {
                lstConfiguracion = response;
            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
            }
        });
    }

    public void ActualizarMesero(Pedido pedido) {
        PedidoService pedidoService = new PedidoService();
        pedidoService.ActualizarMesero(pedido, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

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
            public void onResponseList(List<Boolean> response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
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
            Toast.makeText(this, "Esta en mantenimiento!", Toast.LENGTH_SHORT).show();
            //CargarClientes();
        } else if (tag.equals("6")) {
            //Total
            VerTotal();
        } else if (tag.equals("7")) {
            //Imprimir Total
            ImprimirTotal();
        }
    }

    private void ImprimirTotal() {
        for (PedidoDetalle pDetalle : lstPedidos) {
            pDetalle.setFecha(formatoFecha(pDetalle.getFecha()));
            if (cliente != null) {
                pDetalle.setCliente(cliente);
            } else {
                pDetalle.setCliente("");
            }
            if (mesero != null) {
                pDetalle.setMesero(mesero);
            } else {
                pDetalle.setMesero("");
            }
            if (salon != null) {
                pDetalle.setSalon(salon);
            } else {
                pDetalle.setSalon("");
            }
            if (mesa != null) {
                pDetalle.setMesa(mesa);
            } else {
                pDetalle.setMesa("");
            }
        }
        EnviarListaTask enviarListaTask2 = new EnviarListaTask(ComandaGestion.this);
        enviarListaTask2.enviarTotalPedido(lstPedidos);
    }

    private void VerTotal() {
        // Crear el AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("El total de la comanda es: ");

        // Crear un EditText en el AlertDialog
        final TextView output = new TextView(this);
        DecimalFormat df = new DecimalFormat("#.00");

        double total = CalcularTotal();
        double propina = CalcularPorcentaje(total);

        output.setTextSize(40);
        output.setGravity(View.TEXT_ALIGNMENT_CENTER);
        output.setText("$" + df.format(CalcularTotal() + propina));
        alertDialogBuilder.setView(output);

        alertDialogBuilder.setPositiveButton("Aceptar", (dialog, which) -> {

        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void TienePermiso(PedidoDetalle pedidoDetalle) {

        if (lstConfiguracion != null && !lstConfiguracion.isEmpty()) {
            Configuracion config = lstConfiguracion.get(0);
            if (config.getAutorizarDescProp() == 1) {
                //Necesita autorizacion
                CrearAlertaDialogoDeAcceso(pedidoDetalle);
            } else {
                //No necesita autorizacion
                ContinuarEliinacion(pedidoDetalle);
            }
        }
    }

    private void ContinuarEliinacion(PedidoDetalle pedidoDetalle) {
        //Vamos a ver si guardar en la base de datos
        GuardarEliminacion(pedidoDetalle);
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
        }

        CargarPedidosEnMesa(false);

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

    private double CalcularPorcentaje(double total) {
        double porcentaje = 0;
        DecimalFormat df = new DecimalFormat("#.00");
        if (lstConfiguracion != null && !lstConfiguracion.isEmpty()) {
            Configuracion config = lstConfiguracion.get(0);
            if (config.getIncluirPropina() == 1) {
                porcentaje = (total * (1 + (config.getPropina() / 100))) - total;
            } else {
                porcentaje = 0;
            }
        }
        return Double.parseDouble(df.format(porcentaje));
    }

    private void CrearBotones(View.OnClickListener accionClickListener) {
        llAcciones.removeAllViews();
        String[] valores = {"comanda", "Extras", "Cambiar mesa", "Mesero", "Cliente", "Total comanda", "Imprimir Total"};
        int[] imagen = {R.drawable.comanda, R.drawable.extras, R.drawable.cambio_mesa, R.drawable.mesero, R.drawable.cliente, R.drawable.total, R.drawable.total};
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
                progressDialog.dismiss();
                Toast.makeText(this, "No ingreso ningun valor o valor no valido.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private Boolean CrearAlertaDialogoDeAcceso(PedidoDetalle pedidoDetalle) {
        AtomicReference<Boolean> conPermiso = new AtomicReference<>(false);
        // Crear el AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ComandaGestion.this);
        alertDialogBuilder.setTitle("Digite el pin de un administrador: ");

        // Crear un EditText en el AlertDialog
        final EditText input = new EditText(ComandaGestion.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setPositiveButton("Aceptar", (dialog, which) -> {
            try {
                int number = Integer.parseInt(input.getText().toString());
                // Hacer algo con el número ingresado
                conPermiso.set(ComprobarPin(String.valueOf(number), pedidoDetalle));

            } catch (NumberFormatException e) {
                progressDialog.dismiss();
                Toast.makeText(this, "No ingreso ningun valor o valor no valido.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        return conPermiso.get();
    }

    private Boolean ComprobarPin(String pin, PedidoDetalle pedidoDetalle) {
        final Boolean[] conPermiso = {false};
        progressDialog.show();
        Call<Usuario> call = ApiClient.getClient().create(UsuarioApi.class).ObtenerDatosUsuario(pin);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                //Si hay respuesta
                try {
                    if (response.isSuccessful()) {
                        usuario = response.body();
                        if(usuario != null){
                            if(usuario.getIdRol() == 1){
                                //Continuar la eliminacion
                                ContinuarEliinacion(pedidoDetalle);
                            }else{
                                Toast.makeText(ComandaGestion.this, "No tiene permiso para realizar esta accion.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(ComandaGestion.this, "No tiene permiso para realizar esta accion.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(ComandaGestion.this, "Error en conexion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                //Si hay error
                Toast.makeText(ComandaGestion.this, "Error en conexion de red." + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexion de red." + t.getMessage());
                progressDialog.dismiss();
            }
        });
        return conPermiso[0];
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
                progressDialog.dismiss();
                Toast.makeText(ComandaGestion.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ObtenerProductosEnMesa2(String id, String idPed) {
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

                            ObtenerPedidoPorId(String.valueOf(idPedido2));
                            if (nuevoPedido != null) {
                                nuevoPedido.setIdPedido(idPedido);
                                //Vamos a actualizar el total del pedido cuando es nuevo
                                nuevoPedido.setIdMesa(idMesa);
                                double total = CalcularTotal();
                                nuevoPedido.setTotal(total);
                                ActualizarTotalPedido();
                            }
                            CargarPedidosEnMesa(false);
                        }
                        CargarPedidos();
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
                progressDialog.dismiss();
                Toast.makeText(ComandaGestion.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexión de red: " + t.getMessage());
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
                            CargarPedidosEnMesa(false);
                        }
                        CargarPedidos();
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
                progressDialog.dismiss();
                Toast.makeText(ComandaGestion.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexión de red: " + t.getMessage());
            }
        });
    }

    private void CargarPedidos() {
        // Configurando adaptador
        PedidosAdapter pedidosAdapter = new PedidosAdapter(this, lstPedidos, ComandaGestion.this);
        layoutManager = new LinearLayoutManager(this);
        rcvPedidos = findViewById(R.id.rcvPedidos);
        rcvPedidos.setAdapter(pedidosAdapter);
        rcvPedidos.setLayoutManager(layoutManager);
        rcvPedidos.setHasFixedSize(true);

        View.OnClickListener accionClickListener = view -> {
            // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
            String tag = String.valueOf(view.getTag());
            ProgramarAcciones(tag);
        };

        PedidosAdapter pedidosAdapter1 = (PedidosAdapter) rcvPedidos.getAdapter();
        pedidosAdapter1.setOnItemLongClickListener(new PedidosAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(PedidoDetalle pedidoDetalle) {
                CrearAlertaDialogoCambioPrecio(pedidoDetalle);
                //f.lblPrecio.Text = Double.Parse(dgvDatos.CurrentRow.Cells["precio"].Value.ToString()).ToString("0.00");
                //f.lblProducto.Text = dgvDatos.CurrentRow.Cells["nombre"].Value.ToString();
                //f.ShowDialog();
            }
        });
        CrearBotones(accionClickListener);
    }

    private void CrearAlertaDialogoCambioPrecio(PedidoDetalle pedidoDetalle) {
        // Crear el AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ComandaGestion.this);
        alertDialogBuilder.setTitle("Cambio de precio");

        // Crear un contenedor para las vistas
        LinearLayout layout = new LinearLayout(ComandaGestion.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Crear vistas
        final TextView tvNombre = new TextView(ComandaGestion.this);
        final TextView tvPrecioAnterior = new TextView(ComandaGestion.this);
        final EditText input = new EditText(ComandaGestion.this);

        // Configurar vistas
        tvNombre.setText(pedidoDetalle.getNombre().toString());
        tvPrecioAnterior.setText("Precio anterior : " + pedidoDetalle.getPrecio());
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Agregar vistas al contenedor
        layout.addView(tvNombre);
        layout.addView(tvPrecioAnterior);
        layout.addView(input);

        // Establecer el contenedor como la vista del diálogo
        alertDialogBuilder.setView(layout);

        // Configurar botones
        alertDialogBuilder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Lógica para procesar el valor ingresado
            try {
                Double number = Double.parseDouble(input.getText().toString());
                // Hacer algo con el número ingresado
                // Puedes usar 'number' aquí como lo necesites
                int idDetalle = pedidoDetalle.getIdDetalle();
                int cantidad = pedidoDetalle.getCantidad();
                int idProducto = pedidoDetalle.getIdProducto();
                int idPedido = pedidoDetalle.getIdPedido();
                double precioNuevo = number;
                double subTotal = cantidad*precioNuevo;

                PedidoDetalle pd = new PedidoDetalle();
                pd.setIdDetalle(idDetalle);
                pd.setCantidad(cantidad);
                pd.setPrecio(precioNuevo);
                pd.setSubTotal(subTotal) ;
                pd.setIdPedido(idPedido);
                pd.setIdProducto(idProducto);

                ActualizarCompra(pd);
            } catch (NumberFormatException e) {
                progressDialog.dismiss();
                Toast.makeText(this, "No ingreso ningun valor o valor no valido.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                TienePermiso(pedidoDetalle);
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

    private void GuardarEliminacion(PedidoDetalle pedidoDetalle) {
        Boolean guardar = true;
        if(lstPD.size() > 0){
            //Vamos a verificar si hay igual alguno
            for (PedidoDetalle item : lstPD) {
                if(pedidoDetalle.getIdProducto() == item.getIdProducto()){
                    //No debo insertar o modificar
                    guardar = false;
                    break;
                }
            }
        }

        if(guardar){
            //Debo insertar o modificar
            ObtenerPedidoEliminado(pedidoDetalle);
        }
    }

    private void ObtenerPedidoEliminado(PedidoDetalle pD) {
        PedidoDetalleLogService logService = new PedidoDetalleLogService();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            logService.pedidoEliminado(String.valueOf(pD.getIdDetalle()), new CallBackApi<PedidoDetalleLog>() {
                @Override
                public void onResponse(PedidoDetalleLog response) {
                    PedidoDetalleLog detalleLog = response;

                    String fechaFormateada = "";
                    try {
                        // Verificar la versión de Android
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            // Obtener la fecha y hora actual
                            LocalDateTime fechaHoraActual = LocalDateTime.now();

                            // Definir el formato deseado
                            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                            // Formatear la fecha y hora actual
                            fechaFormateada = fechaHoraActual.format(formato);

                        } else {
                            System.out.println("La versión de Android no es compatible con las clases java.time.");
                        }
                    } catch (Exception e) {
                        System.out.println("Error al obtener la fecha y hora actual: " + e.getMessage());
                    }

                    if(detalleLog != null){

                        //Vamos actualizar
                        PedidoDetalleLog logDetalle = new PedidoDetalleLog();
                        logDetalle.setIdDeleted(detalleLog.getIdDeleted());
                        logDetalle.setCantidad(detalleLog.getCantidad() + 1);
                        logDetalle.setUsuarioDelete(usuario.getIdUsuario());
                        logDetalle.setFechaDelete(fechaFormateada);
                        logDetalle.setSubTotal(detalleLog.getPrecio() * (detalleLog.getCantidad() + 1));

                        ActualizarDetalleLog(logDetalle);
                    }else{
                        //Vamos a insertar
                        PedidoDetalleLog logDetalle = new PedidoDetalleLog();
                        logDetalle.setCantidad(1);
                        logDetalle.setUsuarioDelete(usuario.getIdUsuario());
                        logDetalle.setFechaDelete(fechaFormateada);
                        logDetalle.setPrecio(pD.getPrecio());
                        logDetalle.setSubTotal(pD.getPrecio());
                        logDetalle.setIdDetalle(pD.getIdDetalle());
                        logDetalle.setIdProducto(pD.getIdProducto());
                        logDetalle.setIdPedido(pD.getIdPedido());
                        String fechaPedido = null;
                        try {
                            // Verificar si la versión de Android es compatible con java.time
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                // Obtener la cadena de fecha y hora del pedido de algún objeto pD
                                String horaPedidoStr = pD.getHoraPedido();

                                // Definir el formato deseado
                                DateTimeFormatter formato = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

                                // Intentar analizar la cadena de fecha y hora con el formato deseado
                                LocalDateTime horaPedido = LocalDateTime.parse(horaPedidoStr, formato);

                                // Formatear la fecha y hora del pedido
                                formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                fechaPedido = horaPedido.format(formato);
                            } else {
                                System.out.println("La versión de Android no es compatible con las clases java.time.");
                            }
                        } catch (DateTimeParseException e) {
                            System.out.println("Error al obtener o formatear la fecha y hora del pedido: " + e.getMessage());
                        } catch (Exception e) {
                            System.out.println("Error general al obtener o formatear la fecha y hora del pedido: " + e.getMessage());
                        }

                        // Establecer la fecha y hora formateadas en logDetalle
                        logDetalle.setHoraEntregado(fechaPedido);
                        logDetalle.setHoraPedido(fechaPedido);
                        logDetalle.setCocinando(pD.getCocinando());
                        logDetalle.setExtras(pD.getExtras());

                        InsertarDetalleLog(logDetalle);
                    }
                }

                @Override
                public void onResponseBool(Response<Boolean> response) {

                }

                @Override
                public void onResponseList(List<PedidoDetalleLog> response) {

                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(ComandaGestion.this, "Ocurrio un errror" + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void InsertarDetalleLog(PedidoDetalleLog detalleLog) {
        PedidoDetalleLogService logService = new PedidoDetalleLogService();
        logService.InsertarPedidoDetalle(detalleLog, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {
                    //nuevoPedido = null;
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
                    System.out.println(response.message());
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ComandaGestion.this, "Ocurrio un errror" + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ActualizarDetalleLog(PedidoDetalleLog logDetalle) {
        PedidoDetalleLogService logService = new PedidoDetalleLogService();
        logService.ActualizarPedidoDetalleLog(logDetalle, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {
                    //nuevoPedido = null;
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
                    System.out.println(response.message());
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ComandaGestion.this, "Ocurrio un errror" + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void EliminarPedido(int idPed) {
        PedidoService pedidoService = new PedidoService();

        pedidoService.EliminarPedido(String.valueOf(idPed), new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
            }
            @Override
            public void onResponseBool(Response<Boolean> response) {
                if(response.isSuccessful()){
                    if (PermisoBorra){
                        CargarPedidosVacios(true);
                    }else{
                        CargarPedidosEnMesa(true);
                    }
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
            }
        });
    }

    private void BuscarProductoPorId(String id, int cantidad) {
        progressDialog.show();
        ProductoService productoService = new ProductoService();
        productoService.buscarProductoPorId(id, cantidad, new CallBackApi<Producto>() {
            @Override
            public void onResponse(Producto response) {
                producto = response;
                // Procesa la respuesta del producto aquí
                int itemCount = rcvPedidos.getAdapter().getItemCount();
                if (cantidad > 0) {
                    if (cuentaUnica) {
                        ProcesarComanda(cantidad, producto);
                        cuentaUnica = false;
                    } else {
                        if (Formulario || itemCount == 0) {
                            ProcesarComandaConTicket(cantidad, producto, idPedido2);
                        } else {
                            ProcesarComanda(cantidad, producto);
                        }
                    }
                }else{
                    ActualizarStockProductosIngredientes(String.valueOf(producto.getIdProducto()), 1, producto, false);
                }
            }

            @Override
            public void onResponseBool(Response<Boolean> response) {}
            @Override
            public void onResponseList(List<Producto> response) {}
            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(ComandaGestion.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ProcesarComanda(int cantidad, Producto prod) {
        pDetalle = new PedidoDetalle();
        int cant = cantidad;
        String fechaFormateada = "";
        try {
            // Verificar la versión de Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Obtener la fecha y hora actual
                LocalDateTime fechaHoraActual = LocalDateTime.now();

                // Definir el formato deseado
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                // Formatear la fecha y hora actual
                fechaFormateada = fechaHoraActual.format(formato);

            } else {
                System.out.println("La versión de Android no es compatible con las clases java.time.");
            }
        } catch (Exception e) {
            System.out.println("Error al obtener la fecha y hora actual: " + e.getMessage());
        }
        //String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        pDetalle.setCantidad(cantidad);
        if (!tvTicket.getText().equals("")) {
            pDetalle.setIdPedido(idPedido);
        }
        pDetalle.setMesa(tvMesa.getText().toString());
        pDetalle.setIdProducto(prod.getIdProducto());
        pDetalle.setFecha(fechaFormateada);
        pDetalle.setSalon(salon);
        if (cliente != null) {
            pDetalle.setCliente(cliente);
        } else {
            pDetalle.setCliente("");
        }
        if (mesero != null) {
            pDetalle.setMesero(mesero);
        } else {
            pDetalle.setMesero("");
        }

        pDetalle.setNombre(prod.getNombre());
        pDetalle.setGrupo(prod.getGrupoPrinter());
        Boolean encontrado = false;

        if (lstPD.size() > 0) {
            for (PedidoDetalle item : lstPD) {
                if (prod.getIdProducto() == item.getIdProducto()) {
                    encontrado = true;
                    item.setCantidad(cantidad + item.getCantidad());
                    break;
                }
            }
            if (!encontrado) {
                lstPD.add(pDetalle);
            }
        } else if (lstPedidos.size() > 0) {
            lstPD.add(pDetalle);
        }

        if (lstPedidos.size() > 0) {
            nuevoPedido = new Pedido();
            boolean aumentarUnProducto = false;
            Double precio = 0.00;
            //Saber si ya existe algun producto igual en los detalles
            for (PedidoDetalle pDet : lstPedidos) {
                if (pDet.getIdProducto() == prod.getIdProducto()) {
                    cantidad = cantidad + pDet.getCantidad();
                    precio = pDet.getPrecio();
                    aumentarUnProducto = true;
                }
            }

            PedidoDetalle pedidoDetalle = new PedidoDetalle();
            if (aumentarUnProducto) {
                //Ya existe un producto igual en el datgrid, hay que aumentar
                pedidoDetalle.setIdPedido(idPedido);
                pedidoDetalle.setIdProducto(prod.getIdProducto());
                pedidoDetalle.setCantidad(cantidad);
                pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, precio));
                ActualizarCompra(pedidoDetalle);
            } else {
                //No existe un producto igual en el datgrid, hay que crearlo
                pedidoDetalle.setCocinando(true);
                pedidoDetalle.setExtras("");
                pedidoDetalle.setFecha(fechaFormateada);
                pedidoDetalle.setHoraPedido(fechaFormateada);
                pedidoDetalle.setHoraEntregado(fechaFormateada);
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

            //Actualizar estado de la mesa
            Mesa mesa = new Mesa();
            mesa.setIdMesa(idMesa);
            mesa.setDisponible(false);
            ActualizarEstadoMesa(mesa);

            //Creamos el pedido
            InsertarPedido(pedido, fechaFormateada, cantidad, String.valueOf(prod.getIdProducto()));
        }

        ActualizarStockProductosIngredientes(String.valueOf(prod.getIdProducto()), cant, producto, true);
    }

    private void ProcesarComandaConTicket(int cantidad, Producto prod, int idP) {

        pDetalle = new PedidoDetalle();
        int cant = cantidad;
        String fechaFormateada = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime fechaHoraActual = LocalDateTime.now();
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                fechaFormateada = fechaHoraActual.format(formato);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener la fecha y hora actual: " + e.getMessage());
        }
        pDetalle.setCantidad(cantidad);
        if (!tvTicket.getText().equals("")) {
            pDetalle.setIdPedido(idP);
        }
        pDetalle.setMesa(tvMesa.getText().toString());
        pDetalle.setIdProducto(prod.getIdProducto());
        pDetalle.setFecha(fechaFormateada);
        pDetalle.setSalon(salon);
        if (cliente != null) {
            pDetalle.setCliente(cliente);
        } else {
            pDetalle.setCliente("");
        }
        if (mesero != null) {
            pDetalle.setMesero(mesero);
        } else {
            pDetalle.setMesero("");
        }
        pDetalle.setNombre(prod.getNombre());
        pDetalle.setGrupo(prod.getGrupoPrinter());
        Boolean encontrado = false;

        if (lstPD.size() > 0) {
            for (PedidoDetalle item : lstPD) {
                if (prod.getIdProducto() == item.getIdProducto()) {
                    encontrado = true;
                    item.setCantidad(cantidad + item.getCantidad());
                    break;
                }
            }
            if (!encontrado) {
                lstPD.add(pDetalle);
            }
        } else if (lstPedidos.size() > 0) {
            lstPD.add(pDetalle);
        }
        if (lstPD.size() == 0){
            lstPD.add(pDetalle);
        }
            PedidoDetalle pedidoDetalle = new PedidoDetalle();
            //No existe un producto igual en el datgrid, hay que crearlo
            pedidoDetalle.setCocinando(true);
            pedidoDetalle.setExtras("");
            pedidoDetalle.setFecha(fechaFormateada);
            pedidoDetalle.setHoraPedido(fechaFormateada);
            pedidoDetalle.setHoraEntregado(fechaFormateada);
            //pedidoDetalle.IdCocinero = null;
            pedidoDetalle.setIdProducto(prod.getIdProducto());
            pedidoDetalle.setIdPedido(idP);
            pedidoDetalle.setCantidad(cantidad);
            pedidoDetalle.setPrecio(producto.getPrecio());
            pedidoDetalle.setSubTotal(CalcularSubTotal(cantidad, producto.getPrecio()));
            pedidoDetalle.setGrupo("0");
            pedidoDetalle.setUsuario("1");
            InsertarPedidoDetalle2(pedidoDetalle);
            Formulario = false;
    }

    private void ActualizarStockProductosIngredientes(String idProducto, int cant, Producto producto, boolean aumentar) {
        IngredienteService ingredienteService = new IngredienteService();
        ingredienteService.buscarngredientesDeProducto(idProducto, new CallBackApi<Ingrediente>() {
            @Override
            public void onResponse(Ingrediente response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {

            }

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
                progressDialog.dismiss();
            }
        });
    }

    private void ActualizarStockIngrediente(Ingrediente ingrediente) {
        IngredienteService ingredienteService = new IngredienteService();
        ingredienteService.ActualizarStockIngrediente(ingrediente, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {

                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta actualizar producto: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
            }
        });
    }

    private void ActualizarStockProducto(Producto prod) {
        ProductoService productoService = new ProductoService();
        productoService.ActualizarStockProducto(prod, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {

                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta actualizar producto: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
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
                progressDialog.dismiss();
            }
        });
    }

    private void ActualizarCliente(Pedido pd){
        PedidoService pedidoService = new PedidoService();
        pedidoService.ActualizarCliente(pd, new CallBackApi<Boolean>() {
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
                progressDialog.dismiss();
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
                progressDialog.dismiss();
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
                progressDialog.dismiss();
                Toast.makeText(ComandaGestion.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void InsertarPedidoDetalle2(PedidoDetalle pedidoDetalle) {
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
                        ObtenerProductosEnMesa2(String.valueOf(idMesa), String.valueOf(idPedido2));

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
            public void onResponseList(List<Boolean> response) {}
            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(ComandaGestion.this, "Error al insertar detalle pedido: " + errorMessage, Toast.LENGTH_SHORT).show();
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
            public void onResponseList(List<Boolean> response) {}
            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
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
                    //Vamos a actualizar el pedido dependiendo si inicio sesion un mesero
                    Pedido pedEmpleado = new Pedido();
                    //Guardar en la base de datos ActualizarMesero
                    if (usuario.getIdRol() == 2) {
                        pedEmpleado.setIdPedido(idPedido);
                        pedEmpleado.setIdMesa(idMesa);
                        pedEmpleado.setIdMesero(usuario.getIdUsuario());
                        ActualizarMesero(pedEmpleado);
                    }else{
                        //Toast.makeText(ComandaGestion.this, "Error no entro", Toast.LENGTH_SHORT).show();
                    }
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
            public void onResponseBool(Response<Boolean> response) {

            }

            @Override
            public void onResponseList(List<Integer> response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
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
                    progressDialog.dismiss();
                    Toast.makeText(ComandaGestion.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                // Si hay un error
                progressDialog.dismiss();
                Toast.makeText(ComandaGestion.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexión de red: " + t.getMessage());
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        CargarPedidosEnMesa(false);
        ObtenerProductosEnMesa(String.valueOf(idMesa), String.valueOf(idPedido));
        cambiarMesa = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            EnviarListaParcial();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void EnviarListaParcial() {
        String fechaFormateada = "";
        try {
            // Verificar la versión de Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Obtener la fecha y hora actual
                LocalDateTime fechaHoraActual = LocalDateTime.now();

                // Definir el formato deseado
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

                // Formatear la fecha y hora actual
                fechaFormateada = fechaHoraActual.format(formato);

            } else {
                System.out.println("La versión de Android no es compatible con las clases java.time.");
            }
        } catch (Exception e) {
            System.out.println("Error al obtener la fecha y hora actual: " + e.getMessage());
        }

        for (PedidoDetalle pDetalle : lstPD) {
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
            pDetalle.setFecha(formatoFecha(fechaFormateada));
        }
        Enviar();
    }

    private void Enviar(){
        enviarListaTask.enviarLista(lstPD);
        BorrarPedidosVacios();
    }
    private void EnviarListaCompleta(){
        for (PedidoDetalle pDetalle : lstPedidos) {
            pDetalle.setFecha(formatoFecha(pDetalle.getFecha()));
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
        }
        EnviarListaTask enviarListaTask2 = new EnviarListaTask(ComandaGestion.this);
        enviarListaTask2.enviarListaCompleta(lstPedidos);
    }

    // Método para convertir el formato de la fecha
    private static String formatoFecha(String fechaString) {
        String fechaFormateada = "";
        try {
            // Verificar la versión de Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Formatear la fecha y hora actual
                LocalDateTime fecha = LocalDateTime.parse(fechaString, DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"));

                // Definir el formato deseado
                DateTimeFormatter nuevoFormato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

                // Imprimir la fecha formateada
                fechaFormateada = fecha.format(nuevoFormato);
            } else {
                System.out.println("La versión de Android no es compatible con las clases java.time.");
            }
        } catch (Exception e) {
            System.out.println("Error al obtener la fecha y hora actual: " + e.getMessage());
        }
        return  fechaFormateada;
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
                }else{
                    tvMesero.setText("Mesero: ");
                }
                if(cliente!=null){
                    tvCliente.setText("Cliente: " + cliente);
                }else{
                    tvCliente.setText("Cliente: ");
                }
                if (cargarLista){
                    CargarPedidosVacios(false);
                }
            }

            @Override
            public void onResponseBool(Response<Boolean> response) {

            }

            @Override
            public void onResponseList(List<Pedido> response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
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
                        // El pedido se elimino con éxito
                    } else {
                        // Hubo un error en la eliminacion del pedido
                        //Toast.makeText(DividirPedidos.this, "Hubo un error al eliminar pedido detalle", Toast.LENGTH_SHORT).show();
                    }
                    ObtenerProductosEnMesa(String.valueOf(idMesa), String.valueOf(idPedido));
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(ComandaGestion.this, "Error en la respuesta al eliminar detallepedido: " + response.message(), Toast.LENGTH_SHORT).show();
                }
                if (lstPedidos.size() == 1)
                {
                    if(lstPedidosEnMesa != null){
                        for (Integer item: lstPedidosEnMesa) {
                            if(item.equals(idPedido)){
                                lstPedidosEnMesa.remove(item);
                                break;
                            }
                        }
                        if(lstPedidosEnMesa.size() == 1){
                            btnCuentas.setVisibility(View.GONE);
                            ObtenerProductosEnMesa(String.valueOf(idMesa), lstPedidosEnMesa.get(0).toString());
                        }else if(lstPedidosEnMesa.size() > 1){
                            ObtenerProductosEnMesa(String.valueOf(idMesa), lstPedidosEnMesa.get(0).toString());
                        }
                        EliminarPedido(idPedido);
                    }
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
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