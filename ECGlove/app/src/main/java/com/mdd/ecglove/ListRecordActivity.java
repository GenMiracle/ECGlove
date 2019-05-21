package com.mdd.ecglove;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ListRecordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseUser user;
    private String TAG = "CHILD";
    private RecyclerView recyclerView;
    private RecordAdapter mAdapter;
    private List<String> dateTimeRecords;
    private int tap = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_record);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/ECG records");
        dateTimeRecords = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListRecordActivity.this));

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                dateTimeRecords.add(dataSnapshot.getKey());
                updateUI();
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

        updateUI();
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new RecordAdapter(dateTimeRecords);
            recyclerView.setAdapter(mAdapter);
            Log.d(TAG, "Adapter Created");
        } else {
            mAdapter.setRecords(dateTimeRecords);
            mAdapter.notifyDataSetChanged();
            Log.d(TAG, "Dataset Notified");
        }
    }

    private class RecordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvPos;
        private TextView tvDate;
        private TextView tvTime;
        private String key;
        private int position;


        public RecordHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvPos = (TextView) itemView.findViewById(R.id.tv_pos);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }

        public void bindRecord(String dateTime, int pos) {
            Log.d(TAG, "bindRecord now");
            pos++;
            position = pos;
            key = dateTime;
            String array1[] = dateTime.split(" ");
            tvPos.setText("ECG record " + pos);
            tvDate.setText("Date: " + array1[0]);
            tvTime.setText("Time: " + array1[1]);
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(ListRecordActivity.this,
                    ViewRecordActivity.class);
            intent.putExtra(ViewRecordActivity.KEY_DATE_TIME ,key);
            intent.putExtra(ViewRecordActivity.KEY_POSITION, position);
            startActivity(intent);
        }

    }

    private class RecordAdapter extends RecyclerView.Adapter<RecordHolder> {
        private List<String> records;

        public RecordAdapter(List<String> mRecords) {
            records = mRecords;
        }

        @Override
        public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(ListRecordActivity.this);
            View view = layoutInflater.inflate(R.layout.record_item, parent, false);
            return new RecordHolder(view);
        }

        @Override
        public void onBindViewHolder(RecordHolder holder, int position) {
            String mDateTime = records.get(position);
            holder.bindRecord(mDateTime, position);
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        public void setRecords(List<String> mRecords) {
            records = mRecords;
        }

    }

    public void toLive(View view){
        if(tap == 5){
            Intent intent = new Intent(ListRecordActivity.this,
                    LiveRecordActivity.class);
            String key = dateTimeRecords.get(dateTimeRecords.size()-1);
            intent.putExtra(ViewRecordActivity.KEY_DATE_TIME ,key);
            startActivity(intent);
        }
        tap++;
    }
}
