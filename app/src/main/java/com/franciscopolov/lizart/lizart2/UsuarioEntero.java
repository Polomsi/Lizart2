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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
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


public class UsuarioEntero extends ActionBarActivity {
    Usuario usuario;
    ImageLoader imageLoader;
    ImageView foto_perfil;
    TextView nombreUsu;
    TextView descripcionUsu;
    TextView siguiendo;
    TextView seguidores;
    TextView fotografias;
    GridView gridFotos;
    Button botonUsuario;
    SharedPreferences preferencias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_entero);

        preferencias=getSharedPreferences("preferenciasLizart", Context.MODE_PRIVATE);
        int id=preferencias.getInt("id", -1);
        foto_perfil = (ImageView) findViewById(R.id.FotoPerfilUsu);
        nombreUsu = (TextView) findViewById(R.id.NombreUsu);
        descripcionUsu = (TextView) findViewById(R.id.DescripcionUsu);
        botonUsuario = (Button) findViewById(R.id.btnUsuario);
        siguiendo = (TextView) findViewById(R.id.tvNumSiguiendo);
        seguidores = (TextView) findViewById(R.id.tvNumSeguidores);
        fotografias = (TextView) findViewById(R.id.tvNumFotos);
        gridFotos = (GridView) findViewById(R.id.gridFotosUser);

        Bundle bundle = getIntent().getExtras();
        usuario = new Usuario();
        usuario.setId(bundle.getInt("id"));
        usuario.setNombre(bundle.getString("nombre"));
        usuario.setEmail(bundle.getString("email"));
        usuario.setDescripcion(bundle.getString("descripcion"));
        usuario.setFoto_perfil(bundle.getString("foto_perfil"));

        nombreUsu.setText(usuario.getNombre());
        descripcionUsu.setText(usuario.getDescripcion());

        imageLoader=ImageLoader.getInstance();
        imageLoader.displayImage(usuario.getFoto_perfil(), foto_perfil);

        if(id==usuario.getId()){
            botonUsuario.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
            botonUsuario.setText("Editar perfil");
        }
        else{
            hebraCompruebaSigue hcs = new hebraCompruebaSigue();
            hcs.execute();
            /*botonUsuario.setBackgroundColor(getResources().getColor(R.color.seguir));
            botonUsuario.setText("Seguir");*/
        }

        setToolbar();
    }

    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.miBarra);
        toolbar.setTitle("Ficha de "+usuario.getNombre());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class hebraCompruebaSigue extends AsyncTask<Void, Void, String> {
        AlertDialog dialog;
        @Override
        protected void onPreExecute(){
            dialog = ProgressDialog.show(UsuarioEntero.this, "",
                    "Cargando. Por favor, espere...", true);
        }
        @Override
        protected String doInBackground(Void... voids) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/usuario.php");
            String respuesta = null;
            Integer id = (Integer) preferencias.getInt("id", 0);
            Integer id_usu = (Integer) usuario.getId();
            try {
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("compruebaFollow", "compruebaFollow"));
                parametros.add(new BasicNameValuePair("id_usuario2", id_usu.toString()));
                parametros.add(new BasicNameValuePair("id_usuario1", id.toString()));
                metodo.setEntity(new UrlEncodedFormEntity(parametros));
                HttpResponse response = cliente.execute(metodo);
                respuesta = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Toast.makeText(UsuarioEntero.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(UsuarioEntero.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String mensaje) {
            dialog.hide();
            dialog.dismiss();
            dialog=null;
            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            try{
                JSONObject obj = new JSONObject(mensaje);

                if(obj!=null) {
                    if (obj.getInt("follow") == 1) {
                        botonUsuario.setBackgroundColor(getResources().getColor(R.color.dejar_seguir));
                        botonUsuario.setText("Dejar de seguir");

                    } else {

                        botonUsuario.setBackgroundColor(getResources().getColor(R.color.seguir));
                        botonUsuario.setText("Seguir");

                    }
                    siguiendo.setText(Integer.toString(obj.getInt("siguiendo")));
                    seguidores.setText(Integer.toString(obj.getInt("seguidores")));
                }
            }catch(Throwable t){
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
