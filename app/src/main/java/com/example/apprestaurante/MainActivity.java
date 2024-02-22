package com.example.apprestaurante;

import static com.example.apprestaurante.MesasSalones.cambiarMesa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.apprestaurante.clases.Usuario;
import com.example.apprestaurante.interfaces.UsuarioApi;
import com.example.apprestaurante.network.ApiClient;
import com.example.apprestaurante.utils.Load;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    public static Usuario usuario;
    public String direccionIP = "";
    Load progressDialog;
    Button btnIngresar;
    EditText edtPin;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("miPreferences", Context.MODE_PRIVATE);
        progressDialog = new Load(this, "Por favor espere...");
        btnIngresar = findViewById(R.id.btnIngresar);
        edtPin = findViewById(R.id.edtPin);
        imageView = findViewById(R.id.imageView);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IniciarSesion();
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(MainActivity.this, ConfigurarIP.class));
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        edtPin.setText("");
        cambiarMesa = false;
        cargarDireccionIP();
    }

    private void IniciarSesion(){
        String pin = edtPin.getText().toString();
        if (direccionIP.equals("")){
            Toast.makeText(this, "Debe definir una direccion IP", Toast.LENGTH_SHORT).show();
        }else{
            if(!pin.equals("")){
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
                                    if(usuario.getIdRol() == 1 || usuario.getIdRol() == 2){
                                        startActivity(new Intent(MainActivity.this, MesasSalones.class));
                                    } else{
                                        Toast.makeText(MainActivity.this, "Solo puede acceder un mesero o administrador.", Toast.LENGTH_SHORT).show();
                                    }
                                } else{
                                    Toast.makeText(MainActivity.this, "El pin no es valido.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        //Si hay error
                        Toast.makeText(MainActivity.this, "Error en conexion de red." + t.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("Error en conexion de red." + t.getMessage());
                        progressDialog.dismiss();
                    }
                });
            }else{
                Toast.makeText(this, "Introduzca el pin", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarDireccionIP() {
        direccionIP = sharedPreferences.getString("claveIP", "");
        ApiClient.ipAddress = direccionIP;
    }
}