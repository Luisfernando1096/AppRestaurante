package com.example.apprestaurante;

import static com.example.apprestaurante.MainActivity.usuario;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apprestaurante.clases.Mesa;
import com.example.apprestaurante.clases.Pedido;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.services.MesaService;
import com.example.apprestaurante.services.PedidoService;
import com.example.apprestaurante.utils.Load;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Response;

public class AgregarPedidos extends AppCompatActivity {
    Load progressDialog;
    int idMesa = 0;
    int idPedido = 0;
    private Button btnCuentaUnica, btnVariasCuentas;
    ComandaGestion comanda = new ComandaGestion();
    Pedido nuevoPedido = null;
    public static Boolean cuentaUnica = false;
    public static boolean Formulario = false;
    public static boolean PermisoLista = false;
    public static boolean PermisoBorra = false;
    public static List<Integer> lstPedidosVacios = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new Load(AgregarPedidos.this, "Cargando...");
        setContentView(R.layout.activity_agregar_pedidos);
        btnCuentaUnica = findViewById(R.id.btnCuentaUnica);
        btnVariasCuentas = findViewById(R.id.btnVariasCuentas);

        Intent intent = getIntent();
        if (intent != null) {
            idMesa = intent.getIntExtra("idMesa", 0); // El segundo parámetro (0) es el valor predeterminado si no se encuentra "idMesa"
        }
        btnCuentaUnica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Seleccionar(true);
            }
        });

        btnVariasCuentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Seleccionar(false);
            }
        });
    }

    private void Seleccionar(boolean seleccion) {
        Intent intent = new Intent(AgregarPedidos.this, ComandaGestion.class);
        intent.putExtra("idMesa", idMesa);
        if (seleccion) {
            startActivity(intent);
            Formulario = false;
            cuentaUnica = true;
            finish();
        } else {
            MostrarDialog();
        }
    }

    private void MostrarDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AgregarPedidos.this);
        builder.setTitle("Ingrese la cantidad");
        final EditText editText = new EditText(AgregarPedidos.this);
        editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(editText);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                String cantidadString = editText.getText().toString();
                if (cantidadString != null && !cantidadString.isEmpty()){
                    int cantidad = Integer.parseInt(cantidadString);
                    Boolean estado = false;
                    if (cantidad  > 1 && cantidadString != null && !cantidadString.isEmpty()) {
                        for (int i = 0; i < cantidad; i++) {
                            estado = true;
                            nuevoPedido = new Pedido();
                            Pedido pedido = new Pedido();
                            pedido.setIdMesa(idMesa);
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
                            //Creamos el pedido
                            InsertarPedido(pedido, fechaFormateada);
                        }
                        if (estado){
                            //Actualizar estado de la mesa
                            Mesa mesa = new Mesa();
                            mesa.setIdMesa(idMesa);
                            mesa.setDisponible(false);
                            ActualizarEstadoMesa(mesa);
                        }
                    }
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void InsertarPedido(Pedido pedido, String fecha) {
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
                        comanda.ActualizarMesero(pedEmpleado);
                    }
                }else {
                    Toast.makeText(AgregarPedidos.this, "Hubo un error al insertar pedido", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onResponseBool(Response<Boolean> response) {}
            @Override
            public void onResponseList(List<Integer> response) {}
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AgregarPedidos.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ActualizarEstadoMesa(Mesa mesa) {
        MesaService mesaService = new MesaService();
        mesaService.ActualizarEstadoMesa(mesa, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {}
            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {
                    BuscarIdPedido();
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(AgregarPedidos.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onResponseList(List<Boolean> response) {}
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AgregarPedidos.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void BuscarIdPedido(){
        PedidoService pedidoService = new PedidoService();
        pedidoService.obtenerPedidosVacios(String.valueOf(idMesa), new CallBackApi<Integer>() {
            @Override
            public void onResponse(Integer response) {}
            @Override
            public void onResponseBool(Response<Boolean> response) {}
            @Override
            public void onResponseList(List<Integer> response) {
                lstPedidosVacios = response;
                if (lstPedidosVacios.size()  > 0){
                    idPedido = lstPedidosVacios.get(0);
                    progressDialog.show();
                    Intent intentComandaGestion = new Intent(AgregarPedidos.this, ComandaGestion.class);
                    intentComandaGestion.putExtra("idMesa", idMesa);
                    intentComandaGestion.putExtra("idPedido", idPedido);
                    Formulario = true;
                    PermisoLista = true;
                    PermisoBorra = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            startActivity(intentComandaGestion);
                            finish();
                        }
                    }, 400);
                }
            }
            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }
}