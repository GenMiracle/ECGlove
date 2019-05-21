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

public class ViewRecordActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_record);
        keyDateTime = getIntent().getStringExtra(KEY_DATE_TIME);
        position = getIntent().getIntExtra(KEY_POSITION, 1);
        entries = new ArrayList<Entry>();


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/ECG records/" + keyDateTime);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDate = (TextView) findViewById(R.id.tv_view_date);
        tvTime = (TextView) findViewById(R.id.tv_view_time);

        String array1[] = keyDateTime.split(" ");
        tvDate.setText("Date:\n" + array1[0]);
        tvTime.setText("Time:\n" + array1[1]);
        tvTitle.setText("ECG Record " + position);

        gChart2 = (LineChart) findViewById(R.id.chart_record);
        gChart2.setPinchZoom(false);
        gChart2.getAxisLeft().setAxisMinimum(0f);
        gChart2.getAxisLeft().setAxisMaximum(1200f);
        gChart2.getAxisLeft().setEnabled(false);
        gChart2.getAxisRight().setEnabled(false);
        gChart2.getXAxis().setEnabled(false);
        gChart2.getXAxis().setAxisMinimum(0);
        gChart2.getXAxis().setAxisMaximum(200);
        gChart2.setVisibleXRange(200,200);
        gChart2.setScaleYEnabled(false);
        gChart2.setScaleXEnabled(false);
        gChart2.getLegend().setEnabled(false);
        gChart2.getDescription().setEnabled(false);
        gChart2.setDrawBorders(true);
        gChart2.setBorderColor(ContextCompat.getColor(ViewRecordActivity.this, R.color.colorPrimary));
        //Graph
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
                float x =Float.valueOf(sample.getIndex());
                float y = Float.valueOf(sample.getValue());
                if(x>200){
                    gChart2.getXAxis().resetAxisMinimum();
                    gChart2.getXAxis().setAxisMaximum(x);
                    gChart2.setVisibleXRange(200,200);
                    gChart2.moveViewToX(x-200);
                }
                Log.d(TAG, "x: " + x + "   y: " + y);
                entries.add(new Entry(x,y));
                gChart2.invalidate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                // Comment newComment = dataSnapshot.getValue(Comment.class);
                //String commentKey = dataSnapshot.getKey();

                // ...
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
}
