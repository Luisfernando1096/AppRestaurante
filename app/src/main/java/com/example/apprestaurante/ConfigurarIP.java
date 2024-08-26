package com.example.apprestaurante;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        editTextIP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Valida la IP después de que el texto cambie
                String ip = editable.toString();
                if (!isValidIPAddress(ip)) {
                    editTextIP.setError("Dirección IP no válida");
                } else {
                    editTextIP.setError(null); // Elimina el error si la IP es válida
                }
            }
        });

        cargarDireccionIP();

        btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> guardarDireccionIP());
    }

    // Método para validar la dirección IP
    private boolean isValidIPAddress(String ip) {
        // Patrón para una dirección IP válida
        String IPADDRESS_PATTERN =
                "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        return ip.matches(IPADDRESS_PATTERN);
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

    private void guardarDireccionIP() {
        // Obtener la dirección IP ingresada por el usuario
        String nuevaIP = editTextIP.getText().toString();

        if (isValidIPAddress(nuevaIP)) {

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