package com.franciscopolov.lizart.lizart2;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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


public class FragmentIndex extends Fragment {
    private SharedPreferences preferencias;
    DisplayImageOptions options;
    private ImageLoader imageLoader;
    ArrayList<String> imagenes;
    ArrayList<Fotografia> array_imagenes;
    ProgressDialog dialog;
    GridView gridview;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageLoaderConfiguration defaultConfiguration
                = new ImageLoaderConfiguration.Builder(getActivity())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(defaultConfiguration);
        View v = inflater.inflate(R.layout.fragment_fragment_index, container, false);
        imageLoader = ImageLoader.getInstance();
        imagenes=new ArrayList<String>();
        array_imagenes=new ArrayList<Fotografia>();
        preferencias=getActivity().getSharedPreferences("preferenciasLizart", Context.MODE_PRIVATE);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        gridview = (GridView) v.findViewById(R.id.gridview);
        hebraFotos fotos = new hebraFotos();
        fotos.execute();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gridview.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), FotoEntera.class);
                intent.putExtra("id", array_imagenes.get(position).getId());
                intent.putExtra("ISO", array_imagenes.get(position).getISO());
                intent.putExtra("vel_obturacion", array_imagenes.get(position).getVel_obturacion());
                intent.putExtra("apertura", array_imagenes.get(position).getApertura());
                intent.putExtra("titulo", array_imagenes.get(position).getTitulo());
                intent.putExtra("id_usuario", array_imagenes.get(position).getId_usuario());
                intent.putExtra("fecha", array_imagenes.get(position).getFecha());
                intent.putExtra("url", array_imagenes.get(position).getUrl());
                startActivity(intent);
            }
        });

        return v;

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
            imageLoader.displayImage(imagenesMostrar.get(position).getUrl(),gridViewImageHolder.imageView, options);

            return view;
        }
    }

    private class hebraFotos extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute(){
            dialog = ProgressDialog.show(getActivity(), "",
                    "Cargando. Por favor, espere...", true);
        }
        @Override
        protected String doInBackground(Void... voids) {
            HttpClient cliente = new DefaultHttpClient();
            HttpPost metodo = new HttpPost("http://lizart.franciscopolov.com/json/fotografias.php");
            String respuesta = null;
            Integer id = (Integer) preferencias.getInt("id", 0);
            try {
                //Toast.makeText(getActivity(), "ID: "+id.toString(), Toast.LENGTH_SHORT).show();

                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("consulta", "consulta"));
                parametros.add(new BasicNameValuePair("id", id.toString()));
                metodo.setEntity(new UrlEncodedFormEntity(parametros));
                HttpResponse response = cliente.execute(metodo);
                respuesta = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String mensaje) {
            //Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
            dialog.hide();
            dialog.dismiss();
            dialog=null;
            //Log.d("Mensaje respuesta", mensaje);
            try{
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
                        gridview.setAdapter(new ImageAdapter(getActivity(), array_imagenes));
                } else {
                    Toast.makeText(getActivity(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
                }
            }catch(Throwable t){
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}