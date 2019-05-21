package com.mdd.ecglove;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

public class LiveRecordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseUser user;
    String keyDateTime;
    int position;
    public static final String KEY_DATE_TIME = "datetime";
    public static final String KEY_POSITION = "position";
    String TAG = "RECORD ACTIVITY";
    TextView tvTitle;
    TextView tvDate;
    TextView tvTime;
    LineChart gChart2;
    private LineData gLineData;
    private List<Entry> entries;
    private int xSize = 200;
    final int DEFAULT_Y = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_record);
        keyDateTime = getIntent().getStringExtra(KEY_DATE_TIME);
        position = getIntent().getIntExtra(KEY_POSITION, 1);
        entries = new ArrayList<Entry>();


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/ECG records/" + keyDateTime);

        tvTitle = (TextView) findViewById(R.id.tv_lr_title);
        tvDate = (TextView) findViewById(R.id.tv_lr_view_date);
        tvTime = (TextView) findViewById(R.id.tv_lr_view_time);

        String array1[] = keyDateTime.split(" ");
        tvDate.setText("Date:\n" + array1[0]);
        tvTime.setText("Time:\n" + array1[1]);

        gChart2 = (LineChart) findViewById(R.id.chart_record_live);
        gChart2.setScaleEnabled(false);
        gChart2.setPinchZoom(false);
        gChart2.getAxisLeft().setAxisMinimum(0);
        gChart2.getAxisLeft().setAxisMaximum(1200);
        gChart2.getAxisLeft().setEnabled(false);
        gChart2.getAxisRight().setEnabled(false);
        gChart2.getXAxis().setEnabled(false);
        gChart2.getLegend().setEnabled(false);
        gChart2.getDescription().setEnabled(false);
        gChart2.setDrawBorders(true);
        gChart2.setBorderColor(ContextCompat.getColor(LiveRecordActivity.this, R.color.colorPrimary));
        //Graph
        entries = new ArrayList<Entry>();
        for (int i = 0; i < xSize; i++) {
            entries.add(
                    new Entry(i, DEFAULT_Y));
        }

        // Customising dataset to be given to gChart2
        LineDataSet mDataSet = new LineDataSet(entries, "Label");
        mDataSet.setHighlightEnabled(false);
        mDataSet.setDrawValues(false);
        mDataSet.setDrawCircles(false);
        mDataSet.setLineWidth(0.5f);
        mDataSet.setColor(Color.RED);
        mDataSet.setLabel("");
        gLineData = new LineData(mDataSet);
        gChart2.setData(gLineData);

        ChildEventListener childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                SampleData sample = dataSnapshot.getValue(SampleData.class);
                addData(sample.getValue());
                gChart2.invalidate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getParent(), "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        databaseRef.addChildEventListener(childEventListener);


    }
    private void addData(String data) {
        float newPoint;
        try{
            newPoint = Float.valueOf(data);
        }catch(NumberFormatException ex){
            return;
        }
        for (int j = 0; j < xSize - 1; j++) {
            entries.get(j).setY(entries.get(j + 1).getY());
        }
        entries.get(xSize - 1).setY(newPoint);
        gChart2.invalidate();

    }
}
