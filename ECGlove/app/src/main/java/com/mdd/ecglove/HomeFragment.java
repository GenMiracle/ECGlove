package com.mdd.ecglove;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    private Button btnRecord;
    private Button btnViewRecord;
    private TextView tvHeading;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.main_page_home_fragment, container, false);
        btnRecord = (Button) rootView.findViewById(R.id.btn_record);
        btnViewRecord = (Button) rootView.findViewById(R.id.btn_view_records);
        tvHeading = (TextView) rootView.findViewById(R.id.tv_heading);

        tvHeading.setText("Welcome Back!");
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity().getApplicationContext(),
                        RecordECGActivity.class));

            }
        });

        btnViewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(),
                        ListRecordActivity.class));
            }
        });

        return rootView;
    }

}
