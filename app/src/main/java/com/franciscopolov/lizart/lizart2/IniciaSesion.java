package com.franciscopolov.lizart.lizart2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class IniciaSesion extends ActionBarActivity {
    private String nomUsuario;
    private String password;
    private Button btnIniciaSesion;
    private EditText etNombre;
    private EditText etPass;
    private SharedPreferences preferencias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicia_sesion);
        setToolbar();
        preferencias=getSharedPreferences("preferenciasLizart", Context.MODE_PRIVATE);
        if((preferencias.getInt("id",-1))!=-1){
            Intent intent = new Intent(IniciaSesion.this, Lizart.class);
            startActivity(intent);
        }
            btnIniciaSesion = (Button) findViewById(R.id.iniciasesion);
            etNombre = (EditText) findViewById(R.id.nomUsu);
            etPass = (EditText) findViewById(R.id.passUsu);

        Bundle extras = getIntent().getExtras();
        etNombre.setText(extras.getString("nombre"));
        etPass.setText(extras.getString("pass"));
            btnIniciaSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tieneConexion()) {
                        nomUsuario = etNombre.getText().toString();
                        password = etPass.getText().toString();
                        hebraLogin logearse = new hebraLogin();
                        logearse.execute();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(IniciaSesion.this);
                        builder.setTitle("Importante");
                        builder.setMessage("El dispositivo no tiene conexión a internet");
                        builder.setPositiveButton("OK", null);
                        builder.create();
                        builder.show();
                    }
                }
            });
    }

    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.miBarra);
        toolbar.setTitle("Inicio de sesión");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inicia_sesion, menu);
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
    }*/
    private class hebraLogin extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute(){
            dialog = ProgressDialog.show(IniciaSesion.this, "",
                    "Cargando. Por favor, espere...", true);
        }
        @Override
        protected String doInBackground(Void... voids) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/usuario.php");
            String respuesta = null;
            try {
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("inicia", "inicia"));
                parametros.add(new BasicNameValuePair("nombre", nomUsuario));
                parametros.add(new BasicNameValuePair("pass", password));
                metodo.setEntity(new UrlEncodedFormEntity(parametros));
                HttpResponse response = cliente.execute(metodo);
                respuesta = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Toast.makeText(IniciaSesion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(IniciaSesion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String mensaje) {

            dialog.hide();
            dialog.dismiss();
            try{
                JSONObject obj = new JSONObject(mensaje);

                if(obj!=null) {
                    if (obj.getInt("error") == 0) {
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putInt("id", obj.getInt("ID"));
                        editor.putString("nomUsu", obj.getString("nom_usuario"));
                        editor.putString("password", obj.getString("password"));
                        editor.putString("email", obj.getString("email"));
                        editor.putString("descripcion", obj.getString("descripcion"));
                        editor.putString("foto", obj.getString("foto_perfil"));
                        editor.commit();
                        ParseUser.logInInBackground(obj.getString("nom_usuario"), obj.getString("password"), new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (parseUser != null) {
                                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                    installation.put("Username", parseUser.getUsername());
                                    installation.saveInBackground();
                                    Intent intent = new Intent(IniciaSesion.this, Lizart.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(IniciaSesion.this, obj.getString("aclaracion"), Toast.LENGTH_SHORT).show();
                    }
                }
            }catch(Throwable t){
                Toast.makeText(IniciaSesion.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if((preferencias.getInt("id",-1))!=-1){
            Intent intent = new Intent(IniciaSesion.this, Lizart.class);
            startActivity(intent);
        }
    }

}
