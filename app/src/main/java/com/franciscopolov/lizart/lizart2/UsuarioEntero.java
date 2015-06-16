package com.franciscopolov.lizart.lizart2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import org.json.JSONArray;
import org.json.JSONException;
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
    ArrayList<Fotografia> array_imagenes;
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
        array_imagenes=new ArrayList<>();

        nombreUsu.setText(usuario.getNombre());
        descripcionUsu.setText(usuario.getDescripcion());

        imageLoader=ImageLoader.getInstance();
        imageLoader.displayImage(usuario.getFoto_perfil(), foto_perfil);

        hebraFotosUsuario hebra = new hebraFotosUsuario();
        hebra.execute();

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

        botonUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id=preferencias.getInt("id", -1);
                if(id==usuario.getId()){
                    Intent intent = new Intent(UsuarioEntero.this, Configuracion.class);
                    startActivity(intent);
                }
                else{
                    if(botonUsuario.getText().equals("Seguir")){
                        //Toast.makeText(getApplicationContext(), "Quiero seguirte", Toast.LENGTH_SHORT).show();
                        hebraFollow hf = new hebraFollow();
                        hf.execute("follow");
                    }
                    else{
                        //Toast.makeText(getApplicationContext(), "Ya no quiero seguirte", Toast.LENGTH_SHORT).show();
                        hebraFollow hf = new hebraFollow();
                        hf.execute("unfollow");
                    }
                }
            }
        });

        gridFotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gridFotos.getAdapter().getItem(i);
                Intent intent = new Intent(getApplicationContext(), FotoEntera.class);
                intent.putExtra("id", array_imagenes.get(i).getId());
                intent.putExtra("ISO", array_imagenes.get(i).getISO());
                intent.putExtra("vel_obturacion", array_imagenes.get(i).getVel_obturacion());
                intent.putExtra("apertura", array_imagenes.get(i).getApertura());
                intent.putExtra("titulo", array_imagenes.get(i).getTitulo());
                intent.putExtra("id_usuario", array_imagenes.get(i).getId_usuario());
                intent.putExtra("fecha", array_imagenes.get(i).getFecha());
                intent.putExtra("url", array_imagenes.get(i).getUrl());
                startActivity(intent);

            }
        });

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
            //Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
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

    static class ViewHolder {
        ImageView imageView;
    }
    //    our custom adapter
    private class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<Fotografia> imagenesMostrar;

        public ImageAdapter(Context context, ArrayList<Fotografia> imagenes){
            inflater = LayoutInflater.from(context);
            imagenesMostrar=imagenes;
        }
        @Override
        public int getCount() {
            return imagenesMostrar.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            View view = convertView;
            final ViewHolder gridViewImageHolder;
//            check to see if we have a view
            if (convertView == null) {
//                no view - so create a new one
                view = inflater.inflate(R.layout.grid_imageview, parent, false);
                gridViewImageHolder = new ViewHolder();
                gridViewImageHolder.imageView = (ImageView) view.findViewById(R.id.image);
                gridViewImageHolder.imageView.setMaxHeight(80);
                gridViewImageHolder.imageView.setMaxWidth(80);
                view.setTag(gridViewImageHolder);
            } else {
//                we've got a view
                gridViewImageHolder = (ViewHolder) view.getTag();
            }
            imageLoader.displayImage(imagenesMostrar.get(position).getUrl(), gridViewImageHolder.imageView);

            return view;
        }
    }

    private class hebraFotosUsuario extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute(){
           /* dialog = ProgressDialog.show(getActivity(), "",
                    "Cargando. Por favor, espere...", true);*/
        }
        @Override
        protected String doInBackground(Void... voids) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/fotografias.php");
            String respuesta = null;
            try {
                //Toast.makeText(getActivity(), "ID: "+id.toString(), Toast.LENGTH_SHORT).show();

                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("fotosUsuario", "fotosUsuario"));
                parametros.add(new BasicNameValuePair("id_usuario", usuario.getId().toString()));
                metodo.setEntity(new UrlEncodedFormEntity(parametros));
                HttpResponse response = cliente.execute(metodo);
                respuesta = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String mensaje) {
            //Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
            /*dialog.hide();
            dialog.dismiss();
            dialog=null;*/
            Log.d("Mensaje respuesta", mensaje);
            /*Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();*/
            try {
                JSONArray array = new JSONArray(mensaje);
                if(array!=null) {
                    for(int i=0; i<array.length();i++) {
                        Fotografia imagen = new Fotografia();
                        imagen.setId(array.getJSONObject(i).getInt("ID"));
                        imagen.setApertura(array.getJSONObject(i).getString("apertura"));
                        imagen.setFecha(array.getJSONObject(i).getString("fecha"));
                        imagen.setISO(array.getJSONObject(i).getString("ISO"));
                        imagen.setUrl(array.getJSONObject(i).getString("url"));
                        imagen.setVel_obturacion(array.getJSONObject(i).getString("vel_obturacion"));
                        imagen.setId_usuario(array.getJSONObject(i).getInt("id_usuario"));
                        imagen.setTitulo(array.getJSONObject(i).getString("titulo"));
                        array_imagenes.add(imagen);
                        //imagenes.add(array.getJSONObject(i).getString("url"));
                    }
                    gridFotos.setAdapter(new ImageAdapter(getApplicationContext(), array_imagenes));
                    fotografias.setText(String.valueOf(array_imagenes.size()));
                } else {
                    Toast.makeText(getApplicationContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private class hebraFollow extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
           /* dialog = ProgressDialog.show(getActivity(), "",
                    "Cargando. Por favor, espere...", true);*/
        }
        @Override
        protected String doInBackground(String... strings) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/usuario.php");
            String respuesta = null;
            try {
                //Toast.makeText(getActivity(), "ID: "+id.toString(), Toast.LENGTH_SHORT).show();

                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("seguirUsuario", "seguirUsuario"));
                parametros.add(new BasicNameValuePair("id_usuario1", String.valueOf(preferencias.getInt("id", -1))));
                parametros.add(new BasicNameValuePair("id_usuario2", usuario.getId().toString()));
                parametros.add(new BasicNameValuePair("metodo", strings[0]));
                metodo.setEntity(new UrlEncodedFormEntity(parametros));
                HttpResponse response = cliente.execute(metodo);
                respuesta = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String mensaje) {
            //Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
            /*dialog.hide();
            dialog.dismiss();
            dialog=null;*/
            //Log.d("Mensaje respuesta", mensaje);
            /*Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();*/
            try {
                JSONObject obj = new JSONObject(mensaje);
                if (obj != null) {
                    if (obj.getInt("error") == 0) {
                        if (botonUsuario.getText().equals("Seguir")) {
                            botonUsuario.setBackgroundColor(getResources().getColor(R.color.dejar_seguir));
                            botonUsuario.setText("Dejar de seguir");
                            Integer seg = Integer.valueOf(seguidores.getText().toString());
                            seg++;
                            seguidores.setText(seg.toString());
                        } else {
                            botonUsuario.setBackgroundColor(getResources().getColor(R.color.seguir));
                            botonUsuario.setText("Seguir");
                            Integer seg = Integer.valueOf(seguidores.getText().toString());
                            seg--;
                            seguidores.setText(seg.toString());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Ocurrió un error durante la petición al servidor, consulte con el administrador [franciscopolov@gmail.com]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
                }
            }catch(Throwable t){
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
