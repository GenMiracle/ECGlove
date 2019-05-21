package com.mdd.ecglove;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionBarContainer;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainPageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseUser user;
    private TextView tvUserName;
    private DrawerLayout mDrawerLayout;
    private String uid;
    private FragmentManager fm;
    private UserData userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new HomeFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

        setContentView(R.layout.activity_main_page);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        userData = new UserData();
        userData.setEmail(user.getEmail());
        databaseRef = FirebaseDatabase.getInstance().getReference("users/" + uid);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        int id = menuItem.getItemId();
                        menuItem.setChecked(false);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        Fragment fragment;

                        switch (id) {
                            case R.id.item_home:
                                fragment = new HomeFragment();
                                break;
                            case R.id.item_my_profile:
                                fragment = new ProfileFragment();
                                Bundle data = new Bundle();
                                data.putSerializable(((ProfileFragment) fragment).KEY_EMAIL,userData.getEmail());
                                data.putSerializable(((ProfileFragment) fragment).KEY_NAME,userData.getName());
                                data.putSerializable(((ProfileFragment) fragment).KEY_DOB,userData.getDOB());
                                data.putSerializable(((ProfileFragment) fragment).KEY_SEX,userData.getSex());
                                data.putSerializable(((ProfileFragment) fragment).KEY_HEART_CONDITIONS,userData.getHeartConditions());
                                data.putSerializable(((ProfileFragment) fragment).KEY_PHONE,userData.getPhone());
                                data.putSerializable(((ProfileFragment) fragment).KEY_CLINIC,userData.getClinic());
                                data.putSerializable(((ProfileFragment) fragment).KEY_CLINICIAN,userData.getClinician());
                                fragment.setArguments(data);
                                break;
                            case R.id.item_settings:
                                fragment = new SettingsFragment();
                                break;
                            case R.id.item_support:
                                fragment = new SupportFragment();
                                break;
                            case R.id.item_terms_conditions:
                                fragment = new TermsFragment();
                                break;
                            case R.id.item_log_out:
                                logout();
                                return true;

                            default:
                                return true;


                        }
                        fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        return true;
                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionbar = getSupportActionBar();

        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        tvUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_username);
        ;
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userData = dataSnapshot.getValue(UserData.class);
                if (userData != null) {
                    tvUserName.setText(userData.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
        // Add the buttons
        builder.setTitle("Exit ECGlove?");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                System.exit(0);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);

        // Add the buttons
        builder.setTitle("Log Out?");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Log Out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mAuth.signOut();
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(getApplicationContext(),
                            MainActivity.class));
                } else {
                    Toast.makeText(MainPageActivity.this, "Unable to logout",
                            Toast.LENGTH_SHORT).show();
                    ;
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}


