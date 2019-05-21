package com.mdd.ecglove;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


// Main Activity is the log in page
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnRegister;
    private ProgressDialog mProgress;
    private EditText email;
    private EditText password;
    private TextView tvResetPassword;
    private FirebaseUser currentUser;
    private Button button;
    private Intent i;
    private String str_email;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        i = new Intent(getApplicationContext(), MainPageActivity.class);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        btnRegister = (Button) findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        UserRegistrationActivity.class));
            }
        });

        email = (EditText)findViewById(R.id.et_user_email);
        password = (EditText)findViewById(R.id.et_user_password);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        button = (Button)findViewById(R.id.btn_sign_in);
        tvResetPassword = (TextView) findViewById(R.id.tv_reset_password);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        tvResetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                str_email = email.getText().toString().trim();
                if (TextUtils.isEmpty(str_email)) {
                    Toast.makeText(MainActivity.this, "Fill in your email address",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
// Add the buttons
                builder.setTitle("Reset Password");
                builder.setMessage("Do you want to reset your password?");
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("Reset", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mAuth.sendPasswordResetEmail(str_email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Main Activity", "Email sent.");
                                            Toast.makeText(MainActivity.this, "Link to reset you password has been sent to your email" ,
                                                    Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(MainActivity.this, "Failed to reset password" ,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });
    }

    public void LoginUser(){
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(MainActivity.this, "Fill in your email address",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Password)) {
            Toast.makeText(MainActivity.this, "Fill in your password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            mProgress.dismiss();
                            finish();
                            startActivity(i);
                        }else{
                            mProgress.dismiss();
                            Toast.makeText(MainActivity.this, "Unable to login",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed(){
    }

    public void autoSignIn(View view){
        if(count == 5){
            email.setText("ktoh006@e.ntu.edu.sg");
            password.setText("schooling");
            LoginUser();
        }
        count++;
        if(count == 1 )
        Toast.makeText(MainActivity.this,"Hi-5!",Toast.LENGTH_SHORT).show();
    }
}
