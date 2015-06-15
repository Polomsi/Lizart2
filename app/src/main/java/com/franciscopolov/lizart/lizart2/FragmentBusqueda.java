package com.franciscopolov.lizart.lizart2;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBusqueda extends Fragment {

    EditText nombreUsuario;
    ListView listaUsuarios;
    ArrayList<Usuario> usuariosBusqueda;

    public FragmentBusqueda() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_busqueda, container, false);
        nombreUsuario = (EditText) v.findViewById(R.id.etNomUsuBuscar);
        usuariosBusqueda = new ArrayList<>();
        listaUsuarios = (ListView) v.findViewById(R.id.listaUsuarios);
        nombreUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!nombreUsuario.getText().toString().equals("")){
                    hebraConsultaUsuarios hcu = new hebraConsultaUsuarios();
                    hcu.execute();
                }
                else{
                    usuariosBusqueda.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        listaUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nombre = usuariosBusqueda.get(i).getNombre();
                String email = usuariosBusqueda.get(i).getEmail();
                String foto_perfil = usuariosBusqueda.get(i).getFoto_perfil();
                Integer id = usuariosBusqueda.get(i).getId();
                String descripcion = usuariosBusqueda.get(i).getDescripcion();
                Intent intent = new Intent(getActivity(), UsuarioEntero.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("email", email);
                intent.putExtra("foto_perfil", foto_perfil);
                intent.putExtra("id", id);
                intent.putExtra("descripcion", descripcion);
                startActivity(intent);
            }
        });
        return v;
    }
    private class hebraConsultaUsuarios extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... strings) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/usuario.php");
            String respuesta = null;
            try {
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("buscar", "buscar"));
                parametros.add(new BasicNameValuePair("nombre", nombreUsuario.getText().toString()));
                metodo.setEntity(new UrlEncodedFormEntity(parametros));
                HttpResponse response = cliente.execute(metodo);
                respuesta = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String mensaje) {
            //Toast.makeText(getActivity().getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            usuariosBusqueda.clear();
            try{
                JSONArray array = new JSONArray(mensaje);
                if(array!=null) {
                    for (int i = 0; i < array.length(); i++) {
                        Usuario us = new Usuario();
                        us.setId(array.getJSONObject(i).getInt("ID"));
                        us.setNombre(array.getJSONObject(i).getString("nom_usuario"));
                        us.setDescripcion(array.getJSONObject(i).getString("descripcion"));
                        us.setFoto_perfil(array.getJSONObject(i).getString("foto_perfil"));
                        us.setEmail(array.getJSONObject(i).getString("email"));
                        usuariosBusqueda.add(us);
                        us=null;
                    }

                    AdaptadorUsuarios ac = new AdaptadorUsuarios(getActivity().getApplicationContext(), usuariosBusqueda);
                    listaUsuarios.setAdapter(ac);
                    //setListViewHeightBasedOnChildren(listaComentarios);
                }
                else{

                }
            }catch(Throwable t){
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
