package com.example.apprestaurante;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apprestaurante.clases.Cliente;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.services.ClienteService;
import com.example.apprestaurante.utils.Load;

import java.util.List;

import retrofit2.Response;

public class AgregarCliente extends AppCompatActivity {
    Button btnGuardar;
    Load progressDialog;
    EditText edtNombre, edtDireccion, edtEmail, edtTelefono, edtNit, edtregContable;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cliente);

        progressDialog = new Load(this, "Insertando...");

        btnGuardar = findViewById(R.id.btnGuardar);
        edtNombre = findViewById(R.id.edtNombre);
        edtDireccion = findViewById(R.id.edtDireccion);
        edtEmail = findViewById(R.id.edtEmail);
        edtTelefono = findViewById(R.id.edtTelefono);
        edtNit = findViewById(R.id.edtNit);
        edtregContable = findViewById(R.id.edtregContable);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cliente cliente = new Cliente();
                cliente.setNombre(edtNombre.getText().toString());
                cliente.setDireccion(edtDireccion.getText().toString());
                cliente.setEmail(edtEmail.getText().toString());
                cliente.setTelefono(edtTelefono.getText().toString());
                cliente.setNIT(edtNit.getText().toString());
                cliente.setRegContable(edtregContable.getText().toString());

                InsertarCliente(cliente);
            }
        });
    }

    private void InsertarCliente(Cliente cliente){
        progressDialog.show();
        ClienteService clienteService = new ClienteService();
        clienteService.InsertarCliente(cliente, new CallBackApi<Boolean>() {
            @Override
            public void onResponseBool(Response<Boolean> response) {
                if(response.body()){
                    progressDialog.dismiss();
                    Toast.makeText(AgregarCliente.this, "Cliente agregado con exito", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
            }
        });
    }
}
