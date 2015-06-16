package com.franciscopolov.lizart.lizart2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import com.parse.SignUpCallback;

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


public class Registro extends ActionBarActivity {

    Button btnRegistrar;
    EditText nombreUsuario;
    EditText passUsuario;
    EditText passUsuarioRepite;
    EditText emailUsuario;
    SharedPreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        preferencias=getSharedPreferences("preferenciasLizart", Context.MODE_PRIVATE);

        btnRegistrar = (Button) findViewById(R.id.registraUsu);
        nombreUsuario = (EditText) findViewById(R.id.nomUsu);
        passUsuario = (EditText) findViewById(R.id.passUsu);
        passUsuarioRepite = (EditText) findViewById(R.id.passUsu2);
        emailUsuario = (EditText) findViewById(R.id.emailUsu);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = nombreUsuario.getText().toString();
                String passUno = passUsuario.getText().toString();
                String passDos = passUsuarioRepite.getText().toString();
                String email = emailUsuario.getText().toString();
                if(passUno.equals(passDos)){
                    hebraSign hs = new hebraSign();
                    hs.execute(nombre, passUno, email);
                }
                else{
                    passUsuario.setText("");
                    passUsuarioRepite.setText("");
                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();

                }
            }
        });

        setToolbar();
    }

    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.miBarra);
        toolbar.setTitle("Registrar");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class hebraSign extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute(){
            dialog = ProgressDialog.show(Registro.this, "",
                    "Cargando. Por favor, espere...", true);
        }
        @Override
        protected String doInBackground(String... strings) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/usuario.php");
            String respuesta = null;
            try {
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("registra", "registra"));
                parametros.add(new BasicNameValuePair("nombre", strings[0]));
                parametros.add(new BasicNameValuePair("pass", strings[1]));
                parametros.add(new BasicNameValuePair("email", strings[2]));
                metodo.setEntity(new UrlEncodedFormEntity(parametros));
                HttpResponse response = cliente.execute(metodo);
                respuesta = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Toast.makeText(Registro.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(Registro.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        ParseUser user = new ParseUser();
                        user.setUsername(nombreUsuario.getText().toString());
                        user.setPassword(passUsuario.getText().toString());
                        user.setEmail(emailUsuario.getText().toString());

                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null) {
                                    Intent intent = new Intent(Registro.this, IniciaSesion.class);
                                    intent.putExtra("nombre", nombreUsuario.getText().toString());
                                    intent.putExtra("pass", passUsuario.getText().toString());
                                    startActivity(intent);
                                }
                            }
                        });

                    } else {
                        Toast.makeText(Registro.this, obj.getString("aclaracion"), Toast.LENGTH_SHORT).show();
                    }
                }
            }catch(Throwable t){
                Toast.makeText(Registro.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
