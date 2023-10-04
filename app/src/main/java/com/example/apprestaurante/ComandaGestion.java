package com.example.apprestaurante;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apprestaurante.adapters.PedidosAdapter;
import com.example.apprestaurante.clases.Familia;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.interfaces.FamiliaApi;
import com.example.apprestaurante.interfaces.ProductoApi;
import com.example.apprestaurante.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComandaGestion extends AppCompatActivity implements  PedidosAdapter.OnItemClickListener{

    RecyclerView rcvPedidos;
    private LinearLayoutManager layoutManager;
    LinearLayout llFamilias;
    LinearLayout llProductos;
    TextView textProductos;
    List<Familia> lstFamilias;
    List<Producto> lstProductos;
    public static List<PedidoDetalle> lstPedidos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comanda_gestion);
        llFamilias = findViewById(R.id.llFamilias);
        llProductos = findViewById(R.id.llProductos);
        textProductos = findViewById(R.id.textProductos);
        rcvPedidos = findViewById(R.id.rcvPedidos);

        for (PedidoDetalle elemento : lstPedidos) {
            System.out.println(elemento.getSubTotal());
        }

        if(lstPedidos != null){
            // Configurando adaptador
            PedidosAdapter pedidosAdapter = new PedidosAdapter(lstPedidos, ComandaGestion.this);
            layoutManager = new LinearLayoutManager(this);
            rcvPedidos = findViewById(R.id.rcvPedidos);
            rcvPedidos.setAdapter(pedidosAdapter);
            rcvPedidos.setLayoutManager(layoutManager);
            rcvPedidos.setHasFixedSize(true);
        }


        //Obteniendo idMesa
        Intent intent = getIntent();
        if (intent != null) {
            int idMesa = intent.getIntExtra("idMesa", 0); // El segundo parámetro (0) es el valor predeterminado si no se encuentra "idMesa"
        }

        // Define un OnClickListener común para los botones de productos
        View.OnClickListener productoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
                String tag = String.valueOf(view.getTag());

                // Ahora puedes usar 'tag' para saber qué botón se ha presionado y realizar acciones en consecuencia.
                // Por ejemplo, puedes mostrar un mensaje con el nombre o realizar alguna otra acción.
                Toast.makeText(getApplicationContext(), "Botón de producto presionado: " + tag, Toast.LENGTH_SHORT).show();
            }
        };

        // Define un OnClickListener común para los botones de Familias
        View.OnClickListener familiaClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
                BuscarProductoPorFamilia(productoClickListener, view.getTag().toString());
                textProductos.setText("Productos : " + ((Button) view).getText().toString());
            }
        };

        BuscarFamilias(familiaClickListener);

    }

    @Override
    public void onItemClick(PedidoDetalle pedidoDetalle) {
        //Programar aqui cuando se quiere disminuir un pedido
        Toast.makeText(this, "Esta es un prueba: " + pedidoDetalle.getNombre(), Toast.LENGTH_SHORT).show();
    }

    private void BuscarProductoPorFamilia(View.OnClickListener productoClickListener, String idFamilia) {

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

                                // Establecer un ancho y alto fijo en píxeles para el botón
                                int widthInPixels = 200; // Cambia este valor según tus necesidades
                                int heightInPixels = 100; // Cambia este valor según tus necesidades

                                // Establecer el ancho y el alto del botón mediante LayoutParams
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthInPixels, heightInPixels);

                                // Aplicar los parámetros de diseño al botón
                                btnProducto.setLayoutParams(layoutParams);

                                //Obtener el total de productos

                                // Obtén la imagen que deseas usar desde los recursos drawable
                                @SuppressLint("UseCompatLoadingForDrawables") Drawable originalDrawable = getResources().getDrawable(R.drawable.productos); // Reemplaza "tu_imagen" con el nombre de tu imagen en res/drawable

                                // Redimensiona la imagen al tamaño deseado
                                int width = 200; // Ancho en píxeles
                                int height = 200; // Alto en píxeles
                                Bitmap bitmap = ((BitmapDrawable) originalDrawable).getBitmap();
                                Drawable icono = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, width, height, true));

                                // Establece la posición del icono en el botón (izquierda, arriba, derecha, abajo)
                                btnProducto.setCompoundDrawablesWithIntrinsicBounds(null, icono, null, null);

                                btnProducto.setOnClickListener(productoClickListener);

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

    private void BuscarFamilias(View.OnClickListener familiaClickListener){

        Call<List<Familia>> call = ApiClient.getClient().create(FamiliaApi.class).obtenerFamilias();
        call.enqueue(new Callback<List<Familia>>() {
            @Override
            public void onResponse(Call<List<Familia>> call, Response<List<Familia>> response) {
                // Si hay respuesta
                try {

                    if (response.isSuccessful()) {
                        lstFamilias = response.body();

                        if (lstFamilias != null) {
                            System.out.println("Familias no son nulos");
                            for (Familia familia : lstFamilias) {
                                Button btnFamilia = new Button(ComandaGestion.this);
                                btnFamilia.setTag(familia.getIdFamilia());
                                btnFamilia.setText(familia.getNombre().toString());
                                btnFamilia.setOnClickListener(familiaClickListener);
                                llFamilias.addView(btnFamilia);
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
            public void onFailure(Call<List<Familia>> call, Throwable t) {
                // Si hay un error
                Toast.makeText(ComandaGestion.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error en conexión de red: " + t.getMessage());
            }
        });

    }
}