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
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.interfaces.FamiliaApi;
import com.example.apprestaurante.interfaces.PedidoDetalleApi;
import com.example.apprestaurante.interfaces.ProductoApi;
import com.example.apprestaurante.network.ApiClient;
import com.example.apprestaurante.utils.Calculos;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComandaGestion extends AppCompatActivity implements  PedidosAdapter.OnItemClickListener{

    Calculos calcular = new Calculos();
    RecyclerView rcvPedidos;
    private LinearLayoutManager layoutManager;
    LinearLayout llFamilias;
    LinearLayout llProductos;
    TextView textProductos;
    List<Familia> lstFamilias;
    List<Producto> lstProductos;
    Producto producto;
    public static List<PedidoDetalle> lstPedidos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comanda_gestion);
        llFamilias = findViewById(R.id.llFamilias);
        llProductos = findViewById(R.id.llProductos);
        textProductos = findViewById(R.id.textProductos);
        rcvPedidos = findViewById(R.id.rcvPedidos);

        //Obteniendo idMesa
        Intent intent = getIntent();
        if (intent != null) {
            int idMesa = intent.getIntExtra("idMesa", 0); // El segundo parámetro (0) es el valor predeterminado si no se encuentra "idMesa"
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

    private void ObtenerProductosEnMesa(String id){
        Call<List<PedidoDetalle>> call = ApiClient.getClient().create(PedidoDetalleApi.class).productosEnMesa(id);
        call.enqueue(new Callback<List<PedidoDetalle>>() {
            @Override
            public void onResponse(Call<List<PedidoDetalle>> call, Response<List<PedidoDetalle>> response) {
                // Si hay respuesta
                try {

                    if (response.isSuccessful()) {
                        lstPedidos = response.body();
                        CargarPedidos();
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
        if(lstPedidos != null){
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

    private void BuscarProductoPorId(String id, int cantidad){
        if(cantidad>0){
            Call<Producto> call = ApiClient.getClient().create(ProductoApi.class).productoPorId(id);
            call.enqueue(new Callback<Producto>() {
                double subTotal = 0;
                @Override
                public void onResponse(Call<Producto> call, Response<Producto> response) {
                    // Si hay respuesta
                    try {

                        if (response.isSuccessful()) {
                            producto = response.body();
                            subTotal = calcular.CalcularSubTotal(cantidad, producto.getPrecio());
                            Toast.makeText(ComandaGestion.this, "SubTotal: " + subTotal, Toast.LENGTH_SHORT).show();
                        }else{
                            System.out.println("Fallo el isSuccessful");
                        }

                    } catch (Exception e) {
                        Toast.makeText(ComandaGestion.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Producto> call, Throwable t) {
                    // Si hay un error
                    Toast.makeText(ComandaGestion.this, "Error en conexión de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Error en conexión de red: " + t.getMessage());
                }
            });
        } else{
            Toast.makeText(this, "No ingreso una cantidad valida", Toast.LENGTH_SHORT).show();
        }

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