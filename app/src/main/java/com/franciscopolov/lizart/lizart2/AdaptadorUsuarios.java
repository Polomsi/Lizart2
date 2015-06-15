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
        // Devuelve los datos para la posición actual
        Usuario user = getItem(position);
        // Se comprueba si ya existe una vista de este tipo, si no la hay, se crea, asignandole la vista de la lista de usuarios
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_usuarios, parent, false);
        }
        // Primero asignamos a las variables correspondientes sus ids
        TextView tvNombre = (TextView) convertView.findViewById(R.id.nombreUsuBusqueda);
        TextView tvComentario = (TextView) convertView.findViewById(R.id.descripcionUsuBusqueda);
        ImageView ivFoto = (ImageView) convertView.findViewById(R.id.fotoUsuBusqueda);
        // Una vez asignados, poblamos esas variables
        tvNombre.setText(user.getNombre());
        tvComentario.setText(user.getDescripcion());
        imageLoader= ImageLoader.getInstance();
        imageLoader.displayImage(user.getFoto_perfil(), ivFoto);
        // Se devuelve la vista una vez completada la insercion de datos
        return convertView;
    }
}
