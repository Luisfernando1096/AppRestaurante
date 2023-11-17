package com.example.apprestaurante;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apprestaurante.clases.Usuario;
import com.example.apprestaurante.interfaces.UsuarioApi;
import com.example.apprestaurante.network.ApiClient;
import com.example.apprestaurante.utils.Load;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.apprestaurante.MesasSalones.cambiarMesa;

public class MainActivity extends AppCompatActivity {

    Load progressDialog;
    Button btnIngresar;
    EditText edtPin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new Load(this);

        btnIngresar = findViewById(R.id.btnIngresar);
        edtPin = findViewById(R.id.edtPin);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IniciarSesion();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        edtPin.setText("");
        cambiarMesa = false;
    }

    private void IniciarSesion(){
        String pin = edtPin.getText().toString();
        if(!pin.equals("")){
            progressDialog.show();
            Call<Usuario> call = ApiClient.getClient().create(UsuarioApi.class).ObtenerDatosUsuario(pin);
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    //Si hay respuesta
                    try {
                        if (response.isSuccessful()) {
                            Usuario usuario = response.body();
                            if(usuario != null){
                                if(usuario.getIdUsuario() == 1 || usuario.getIdUsuario() == 3){
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
                }
            });
        }else{
            Toast.makeText(this, "Introduzca el pin", Toast.LENGTH_SHORT).show();
        }
    }

}