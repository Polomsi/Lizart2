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
 * Created by Polo on 11/06/2015.
 */
public class AdaptadorComentarios extends ArrayAdapter<Comentario> {
    private ImageLoader imageLoader;
    public AdaptadorComentarios(Context context, ArrayList<Comentario> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Comentario user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_comentarios, parent, false);
        }
        // Lookup view for data population
        TextView tvNombre = (TextView) convertView.findViewById(R.id.nombreUsuCom);
        TextView tvComentario = (TextView) convertView.findViewById(R.id.comentarioUsu);
        ImageView ivFoto = (ImageView) convertView.findViewById(R.id.fotoUsuComenta);
        // Populate the data into the template view using the data object
        tvNombre.setText(user.getNom_usu());
        tvComentario.setText(user.getComent());
        imageLoader= ImageLoader.getInstance();
        imageLoader.displayImage(user.getUrl_fotoUsu(), ivFoto);
        // Return the completed view to render on screen
        return convertView;
    }
}
