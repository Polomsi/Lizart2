package com.franciscopolov.lizart.lizart2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FotoEntera extends ActionBarActivity {
    ImageView imagen;
    private ImageLoader imageLoader;
    private Fotografia foto;
    SharedPreferences preferencias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_entera);
        foto=new Fotografia();
        Bundle bundle = getIntent().getExtras();
        preferencias=getSharedPreferences("preferenciasLizart", Context.MODE_PRIVATE);
        foto.setId(bundle.getInt("id"));
        foto.setId_usuario(bundle.getInt("id_usuario"));
        foto.setTitulo(bundle.getString("titulo"));
        foto.setISO(bundle.getString("ISO"));
        foto.setVel_obturacion(bundle.getString("vel_obturacion"));
        foto.setApertura(bundle.getString("apertura"));
        foto.setFecha(bundle.getString("fecha"));
        foto.setUrl(bundle.getString("url"));
        setToolbar();
        hebraCompruebaLike hcl = new hebraCompruebaLike();
        hcl.execute();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageLoader=ImageLoader.getInstance();
        imagen = (ImageView) findViewById(R.id.imagen);
        imageLoader.displayImage(foto.getUrl(),imagen);

    }



    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.miBarra);
        toolbar.setTitle(foto.getTitulo());
        setSupportActionBar(toolbar);
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
    private class hebraCompruebaLike extends AsyncTask<Void, Void, String> {
        AlertDialog dialog;
        @Override
        protected void onPreExecute(){
            dialog = ProgressDialog.show(FotoEntera.this, "",
                    "Cargando. Por favor, espere...", true);
        }
        @Override
        protected String doInBackground(Void... voids) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/fotografias.php");
            String respuesta = null;
            Integer id = (Integer) preferencias.getInt("id", 0);
            Integer id_foto = foto.getId();
            try {
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("consultaLike", "consultaLike"));
                parametros.add(new BasicNameValuePair("id_foto", id_foto.toString()));
                parametros.add(new BasicNameValuePair("id_usu", id.toString()));
                metodo.setEntity(new UrlEncodedFormEntity(parametros));
                HttpResponse response = cliente.execute(metodo);
                respuesta = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Toast.makeText(FotoEntera.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(FotoEntera.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String mensaje) {

            dialog.hide();
            dialog.dismiss();
            dialog=null;
            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
            /*try{
                JSONObject obj = new JSONObject(mensaje);

                if(obj!=null) {
                    if (obj.getInt("error") == 0) {
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putString("password", pass);
                        editor.commit();
                    } else {
                        Toast.makeText(Configuracion.this, obj.getString("aclaracion"), Toast.LENGTH_SHORT).show();
                    }
                }
            }catch(Throwable t){
                Toast.makeText(Configuracion.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }*/
        }

    }
}
