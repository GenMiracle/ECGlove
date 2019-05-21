package com.mdd.ecglove;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
    public final String KEY_NAME = "name";
    public final String KEY_EMAIL = "email";
    public final String KEY_DOB = "DOB";
    public final String KEY_SEX = "sex";
    public final String KEY_HEART_CONDITIONS = "heart conditions";
    public final String KEY_CLINIC = "clinic";
    public final String KEY_CLINICIAN = "clinician";
    public final String KEY_PHONE = "phone";
    private String name;
    private String email;
    private String DOB;
    private String sex;
    private String clinic;
    private String clinician;
    private String phone;
    private String heartConditions;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvProfileName;
    private TextView tvDOB;
    private TextView tvSex;
    private TextView tvPhone;
    private TextView tvHeartConditions;
    private TextView tvClinic;
    private TextView tvClinician;


    public void onCreate(Bundle savedInstanceState) {
        name = "USER";
        email = "NIL";
        DOB = "NIL";
        sex = "NIL";
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(KEY_NAME);
            email = getArguments().getString(KEY_EMAIL);
            DOB = getArguments().getString(KEY_DOB);
            sex = getArguments().getString(KEY_SEX);
            clinic = getArguments().getString(KEY_CLINIC);
            phone = getArguments().getString(KEY_PHONE);
            clinician = getArguments().getString(KEY_CLINICIAN);
            heartConditions = getArguments().getString(KEY_HEART_CONDITIONS);
            Log.d("profile", "HC: " + heartConditions);
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.main_page_profile_fagment, container, false);
        tvName = rootView.findViewById(R.id.tv_profile_name);
        tvProfileName = rootView.findViewById(R.id.profile_user_name);
        tvEmail = rootView.findViewById(R.id.profile_user_email);
        tvSex = rootView.findViewById(R.id.profile_user_sex);
        tvDOB = rootView.findViewById(R.id.profile_user_dob);
        tvClinic = rootView.findViewById(R.id.profile_clinic);
        tvClinician = rootView.findViewById(R.id.profile_clinician);
        tvHeartConditions = rootView.findViewById(R.id.profile_heart_conditions);
        tvPhone = rootView.findViewById(R.id.profile_phone);
        tvName.setText(name);
        tvProfileName.setText(name);
        tvEmail.setText(email);
        tvDOB.setText(DOB);
        tvSex.setText(sex);
        tvClinic.setText(clinic);
        tvPhone.setText(phone);
        tvClinician.setText(clinician);
        tvHeartConditions.setText(heartConditions);
        return rootView;
    }
}
