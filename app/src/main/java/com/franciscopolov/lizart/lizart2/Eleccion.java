package com.franciscopolov.lizart.lizart2;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Eleccion extends ActionBarActivity {
    Button btnIniciaSesion;
    Button btnRegistra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eleccion);
        btnIniciaSesion = (Button) findViewById(R.id.irAInicia);
        btnRegistra = (Button) findViewById(R.id.irARegistro);

        btnIniciaSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Eleccion.this, IniciaSesion.class);
                intent.putExtra("nombre","");
                intent.putExtra("pass","");
                startActivity(intent);
            }
        });

        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Eleccion.this, Registro.class);
                startActivity(intent);
            }
        });
    }

}
