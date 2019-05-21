package com.mdd.ecglove;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RecordECGStartFragment extends Fragment {
    TextView tvInstructions;
    Button btnStart;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.ecg_starting_fragment, container, false);
        tvInstructions = rootView.findViewById(R.id.tv_instructions);
        btnStart = rootView.findViewById(R.id.btn_start);
        tvInstructions.setText("First, find a comfortable resting position.\n\n" +
                "Place your fist onto your chest as though you are taking a pledge. Unclench your fist " +
                "and ensure that the electrodes are     " +
                "positioned as shown in the diagram below.\n\n" +
                "Once done, press the 'Start' button to " +
                "begin recording!");

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new RecordECGMainFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return rootView;
    }
}
