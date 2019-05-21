package com.mdd.ecglove;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private UserData userData;
    EditText etName;
    EditText etEmail;
    EditText etPassword;
    EditText etConfirmPassword;
    EditText etDOBdd;
    EditText etDOBmm;
    EditText etDOByyyy;
    EditText etPhone;
    EditText etHeartConditions;
    EditText etClinic;
    EditText etClinician;
    Button btnRegister;
    TextView tvFeedback;
    Spinner spinner;
    private DatabaseReference databaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] spinnerItems = new String[]{"","Male", "Female"};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        etName = (EditText) findViewById(R.id.et_user_name);
        etDOBdd = (EditText) findViewById(R.id.et_dob_dd);
        etDOBmm = (EditText) findViewById(R.id.et_dob_mm);
        etDOByyyy = (EditText) findViewById(R.id.et_dob_yyyy);
        etEmail = (EditText) findViewById(R.id.et_user_email);
        etPassword = (EditText) findViewById(R.id.et_user_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        etClinic = (EditText) findViewById(R.id.et_clinic);
        etClinician = (EditText) findViewById(R.id.et_clinician);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etHeartConditions = (EditText) findViewById(R.id.et_heart_condition);
        btnRegister = (Button) findViewById(R.id.btn_register);
        tvFeedback = (TextView) findViewById(R.id.tv_feedback);
        spinner = findViewById(R.id.spinner);
        databaseRef = FirebaseDatabase.getInstance().getReference("users");
        userData = new UserData();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

    }
    private void startRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String dob_dd = etDOBdd.getText().toString().trim();
        String dob_mm = etDOBmm.getText().toString().trim();
        String dob_yyyy = etDOByyyy.getText().toString().trim();
        String dob;
        String phone = etPhone.getText().toString().trim();;
        String heartConditions = etHeartConditions.getText().toString().trim();
        String clinic = etClinic.getText().toString().trim();
        String clinician = etClinician.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            tvFeedback.setText("Name Field is Empty");
            return;
        }
        if(userData.getSex().equals("") || userData.getSex() == null){
            tvFeedback.setText("Please indicate you sex");
            return;
        }
        if (isDigit(dob_dd) || isDigit(dob_mm) || isDigit(dob_yyyy) || dob_dd.length() != 2 || dob_mm.length() != 2 || dob_yyyy.length() != 4) {
            tvFeedback.setText("Invalid DOB");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            tvFeedback.setText("Email Field is Empty");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            tvFeedback.setText("phone Field is Empty");
            return;
        }
        if (isDigit(phone) || phone.length() != 8) {
            tvFeedback.setText("Invalid DOB");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tvFeedback.setText("Password Field is Empty");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            tvFeedback.setText("Confirm Password Field is Empty");
            return;
        }
        if (!confirmPassword.equals(password)) {
            tvFeedback.setText("Passwords do not match ");
            return;
        }
        if (TextUtils.isEmpty(heartConditions)) {
            heartConditions = "NIL";
        }
        if (TextUtils.isEmpty(clinic)) {
            clinic = "Not Registered";
        }
        if (TextUtils.isEmpty(clinician)) {
            clinician = "Not Registered";
        }
        mProgress.show();
        dob = dob_dd + "/" + dob_mm + "/" + dob_yyyy;
        userData.setDOB(dob);
        userData.setName(name);
        userData.setEmail(email);
        userData.setPhone(phone);
        userData.setClinic(clinic);
        userData.setClinician(clinician);
        userData.setHeartConditions(heartConditions);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            //check if successful
                            if (task.isSuccessful()) {
                                //User is successfully registered and logged in
                                //start Profile Activity here

                                Toast.makeText(UserRegistrationActivity.this, "registration successful",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();
                                databaseRef.child(uid).setValue(userData);
                                mProgress.dismiss();
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                            }else{
                                mProgress.dismiss();
                                Toast.makeText(UserRegistrationActivity.this, "Couldn't register, try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String item = parent.getItemAtPosition(pos).toString();
        if(item != null){
            userData.setSex(item);
        }

        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    private boolean isDigit(String item){
        if(TextUtils.isEmpty(item) || TextUtils.isDigitsOnly(item)){
            return false;
        }
        return true;
    }
}

