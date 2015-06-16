package com.franciscopolov.lizart.lizart2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

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
    private String pass;
    private EditText etNombreUsuario;
    private EditText etEmailNuevo;
    private EditText etDescripcion;
    private ImageView ivFoto;
    ProgressDialog dialog;
    private ImageLoader imageLoader;
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
                        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
                        parseInstallation.put("username", "");
                        ParseUser.logOut();
                        preferencias.edit().clear().commit();
                        Intent i = new Intent(Configuracion.this, IniciaSesion.class);
                        startActivity(i);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };
        etPass = (EditText) findViewById(R.id.etPassNueva1);
        etPassRep = (EditText) findViewById(R.id.etPassNueva2);
        etNombreUsuario = (EditText) findViewById(R.id.etNomUsuNuevo);
        etEmailNuevo = (EditText) findViewById(R.id.etNuevoEmail);
        etDescripcion = (EditText) findViewById(R.id.etNuevaDesc);
        ivFoto = (ImageView) findViewById(R.id.ivFotoPerfilNueva);
        Toast.makeText(getApplicationContext(), preferencias.getString("foto",null),Toast.LENGTH_SHORT).show();
        imageLoader=ImageLoader.getInstance();
        imageLoader.displayImage(preferencias.getString("foto", "none"), ivFoto);
        etNombreUsuario.setText(preferencias.getString("nomUsu", "none"));
        etEmailNuevo.setText(preferencias.getString("email", "none"));
        etDescripcion.setText(preferencias.getString("descripcion", "none"));
        btnActualizaPass = (Button) findViewById(R.id.btnActualizaPass);
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
        btnActualizaPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etPass.getText().toString().equals(etPassRep.getText().toString())){
                    pass=etPass.getText().toString();
                    hebraEditaPass hep= new hebraEditaPass();
                    hep.execute();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Ha ocurrido un error, las contraseñas no coinciden, inténtalo de nuevo",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.miBarra);
        toolbar.setTitle("Configuración");
        setSupportActionBar(toolbar);
    }
    private class hebraEditaPass extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute(){
            dialog = ProgressDialog.show(Configuracion.this, "",
                    "Cargando. Por favor, espere...", true);
        }
        @Override
        protected String doInBackground(Void... voids) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/usuario.php");
            String respuesta = null;
            Integer id = (Integer) preferencias.getInt("id", 0);
            try {
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("edita", "edita"));
                parametros.add(new BasicNameValuePair("editapass", "editapass"));
                parametros.add(new BasicNameValuePair("id", id.toString()));
                parametros.add(new BasicNameValuePair("pass", pass));
                metodo.setEntity(new UrlEncodedFormEntity(parametros));
                HttpResponse response = cliente.execute(metodo);
                respuesta = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Toast.makeText(Configuracion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(Configuracion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String mensaje) {

            dialog.hide();
            dialog.dismiss();
            dialog=null;
            try{
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
            }
        }

    }
    public boolean tieneConexion(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo!=null && netInfo.isConnectedOrConnecting()){
            return true;
        }
        else{
            return false;
        }

    }
}
