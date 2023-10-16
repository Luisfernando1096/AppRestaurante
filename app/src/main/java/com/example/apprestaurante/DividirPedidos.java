package com.example.apprestaurante;

import static com.example.apprestaurante.ComandaGestion.lstPedidos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.apprestaurante.adapters.PedidosAdapter;
import com.example.apprestaurante.adapters.PedidosAdapterCuenta1;
import com.example.apprestaurante.adapters.PedidosAdapterCuenta2;
import com.example.apprestaurante.clases.Pedido;
import com.example.apprestaurante.clases.PedidoDetalle;

import java.util.ArrayList;
import java.util.List;

public class DividirPedidos extends AppCompatActivity {

    RecyclerView rcvPedidosActual, rcvPedidosSiguiente;
    private LinearLayoutManager layoutManagerActual, layoutManagerSiguiente;
    List<PedidoDetalle> lstPedidosActual;
    List<PedidoDetalle> lstPedidosSiguiente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dividir_pedidos);

        CargarPedidosDetalle1();
        //CargarPedidosDetalle2();
    }

    private void CargarPedidosDetalle1(){
        if(lstPedidos.size() > 0){
            // Configurando adaptador
            lstPedidosActual = new ArrayList<>(lstPedidos);
            PedidosAdapterCuenta1 pedidosAdapter = new PedidosAdapterCuenta1(lstPedidosActual);
            layoutManagerActual = new LinearLayoutManager(this);
            rcvPedidosActual = findViewById(R.id.rcvPedidos1);
            rcvPedidosActual.setAdapter(pedidosAdapter);
            rcvPedidosActual.setLayoutManager(layoutManagerActual);
            rcvPedidosActual.setHasFixedSize(true);
        }

    }

    private void CargarPedidosDetalle2(){
        if(lstPedidos.size() > 0){
            // Configurando adaptador
            PedidosAdapterCuenta2 pedidosAdapter = new PedidosAdapterCuenta2(lstPedidosSiguiente);
            layoutManagerSiguiente = new LinearLayoutManager(this);
            rcvPedidosSiguiente = findViewById(R.id.rcvPedidos2);
            rcvPedidosSiguiente.setAdapter(pedidosAdapter);
            rcvPedidosSiguiente.setLayoutManager(layoutManagerSiguiente);
            rcvPedidosSiguiente.setHasFixedSize(true);
        }

    }
}