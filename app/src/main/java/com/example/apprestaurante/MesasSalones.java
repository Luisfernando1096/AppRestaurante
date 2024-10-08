package com.example.apprestaurante;

import static com.example.apprestaurante.AgregarPedidos.PermisoBorra;
import static com.example.apprestaurante.ComandaGestion.lstPedidos;
import static com.example.apprestaurante.ComandaGestion.lstPedidosEnMesa;
import static com.example.apprestaurante.ComandaGestion.salon;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.apprestaurante.clases.Mesa;
import com.example.apprestaurante.clases.Pedido;
import com.example.apprestaurante.clases.Salon;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.MesaApi;
import com.example.apprestaurante.interfaces.SalonApi;
import com.example.apprestaurante.network.ApiClient;
import com.example.apprestaurante.services.MesaService;
import com.example.apprestaurante.services.PedidoService;
import com.example.apprestaurante.utils.EnviarListaTask;
import com.example.apprestaurante.utils.Load;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesasSalones extends AppCompatActivity {

    Load progressDialog;
    LinearLayout llSalones;
    GridLayout glMesas;
    TextView textMesas;
    List<Salon> lstSalones;
    List<Mesa> lstMesas;
    public static boolean cambiarMesa = false;
    public static int idPedidoCambioMesa = 0;
    public static int idMesaAnterior = 0;
    int[] aux;
    int idMesa = 0;
    public static List<Integer> lstPedidosVacios = null;

    // Crear una instancia de EnviarListaTask y ejecutarla en segundo plano
    public static EnviarListaTask enviarListaTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas_salones);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        enviarListaTask = new EnviarListaTask(MesasSalones.this);
        progressDialog = new Load(this, "Cargando...");

        llSalones = findViewById(R.id.llSalones);
        glMesas = findViewById(R.id.glMesas);
        textMesas = findViewById(R.id.textMesas);

        //ajustar automaticamente las columnas
        // Obtener el ancho de pantalla en dp
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;

        // Establecer el número de columnas según el ancho de pantalla
        int numberOfColumns;
        if (screenWidthDp >= 600) { // Generalmente, se considera una tablet si el ancho es de 600dp o más
            numberOfColumns = 4;
        } else {
            numberOfColumns = 2;
        }

        glMesas.setColumnCount(numberOfColumns);

        // Define un OnClickListener común para los botones de mesas
        View.OnClickListener mesaClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
                aux = (int[]) (view.getTag());
                idMesa = aux[0] + 0;
                VerficaPedidos();
            }
        };
        // Define un OnClickListener común para los botones de salones
        View.OnClickListener salonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Activamos load
                progressDialog.show();
                // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
                BuscarMesaPorSalon(mesaClickListener, view.getTag().toString());
                textMesas.setText("Mesas : " + ((Button) view).getText().toString());
                salon = ((Button) view).getText().toString();
            }
        };
        BuscarSalones(salonClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            showConfirmationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void CambiarMesa(){
        if (cambiarMesa)
        {
            if(aux[0] != idMesaAnterior){
                Pedido pedido = new Pedido();
                pedido.setIdPedido(idPedidoCambioMesa);
                pedido.setIdMesa(aux[0]);

                //Aqui el codigo para cambiar de mesa
                Mesa mesa = new Mesa();
                mesa.setDisponible(false);
                mesa.setIdMesa(aux[0]);
                ActualizarEstadoMesa(mesa);
                if (lstPedidosEnMesa.size() == 1)
                {
                    //Hacer disponible la mesa anterior
                    Mesa mesa2 = new Mesa();
                    mesa2.setDisponible(true);
                    mesa2.setIdMesa(idMesaAnterior);
                    ActualizarEstadoMesa(mesa2);
                }
                ActualizarMesa(pedido, aux);
            } else{
                Intent intent = new Intent(MesasSalones.this, ComandaGestion.class);
                intent.putExtra("idMesa", aux[0] + 0);
                startActivity(intent);
            }

        } else{
            Intent intent = new Intent(MesasSalones.this, ComandaGestion.class);
            intent.putExtra("idMesa", aux[0] + 0);

            startActivity(intent);
        }
    }
    private void VerficaPedidos() {
        PedidoService pedidoService = new PedidoService();
        pedidoService.obtenerPedidosVacios(String.valueOf(idMesa), new CallBackApi<Integer>() {
            @Override
            public void onResponse(Integer response) {}
            @Override
            public void onResponseBool(Response<Boolean> response) {}
            @Override
            public void onResponseList(List<Integer> response) {
                lstPedidosVacios = response;
                PermisoBorra = false;
                if (lstPedidosVacios.size() == 0){
                    if (cambiarMesa){
                        CambiarMesa();
                    }else {
                        Intent intent = new Intent(MesasSalones.this, AgregarPedidos.class);
                        intent.putExtra("idMesa", idMesa);
                        startActivity(intent);
                    }
                }else{
                    Intent intent = new Intent(MesasSalones.this, ComandaGestion.class);
                    intent.putExtra("idMesa", idMesa);
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
            }
        });
    }

    private void ActualizarMesa(Pedido pedido, int[] tag) {
        PedidoService pedidoService = new PedidoService();
        pedidoService.ActualizarMesa(pedido, new CallBackApi<Boolean>() {
            @Override
            public void onResponse(Boolean response) {}
            @Override
            public void onResponseBool(Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(MesasSalones.this, ComandaGestion.class);
                    intent.putExtra("idMesa", tag[0] + 0);
                    startActivity(intent);
                } else {
                    // La respuesta no fue exitosa, puedes manejar el error aquí si es necesario
                    Toast.makeText(MesasSalones.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }
            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(MesasSalones.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(lstPedidos != null){
            lstPedidos.clear();
        }
        if(lstMesas != null){
            lstMesas.clear();
            glMesas.removeAllViews();
        }
    }

    private void BuscarMesaPorSalon(View.OnClickListener mesaClickListener, String idSalon) {
        Call<List<Mesa>> call = ApiClient.getClient().create(MesaApi.class).mesasPorSalon(idSalon);
        call.enqueue(new Callback<List<Mesa>>() {
            @Override
            public void onResponse(Call<List<Mesa>> call, Response<List<Mesa>> response) {
                // Si hay respuesta
                try {
                    if (response.isSuccessful()) {
                        glMesas.removeAllViews();
                        lstMesas = response.body();
                        if (lstMesas != null) {
                            for (Mesa mesa : lstMesas) {
                                Button btnMesa = new Button(MesasSalones.this);
                                int[] idNum = {mesa.getIdMesa(), mesa.getNumero()};
                                btnMesa.setTag(idNum);
                                btnMesa.setText(mesa.getNombre());

                                if (!mesa.getDisponible())
                                {
                                    //Hacer que las mesas se puedan o no seleccionar
                                    if (cambiarMesa && mesa.getIdMesa() != idMesaAnterior)
                                    {
                                        btnMesa.setEnabled(false);
                                        btnMesa.setBackgroundColor(Color.parseColor("#909CA9"));
                                        btnMesa.setTextColor(Color.parseColor("#FFFFFF"));
                                    } else{
                                        btnMesa.setBackgroundColor(Color.parseColor("#3393FF"));
                                        btnMesa.setTextColor(Color.parseColor("#FFFFFF"));
                                    }

                                    if(!cambiarMesa){
                                        btnMesa.setEnabled(true);
                                    }

                                }
                                //Obtener el total de mesas
                                int tMesas = lstMesas.size();
                                int enteroRedondeado = (int) Math.ceil(tMesas/2);
                                //Definir filas totales
                                glMesas.setRowCount(enteroRedondeado);
                                // Obtén la imagen que deseas usar desde los recursos drawable
                                @SuppressLint("UseCompatLoadingForDrawables") Drawable originalDrawable = getResources().getDrawable(R.drawable.mesa); // Reemplaza "tu_imagen" con el nombre de tu imagen en res/drawable

                                // Redimensiona la imagen al tamaño deseado
                                int width = 70; // Ancho en píxeles
                                int height = 60; // Alto en píxeles
                                Bitmap bitmap = ((BitmapDrawable) originalDrawable).getBitmap();
                                Drawable icono = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, width, height, true));

                                // Establece la posición del icono en el botón (izquierda, arriba, derecha, abajo)
                                btnMesa.setCompoundDrawablesWithIntrinsicBounds(null, icono, null, null);

                                btnMesa.setOnClickListener(mesaClickListener);

                                // Agrega el botón al GridLayout
                                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                                params.setMargins(5, 5, 5, 5); // Espacio entre los botones
                                btnMesa.setLayoutParams(params);

                                glMesas.addView(btnMesa);

                            }
                        }
                    }else{
                        System.out.println("Fallo el isSuccessful");
                    }

                } catch (Exception e) {
                    Toast.makeText(MesasSalones.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Mesa>> call, Throwable t) {
                // Si hay un error
                progressDialog.dismiss();
                Toast.makeText(MesasSalones.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexión de red: " + t.getMessage());
            }
        });



    }

    private void BuscarSalones(View.OnClickListener salonClickListener){

        Call<List<Salon>> call = ApiClient.getClient().create(SalonApi.class).findAll();
        call.enqueue(new Callback<List<Salon>>() {
            @Override
            public void onResponse(Call<List<Salon>> call, Response<List<Salon>> response) {
                // Si hay respuesta
                try {

                    if (response.isSuccessful()) {
                        lstSalones = response.body();

                        if (lstSalones != null) {
                            System.out.println("Salones no son nulos");
                            for (Salon salon : lstSalones) {
                                Button btnSalon = new Button(MesasSalones.this);
                                btnSalon.setTag(salon.getIdSalon());
                                btnSalon.setText(salon.getNombre().toString());
                                btnSalon.setOnClickListener(salonClickListener);
                                llSalones.addView(btnSalon);
                            }
                        }
                    }else{
                        System.out.println("Fallo el isSuccessful");
                    }

                } catch (Exception e) {
                    Toast.makeText(MesasSalones.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Salon>> call, Throwable t) {
                // Si hay un error
                progressDialog.dismiss();
                Toast.makeText(MesasSalones.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexión de red: " + t.getMessage());
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
                    Toast.makeText(MesasSalones.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseList(List<Boolean> response) {

            }
            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(MesasSalones.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showConfirmationDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Está seguro de que desea salir?, se cerrara la sesion");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Realiza la acción de retroceder aquí
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancela el retroceso
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
