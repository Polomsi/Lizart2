package com.franciscopolov.lizart.lizart2;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentConfig extends Fragment {


    public FragmentConfig() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        startActivity(new Intent(getActivity(), Configuracion.class));
        return inflater.inflate(R.layout.fragment_fragment_config, container, false);
    }


}
