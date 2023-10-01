package com.example.apprestaurante;

import android.annotation.SuppressLint;
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

public class MesasSalones extends AppCompatActivity {

    LinearLayout llSalones;
    GridLayout glMesas;
    TextView textMesas;
    String[] nombresDeSalones = {"VIP", "Terraza", "Principal", "Salon 1", "Salon 2"};
    int[] tagsSalones = {1, 2, 3, 4, 5};
    String[] nombresDeMesas = {"Mesa 1", "Mesa 3", "Mesa 4", "Mesa 5", "Mesa 6", "Mesa 7"};
    int[] tagsMesas = {1, 2, 3, 4, 5, 6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas_salones);

        llSalones = findViewById(R.id.llSalones);
        glMesas = findViewById(R.id.glMesas);
        textMesas = findViewById(R.id.textMesas);

        // Define un OnClickListener común para los botones de salones
        View.OnClickListener salonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
                String tag = String.valueOf(view.getTag());
                textMesas.setText("Mesas : " + ((Button) view).getText().toString());
                // Ahora puedes usar 'tag' para saber qué botón se ha presionado y realizar acciones en consecuencia.
                // Por ejemplo, puedes mostrar un mensaje con el nombre o realizar alguna otra acción.
                Toast.makeText(getApplicationContext(), "Botón de salón presionado: " + tag, Toast.LENGTH_SHORT).show();
            }
        };

        // Define un OnClickListener común para los botones de mesas
        View.OnClickListener mesaClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí obtienes la etiqueta (Tag) del botón que se ha presionado.
                String tag = String.valueOf(view.getTag());

                // Ahora puedes usar 'tag' para saber qué botón se ha presionado y realizar acciones en consecuencia.
                // Por ejemplo, puedes mostrar un mensaje con el nombre o realizar alguna otra acción.
                Toast.makeText(getApplicationContext(), "Botón de mesa presionado: " + tag, Toast.LENGTH_SHORT).show();
            }
        };

        // Configura los botones de salones
        int i = 0;
        for (String nombre : nombresDeSalones) {
            Button btnSalon = new Button(this);
            btnSalon.setTag(tagsSalones[i]);
            i++;
            btnSalon.setText(nombre);
            btnSalon.setOnClickListener(salonClickListener);
            llSalones.addView(btnSalon);
        }

        int j = 0;

        for (String nombre : nombresDeMesas) {
            Button btnMesa = new Button(this);
            btnMesa.setTag(tagsMesas[j]);

            //Obtener el total de mesas
            int tMesas = 3;
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

            btnMesa.setText(nombre);
            btnMesa.setOnClickListener(mesaClickListener);

            // Agrega el botón al GridLayout
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(5, 5, 5, 5); // Espacio entre los botones
            btnMesa.setLayoutParams(params);

            glMesas.addView(btnMesa);

            j++;
        }
    }
}
