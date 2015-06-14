package com.franciscopolov.lizart.lizart2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Polo on 14/06/2015.
 */
public class AdaptadorUsuarios extends ArrayAdapter<Usuario> {
    private ImageLoader imageLoader;
    public AdaptadorUsuarios(Context context, ArrayList<Usuario> users) {
        super(context, 0, users);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Usuario user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_usuarios, parent, false);
        }
        // Lookup view for data population
        TextView tvNombre = (TextView) convertView.findViewById(R.id.nombreUsuBusqueda);
        TextView tvComentario = (TextView) convertView.findViewById(R.id.descripcionUsuBusqueda);
        ImageView ivFoto = (ImageView) convertView.findViewById(R.id.fotoUsuBusqueda);
        // Populate the data into the template view using the data object
        tvNombre.setText(user.getNombre());
        tvComentario.setText(user.getDescripcion());
        imageLoader= ImageLoader.getInstance();
        imageLoader.displayImage(user.getFoto_perfil(), ivFoto);
        // Return the completed view to render on screen
        return convertView;
    }
}
