package com.example.apprestaurante;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurarIP extends AppCompatActivity {
    private EditText editTextIP;
    private SharedPreferences sharedPreferences;
    private Button btnGuardar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configurar_ip);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("miPreferences", Context.MODE_PRIVATE);

        editTextIP = findViewById(R.id.IP);
        cargarDireccionIP();

        btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> guardarDireccionIP());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void cargarDireccionIP() {
        String direccionIP = sharedPreferences.getString("claveIP", "");
        editTextIP.setText(direccionIP);
    }

    private boolean esDireccionIPValida(String ip) {
        // Patrón para validar una dirección IP
        String patronIP = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$";

        // Compilar el patrón en una expresión regular
        Pattern pattern = Pattern.compile(patronIP);

        // Crear un objeto Matcher para la cadena proporcionada
        Matcher matcher = pattern.matcher(ip);

        // Verificar si la cadena coincide con el patrón
        return matcher.matches();
    }

    private void guardarDireccionIP() {
        // Obtener la dirección IP ingresada por el usuario
        String nuevaIP = editTextIP.getText().toString();

        if (esDireccionIPValida(nuevaIP)) {

            // Guardar la dirección IP en SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("claveIP", nuevaIP);
            editor.apply();

            Toast.makeText(this, "IP Guardada con éxito: " + nuevaIP, Toast.LENGTH_SHORT).show();

            finish();
        } else {
            Toast.makeText(this, "Dirección IP no válida. Por favor, ingrese una dirección IP válida.", Toast.LENGTH_SHORT).show();
        }
    }
}