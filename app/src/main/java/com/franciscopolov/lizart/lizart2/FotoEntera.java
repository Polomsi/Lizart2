package com.franciscopolov.lizart.lizart2;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;


public class FotoEntera extends ActionBarActivity {
    ImageView imagen;
    private ImageLoader imageLoader;
    private Fotografia foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_entera);
        foto=new Fotografia();
        Bundle bundle = getIntent().getExtras();

        foto.setId(bundle.getInt("id"));
        foto.setId_usuario(bundle.getInt("id_usuario"));
        foto.setTitulo(bundle.getString("titulo"));
        foto.setISO(bundle.getString("ISO"));
        foto.setVel_obturacion(bundle.getString("vel_obturacion"));
        foto.setApertura(bundle.getString("apertura"));
        foto.setFecha(bundle.getString("fecha"));
        foto.setUrl(bundle.getString("url"));

        imageLoader=ImageLoader.getInstance();
        imagen = (ImageView) findViewById(R.id.imagen);
        imageLoader.displayImage(foto.getUrl(),imagen);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_foto_entera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
