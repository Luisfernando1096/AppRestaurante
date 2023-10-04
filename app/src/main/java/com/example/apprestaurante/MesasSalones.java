package com.example.apprestaurante;

import static com.example.apprestaurante.ComandaGestion.lstPedidos;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apprestaurante.clases.Mesa;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.clases.Salon;
import com.example.apprestaurante.interfaces.MesaApi;
import com.example.apprestaurante.interfaces.PedidoDetalleApi;
import com.example.apprestaurante.interfaces.SalonApi;
import com.example.apprestaurante.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesasSalones extends AppCompatActivity {

    LinearLayout llSalones;
    GridLayout glMesas;
    TextView textMesas;
    List<Salon> lstSalones;
    List<Mesa> lstMesas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas_salones);

        llSalones = findViewById(R.id.llSalones);
        glMesas = findViewById(R.id.glMesas);
        textMesas = findViewById(R.id.textMesas);

        // Define un OnClickListener común para los botones de mesas
        View.OnClickListener mesaClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
                String tag = String.valueOf(view.getTag());
                ObtenerProductosEnMesa(tag);
            }
        };

        // Define un OnClickListener común para los botones de salones
        View.OnClickListener salonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
                BuscarMesaPorSalon(mesaClickListener, view.getTag().toString());
                textMesas.setText("Mesas : " + ((Button) view).getText().toString());
            }
        };

        BuscarSalones(salonClickListener);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(lstPedidos != null){
            lstPedidos.clear();
        }

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
                        Intent intent = new Intent(MesasSalones.this, ComandaGestion.class);
                        intent.putExtra("idMesa", Integer.parseInt(id.toString()) + 0);
                        startActivity(intent);
                    }else{
                        System.out.println("Fallo el isSuccessful");
                    }

                } catch (Exception e) {
                    Toast.makeText(MesasSalones.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PedidoDetalle>> call, Throwable t) {
                // Si hay un error
                Toast.makeText(MesasSalones.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexión de red: " + t.getMessage());
            }
        });
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
                                btnMesa.setTag(mesa.getIdMesa());
                                btnMesa.setText(mesa.getNombre());

                                if (!mesa.getDisponible())
                                {
                                    btnMesa.setBackgroundColor(Color.parseColor("#3393FF"));
                                    btnMesa.setTextColor(Color.parseColor("#FFFFFF"));
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
            }

            @Override
            public void onFailure(Call<List<Mesa>> call, Throwable t) {
                // Si hay un error
                Toast.makeText(MesasSalones.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexión de red: " + t.getMessage());
            }
        });



    }

    /*private void BuscarSalonesPorId(String id){
        Call<Salon> call = ApiClient.getClient().create(SalonApi.class).find("3");
        call.enqueue(new Callback<Salon>() {
            @Override
            public void onResponse(Call<Salon> call, Response<Salon> response) {
                //Si hay respuesta
                try {
                    if(response.isSuccessful()){
                        salones = response.body();
                        Toast.makeText(MesasSalones.this, "Nombre salon: " + salones.getNombre(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    Toast.makeText(MesasSalones.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Salon> call, Throwable t) {
                //Si hay error
                Toast.makeText(MesasSalones.this, "Error en conexion de red." + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexion de red." + t.getMessage());
            }
        });
    }*/

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
                Toast.makeText(MesasSalones.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexión de red: " + t.getMessage());
            }
        });

    }
}
