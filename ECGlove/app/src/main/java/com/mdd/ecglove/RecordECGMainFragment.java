package com.mdd.ecglove;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.Math.round;

public class RecordECGMainFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseRef;
    Button btnConnect;
    Button btnRecord;
    TextView tvStatus;
    TextView tvBPM;
    LineChart gChart2;
    String recordDateTime;
    private LineData gLineData;
    private List<Entry> entries;
    private int xSize = 200;
    private final int SAMPLE_SIZE = 1500;
    private float ECGsamples[] = new float[SAMPLE_SIZE];
    private int index = 0;
    private int recordIndex = 0;
    private final int DEFAULT_Y = 200;
    private final String BT_DEVICE_ADDRESS = "00:14:03:05:59:9B";
    private final int REQUEST_ENABLE_BT = 1;
    private final int MESSAGE_READ = 1;
    private final int MESSAGE_BPM = 2;
    private final int MESSAGE_BPM_0 = 3;

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice mDevice;
    BluetoothSocket mmSocket;
    InputStream mmInStream;
    OutputStream mmOutStream;
    Boolean isConnected = false;
    Boolean isRecording = false;
    private long startTime = 0;

     @Override
     public void onCreate(Bundle savedInstanceState){
         super.onCreate(savedInstanceState);
         mAuth = FirebaseAuth.getInstance();
         user = mAuth.getCurrentUser();
         databaseRef = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
     }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    String in = (String) msg.obj;
                    addData(in);
                    if(isRecording){
                        Log.d("RECORD", "isRecording true");
                    }
                    break;
                case MESSAGE_BPM:
                    Log.d("BPM", "MESSAGE_BPM received");
                    int bpm = (int) msg.obj;
                    tvBPM.setText(bpm + " BPM");
                    break;
                case MESSAGE_BPM_0:
                    Log.d("BPM", "MESSAGE_BPM received");
                    tvBPM.setText("-- BPM");
                    break;
            }
            return false;
        }
    });

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                tvStatus.setText("Not Connected"); //Device has disconnected
                resetConnection();
                isConnected = false;
                isRecording = false;
                btnConnect.setText("Connect");
                Toast.makeText(getActivity(),"Device Disconnected",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        getActivity().registerReceiver(mReceiver,filter);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.ecg_main_fragment, container, false);
        btnConnect = (Button) rootView.findViewById(R.id.btn_connect);
        btnRecord = (Button) rootView.findViewById(R.id.btn_ecgrecord);
        tvStatus = (TextView) rootView.findViewById(R.id.tv_status);
        tvBPM = (TextView) rootView.findViewById(R.id.tv_bpm);


        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBluetoothAdapter.isEnabled()) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                    return;
                }

                mDevice = mBluetoothAdapter.getRemoteDevice(BT_DEVICE_ADDRESS);

                if (isConnected) {
                    resetConnection();
                    btnConnect.setText("Connect");
                    tvStatus.setText("Not Connected");
                }
                else if (!isConnected) {
                    ConnectThread ct = new ConnectThread(mDevice);      //socket connection two devices
                    ct.start();

                    ConnectedThread cet = new ConnectedThread(mmSocket);
                    cet.start();//Stream connection for two devices

                }
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording){
                    isRecording = false;
                    btnRecord.setText("Record");

                }
                else if(!isRecording){
                    if(!isConnected){
                        Toast.makeText(getActivity(),"Connect to device first",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    isRecording = true;
                    recordIndex = 0;
                    recordDateTime = android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss", new java.util.Date()).toString();
                    Toast.makeText(getActivity(),recordDateTime,Toast.LENGTH_SHORT).show();
                    btnRecord.setText("Stop Recording");
                }

            }
        });


        gChart2 = (LineChart) rootView.findViewById(R.id.chart2);
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
        gChart2.setBorderColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
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

        gChart2.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view){
                tvBPM.setText("-- BPM");
                for (int i = 0; i < xSize; i++) {
                    entries.get(i).setY(DEFAULT_Y);
                }
                index = 0;
                return true;
            }
        });

        return rootView;
    }
    private class ConnectThread extends Thread {

        private final BluetoothDevice mmDevice;
        private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                Log.d("BT", "Socket's create() sucess");
                Toast.makeText(getActivity(),"Connecting to device...",Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.d("BT", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }


        public void run() {
            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
                Log.d("Socket", "Socket Connected!!!");
                isConnected = true;
                index = 0;
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("Socket", "Could not close the client socket", closeException);
                }
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isConnected) {
                        btnConnect.setText("Disconnect");
                        tvStatus.setText("Connected");
                        Toast.makeText(getActivity(), "Sucessfully connected", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            if (mmSocket == null) {
            }
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            String inMsg = "";
            int count = 0;
            while (true) {
                // Read from the InputStream
                try {

                    if (isConnected) {
                        if (mmInStream.available() > 0) {
                            int in;
                            in = mmInStream.read();
                            if (in == 10) {
                                inMsg += (char) in;
                                handler.obtainMessage(MESSAGE_READ, inMsg).sendToTarget();
                                inMsg = "";
                            } else {
                                inMsg += (char) in;
                            }

                        }
                    }

                } catch (IOException e) {
                    Log.e("BT", "write: Error reading Input Stream. " + e.getMessage());
                    break;
                }
            }
        }
    }
    private class calECG extends Thread{
        float samples[];
        final int MIN = 60000;
        long duration;

        calECG(long duration, float[] ECGsamples){
            samples = ECGsamples;
            this.duration = duration;
        }
        public void run(){
            int heartbeat = detect(samples);
            if (heartbeat != 0){
                float sampleDuration = duration/(samples.length);
                float timeBetweenHB = sampleDuration*heartbeat;
                int bpm = (int)(MIN/timeBetweenHB);
                handler.obtainMessage(MESSAGE_BPM, bpm).sendToTarget();
            }else{
                handler.obtainMessage(MESSAGE_BPM_0).sendToTarget();
            }

        }

        // this function detects the peak of the ecg signal. Unfortunately, I'm unable to recall the source of the function.
        private int detect(float[] ecg) {
            // circular buffer for input ecg signal
            // we need to keep a history of M + 1 samples for HP filter
            //m=5

            boolean measuredPeak1 = false;
            int total = 0;
            int cnt = 0;
            int peak1 = 0;
            final int M = 10;
            final int N = 30;
            final int winSize = 250;
            final float HP_CONSTANT = (float) 1 / M;

            float[] ecg_circ_buff = new float[M + 1];
            int ecg_circ_WR_idx = 0;
            int ecg_circ_RD_idx = 0;

            // circular buffer for input ecg signal
            // we need to keep a history of N+1 samples for LP filter
            float[] hp_circ_buff = new float[N + 1];
            int hp_circ_WR_idx = 0;
            int hp_circ_RD_idx = 0;

            // LP filter outputs a single point for every input point
            // This goes straight to adaptive filtering for eval
            float next_eval_pt = 0;

            // output
            int[] QRS = new int[ecg.length];

            // running sums for HP and LP filters, values shifted in FILO
            float hp_sum = 0;
            float lp_sum = 0;

            // parameters for adaptive thresholding
            double treshold = 0;
            boolean triggered = false;
            int trig_time = 0;
            float win_max = 0;
            int win_idx = 0;

            for (int i = 0; i < ecg.length; i++) {
                ecg_circ_buff[ecg_circ_WR_idx++] = ecg[i];
                ecg_circ_WR_idx %= (M + 1);

                /* High pass filtering */
                if (i < M) {
                    // first fill buffer with enough points for HP filter
                    hp_sum += ecg_circ_buff[ecg_circ_RD_idx];
                    hp_circ_buff[hp_circ_WR_idx] = 0;
                } else {
                    hp_sum += ecg_circ_buff[ecg_circ_RD_idx];

                    int tmp = ecg_circ_RD_idx - M;
                    if (tmp < 0) {
                        tmp += M + 1;
                    }
                    hp_sum -= ecg_circ_buff[tmp];

                    float y1 = 0;
                    float y2 = 0;

                    tmp = (ecg_circ_RD_idx - ((M + 1) / 2));
                    if (tmp < 0) {
                        tmp += M + 1;
                    }
                    y2 = ecg_circ_buff[tmp];

                    y1 = HP_CONSTANT * hp_sum;

                    hp_circ_buff[hp_circ_WR_idx] = y2 - y1;
                }

                ecg_circ_RD_idx++;
                ecg_circ_RD_idx %= (M + 1);

                hp_circ_WR_idx++;
                hp_circ_WR_idx %= (N + 1);

                /* Low pass filtering */

                // shift in new sample from high pass filter
                lp_sum += hp_circ_buff[hp_circ_RD_idx] * hp_circ_buff[hp_circ_RD_idx];

                if (i < N) {
                    // first fill buffer with enough points for LP filter
                    next_eval_pt = 0;

                } else {
                    // shift out oldest data point
                    int tmp = hp_circ_RD_idx - N;
                    if (tmp < 0) {
                        tmp += N + 1;
                    }
                    lp_sum -= hp_circ_buff[tmp] * hp_circ_buff[tmp];

                    next_eval_pt = lp_sum;
                }

                hp_circ_RD_idx++;
                hp_circ_RD_idx %= (N + 1);

                /* Adapative thresholding beat detection */
                // set initial threshold
                if (i < winSize) {
                    if (next_eval_pt > treshold) {
                        treshold = next_eval_pt;
                    }
                }

                // check if detection hold off period has passed
                if (triggered) {
                    trig_time++;

                    if (trig_time >= 100) {
                        triggered = false;
                        trig_time = 0;
                    }
                }

                // find if we have a new max
                if (next_eval_pt > win_max) win_max = next_eval_pt;

                // find if we are above adaptive threshold
                if (next_eval_pt > treshold && !triggered) {
                    QRS[i] = 1;
                    if (!measuredPeak1){
                        peak1 = i;
                        measuredPeak1 = true;
                    }
                    else if (measuredPeak1){
                        total += i - peak1;
                        peak1 = i;
                        cnt++;
                    }
                    triggered = true;
                } else {
                    QRS[i] = 0;
                }

                // adjust adaptive threshold using max of signal found
                // in previous window
                if (++win_idx > winSize) {
                    // weighting factor for determining the contribution of
                    // the current peak value to the threshold adjustment
                    double gamma = 0.175;

                    // forgetting factor -
                    // rate at which we forget old observations
                    double alpha = 0.01 + (Math.random() * ((0.1 - 0.01)));

                    treshold = alpha * gamma * win_max + (1 - alpha) * treshold;

                    // reset current window ind
                    win_idx = 0;
                    win_max = -10000000;
                }
            }

            int sample = 0;
            if (cnt != 0){
                sample = round(total/cnt);
                Log.d("TIME","num of samples between heartbeats: " + sample);
            }
            return sample;
        }
    }

    private void resetConnection() {
        if (mmInStream != null) {
            try {
                mmInStream.close();
            } catch (Exception e) {
            }
            mmInStream = null;
        }

        if (mmOutStream != null) {
            try {
                mmOutStream.close();
            } catch (Exception e) {
            }
            mmOutStream = null;
        }

        if (mmSocket != null) {
            try {
                mmSocket.close();
            } catch (Exception e) {
            }
            mmSocket = null;
        }
        isConnected = false;
    }

    private void addData(String data) {
        float newPoint;
        try{
            newPoint = Float.valueOf(data);
        }catch(NumberFormatException ex){
            return;
        }
        if(index == 0){
            startTime = System.currentTimeMillis();
            Log.d("TIME", "Start time: " + startTime);
        }

        for (int j = 0; j < xSize - 1; j++) {
            entries.get(j).setY(entries.get(j + 1).getY());
        }
        entries.get(xSize - 1).setY(newPoint);
        gChart2.invalidate();
        ECGsamples[index] = newPoint;
        index++;

        if(index == SAMPLE_SIZE-1){
            index = 0;
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            Log.d("TIME", "Duration: " + duration);
            calECG countBPM = new calECG(duration, ECGsamples);
            countBPM.run();
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        resetConnection();
        isConnected = false;
        isRecording = false;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        resetConnection();
        getActivity().unregisterReceiver(mReceiver);
    }

    public void recordData(String data){
        Log.d("RECORD", "Recording Data");
        String input = data.trim();
        if(!TextUtils.isEmpty(input) && TextUtils.isDigitsOnly(input)){
            Log.d("RECORD", "Recorded Data");
            SampleData sampleData = new SampleData(Integer.toString(recordIndex),input);
            databaseRef.child("ECG records").child(recordDateTime).child(Integer.toString(recordIndex)).setValue(sampleData);
            recordIndex++;
        }
    }

}
