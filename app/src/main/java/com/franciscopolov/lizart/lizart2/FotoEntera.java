package com.franciscopolov.lizart.lizart2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FotoEntera extends ActionBarActivity {
    LinearLayout linearLayout;
    Button comentar;
    ImageButton like;
    ImageView imagen;
    private ImageLoader imageLoader;
    Fotografia foto;
    SharedPreferences preferencias;
    ArrayList<Comentario> comentarios;
    ListView listaComentarios;
    TextView iso;
    TextView obturacion;
    TextView apertura;
    Integer index;
    Button btnEnviarComentario;
    EditText textoComentario;
    TextView numeroLikes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_entera);
        foto=new Fotografia();
        comentarios=new ArrayList<Comentario>();
        like = (ImageButton) findViewById(R.id.imageButton);
        listaComentarios = (ListView) findViewById(R.id.listaComentarios);
        linearLayout = (LinearLayout) findViewById(R.id.linearComentarios);
        comentar = (Button) findViewById(R.id.btnComentar);
        btnEnviarComentario = (Button) findViewById(R.id.btnEnviaComentario);
        textoComentario = (EditText) findViewById(R.id.textoComentario);
        numeroLikes = (TextView) findViewById(R.id.numeroMG);
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
        hebraConsultaComentarios hcc = new hebraConsultaComentarios();
        hcc.execute();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageLoader=ImageLoader.getInstance();
        imagen = (ImageView) findViewById(R.id.imagen);
        imageLoader.displayImage(foto.getUrl(), imagen);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (like.getDrawable().getConstantState() == getResources().getDrawable(R.mipmap.ic_favorite_border_white_36dp).getConstantState()) {
                    hebraLike hl = new hebraLike();
                    hl.execute("megusta");
                } else if (like.getDrawable().getConstantState() == getResources().getDrawable(R.mipmap.ic_like_36dp).getConstantState()) {
                    hebraLike hl = new hebraLike();
                    hl.execute("nomegusta");
                }
            }
        });
        listaComentarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), comentarios.get(i).getId_usuario() + " Me voy al perfil de " + comentarios.get(i).getNom_usu(), Toast.LENGTH_SHORT).show();
            }
        });
        listaComentarios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean usuario = false;
                if (preferencias.getInt("id", 0) == comentarios.get(i).getId_usuario().intValue()) {
                    usuario = true;
                    index = i;
                    //Toast.makeText(getApplicationContext(), "Si quieres elimino", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(FotoEntera.this);
                    builder.setTitle("Confirmación");
                    builder.setMessage("¿Estás seguro de que deseas eliminar el comentario?");
                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String ii = String.valueOf(i);
                            hebraEliminaComentario hec = new hebraEliminaComentario();
                            hec.execute(comentarios.get(index).getFecha(), index.toString());
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                } else {

                }
                return usuario;
            }
        });
        comentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayout.getVisibility() == View.INVISIBLE) {
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        btnEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comentarioFinal = textoComentario.getText().toString();
                hebraComentarFoto hcf = new hebraComentarFoto();
                hcf.execute(comentarioFinal);
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textoComentario.getWindowToken(), 0);
                textoComentario.setText("");
            }
        });
        iso = (TextView) findViewById(R.id.iso);
        obturacion = (TextView) findViewById(R.id.obturacion);
        apertura = (TextView) findViewById(R.id.apertura);
        iso.setText(foto.getISO());
        obturacion.setText(foto.getVel_obturacion());
        apertura.setText(foto.getApertura());


    }

    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.miBarra);
        toolbar.setTitle(foto.getTitulo());
        setSupportActionBar(toolbar);
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
            Integer id_foto = (Integer) foto.getId();
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
            try{
                JSONObject obj = new JSONObject(mensaje);

                if(obj!=null) {
                    if (obj.getInt("like") == 0) {
                        like.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_border_white_36dp));
                        numeroLikes.setText(obj.getInt("numero")+ " me gusta");

                    } else {

                        like.setImageDrawable(getResources().getDrawable(R.mipmap.ic_like_36dp));
                        numeroLikes.setText(obj.getInt("numero") + " me gusta");

                    }
                }
            }catch(Throwable t){
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
    private class hebraLike extends AsyncTask<String, Void, String> {
        //AlertDialog dialog;
        @Override
        protected void onPreExecute(){
            /*dialog = ProgressDialog.show(FotoEntera.this, "",
                    "Cargando. Por favor, espere...", true);*/
        }
        @Override
        protected String doInBackground(String... strings) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/fotografias.php");
            String respuesta = null;
            Integer id = (Integer) preferencias.getInt("id", 0);
            Integer id_foto = (Integer) foto.getId();
            try {
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("like", "like"));
                if(strings[0].equals("megusta")){
                    parametros.add(new BasicNameValuePair("megusta", "megusta"));
                }
                else if(strings[0].equals("nomegusta")){
                    parametros.add(new BasicNameValuePair("nomegusta", "nomegusta"));
                }
                parametros.add(new BasicNameValuePair("id_foto", id_foto.toString()));
                parametros.add(new BasicNameValuePair("id_usuario", id.toString()));
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
            try{
                JSONObject obj = new JSONObject(mensaje);

                if(obj!=null) {
                    if (obj.getInt("error") == 0) {
                        if(like.getDrawable().getConstantState()==getResources().getDrawable(R.mipmap.ic_favorite_border_white_36dp).getConstantState()){
                            like.setImageDrawable(getResources().getDrawable(R.mipmap.ic_like_36dp));
                            numeroLikes.setText(obj.getInt("numero") + " me gusta");
                        }
                        else if(like.getDrawable().getConstantState()==getResources().getDrawable(R.mipmap.ic_like_36dp).getConstantState()) {
                            like.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_border_white_36dp));
                            numeroLikes.setText(obj.getInt("numero")+ " me gusta");
                        }

                    } else {

                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();

                    }
                }
            }catch(Throwable t){
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
    private class hebraConsultaComentarios extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... strings) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/fotografias.php");
            String respuesta = null;
            Integer id_foto = (Integer) foto.getId();
            try {
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("consultaComentarios", "consultaComentarios"));
                parametros.add(new BasicNameValuePair("id_foto", id_foto.toString()));
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
            try{
                JSONArray array = new JSONArray(mensaje);
                if(array!=null) {
                    for (int i = 0; i < array.length(); i++) {
                        Comentario com = new Comentario();
                        com.setId_fotografia(array.getJSONObject(i).getInt("id_fotografia"));
                        com.setId_usuario(array.getJSONObject(i).getInt("id_usuario"));
                        com.setFecha(array.getJSONObject(i).getString("fecha"));
                        com.setComent(array.getJSONObject(i).getString("comentario"));
                        com.setNom_usu(array.getJSONObject(i).getString("nom_usuario"));
                        com.setUrl_fotoUsu(array.getJSONObject(i).getString("foto_perfil"));
                        comentarios.add(com);

                    }

                    AdaptadorComentarios ac = new AdaptadorComentarios(getApplicationContext(), comentarios);
                    listaComentarios.setAdapter(ac);
                    setListViewHeightBasedOnChildren(listaComentarios);
                }
                else{

                }
            }catch(Throwable t){
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    private class hebraEliminaComentario extends AsyncTask<String, Void, String> {
        Integer i;
        @Override
        protected String doInBackground(String... strings) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/fotografias.php");
            String respuesta = null;
            Integer id_usuario = (Integer) preferencias.getInt("id", 0);
            Integer id_foto = (Integer) foto.getId();
            String fecha = strings[0];
            i = Integer.valueOf(strings[1]);
            try {
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("eliminaComentarios", "eliminaComentarios"));
                parametros.add(new BasicNameValuePair("id_foto", id_foto.toString()));
                parametros.add(new BasicNameValuePair("id_usuario", id_usuario.toString()));
                parametros.add(new BasicNameValuePair("fecha", fecha));
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
            try{
                //comentarios.remove(i);
                comentarios.clear();
                //listaComentarios.invalidateViews();
                hebraConsultaComentarios hcc = new hebraConsultaComentarios();
                hcc.execute();
                AdaptadorComentarios ac = new AdaptadorComentarios(getApplicationContext(), comentarios);
                listaComentarios.setAdapter(ac);
                setListViewHeightBasedOnChildren(listaComentarios);

            }catch(Throwable t){
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
    private class hebraComentarFoto extends AsyncTask<String, Void, String> {
        Integer i;
        @Override
        protected String doInBackground(String... strings) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/fotografias.php");
            String respuesta = null;
            Integer id_usuario = (Integer) preferencias.getInt("id", 0);
            Integer id_foto = (Integer) foto.getId();
            String comentario = strings[0];
            //i = Integer.valueOf(strings[1]);
            try {
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("comentar", "comentar"));
                parametros.add(new BasicNameValuePair("id_foto", id_foto.toString()));
                parametros.add(new BasicNameValuePair("id_usuario", id_usuario.toString()));
                parametros.add(new BasicNameValuePair("comentario", comentario));
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
            try{
                //comentarios.remove(i);
                comentarios.clear();
                //listaComentarios.invalidateViews();
                hebraConsultaComentarios hcc = new hebraConsultaComentarios();
                hcc.execute();
                AdaptadorComentarios ac = new AdaptadorComentarios(getApplicationContext(), comentarios);
                listaComentarios.setAdapter(ac);
                setListViewHeightBasedOnChildren(listaComentarios);

            }catch(Throwable t){
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
