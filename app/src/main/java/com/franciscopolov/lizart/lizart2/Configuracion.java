package com.franciscopolov.lizart.lizart2;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Configuracion  extends ActionBarActivity{

    /*private String nomUsuario;
    private String password;
    private Button btnIniciaSesion;
    private EditText etNombre;*/
    private Button btnCierraSesion;
    private EditText etPass;
    private EditText etPassRep;
    private Button btnActualizaPass;
    private SharedPreferences preferencias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        setToolbar();
        preferencias=getSharedPreferences("preferenciasLizart", Context.MODE_PRIVATE);
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        preferencias.edit().clear().commit();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };
        btnCierraSesion = (Button) findViewById(R.id.btnCerrarSesion);
        btnCierraSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Configuracion.this);
                builder.setTitle("Confirmación");
                builder.setMessage("¿Está seguro de que desea cerrar la sesión actual?");
                builder.setPositiveButton("Sí", dialogClickListener);
                builder.setNegativeButton("No", dialogClickListener);
                builder.show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.miBarra);
        toolbar.setTitle("Configuración");
        setSupportActionBar(toolbar);
    }

}
