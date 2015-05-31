package com.franciscopolov.lizart.lizart2;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FragmentIndex extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_fragment_index, container, false);
        //TextView text = new TextView(this.getActivity());
        //text.setText("Section");
        //text.setGravity(Gravity.CENTER);
        return v;

    }
}