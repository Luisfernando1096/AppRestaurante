package com.example.apprestaurante.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.apprestaurante.R;

public class Load {

    private AlertDialog dialog;

    public Load(Context context, String texto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_progress, null);
        TextView tvTexto = view.findViewById(R.id.tvTexto);
        tvTexto.setText(texto);
        builder.setView(view);
        builder.setCancelable(false); // Evitar que el usuario cierre el diálogo al tocar fuera de él

        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
