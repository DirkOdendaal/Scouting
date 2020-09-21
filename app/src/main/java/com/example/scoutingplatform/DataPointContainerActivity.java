package com.example.scoutingplatform;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DataPointContainerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapterDatapointContainer myAdapter;
    DatabaseHelper mDatabaseHelper;
    Button btnBackdpca;
    Context ct;
    private BroadcastReceiver br;
    public final static String BROADCAST_ACTION = "BROADCAST_ACTION";
    AlertDialog al;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_point_container);
        mDatabaseHelper = new DatabaseHelper(this);
        ct = this;
        SharedPreferences settings = getSharedPreferences("Scouting", 0);
        Integer reqdp = settings.getInt("RequiredDataPoints", 0);
        //String ScannedText = settings.getString("ScannedText", "");
       // Toast.makeText(this,ScannedText,Toast.LENGTH_SHORT).show();
        btnBackdpca = findViewById(R.id.btnBackdpca);
        btnBackdpca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent b = new Intent(getBaseContext(), MapsActivity.class);
               b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                b.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                navigateUpTo(b);
            }
        });
        recyclerView = findViewById(R.id.recyclerDP);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new RecyclerAdapterDatapointContainer(this, getMyList(reqdp),getIntent());
        recyclerView.setAdapter(myAdapter);

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);
        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                SharedPreferences settings = getSharedPreferences("Scouting", 0);
                Integer reqdp = settings.getInt("RequiredDataPoints", 0);
//                ArrayList<Modeldp> ls = getMyList(reqdp);
                myAdapter = new RecyclerAdapterDatapointContainer(ct, getMyList(reqdp),getIntent());
//                myAdapter.notifyDataSetChanged();
//                myAdapter.notifyItemRangeChanged(0, ls.size());

                //recyclerView.invalidate();
              int scrollstate =  recyclerView.getScrollState();
//                recyclerView
                recyclerView.setAdapter(myAdapter);

            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);
        SharedPreferences settings = getSharedPreferences("Scouting", 0);
        Integer reqdp = settings.getInt("RequiredDataPoints", 0);
        myAdapter = new RecyclerAdapterDatapointContainer(this, getMyList(reqdp),getIntent());
        recyclerView.setAdapter(myAdapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent b = new Intent(getBaseContext(), MapsActivity.class);
        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        b.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        navigateUpTo(b);

    }

    private ArrayList<Modeldp> getMyList(Integer requireddp) {
        ArrayList<Modeldp> myList = new ArrayList<Modeldp>();
        for (int i = 1; i < requireddp + 1; i++) {
            Modeldp m = new Modeldp();
            m.setCapPoint(getIntent().getStringExtra("CapturePoint")); //intentls.putExtra("CapturePoint",
            m.setDataPoint("DataPoint " + String.valueOf(i));
            Cursor data = mDatabaseHelper.getCapdp(getIntent().getStringExtra("CapturePoint"), "DataPoint " + String.valueOf(i));
            data.moveToFirst();
            StringBuilder st = new StringBuilder();
            for (int q = 0; q < data.getCount(); ++q) {
                st.append(data.getString(0) + " ");
                data.moveToNext();
            }
            data.close();
            m.setCount(mDatabaseHelper.getCapdpCount(getIntent().getStringExtra("CapturePoint"), "DataPoint " + String.valueOf(i)));
            m.setDetails(mDatabaseHelper.getCapdpCount(getIntent().getStringExtra("CapturePoint"), "DataPoint " + String.valueOf(i)) + " - " + st.toString());
            m.setBarcode(getIntent().getStringExtra("Barcode"));
          //m.setDetails(mDatabaseHelper.getCapdpCount(getIntent().getStringExtra("CapturePoint"), "DataPoint " + String.valueOf(i)) + "/" + requireddp);
            myList.add(m);
        }
        return myList;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(br);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        unregisterReceiver(br);
    }
}
