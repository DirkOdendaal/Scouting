package com.example.scoutingplatform;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class CapturePointActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    RecyclerView recyclerView;
    RecyclerAdaptercp myAdapter;
    String BlockName;
    String CapturePoint;
    Button btnFin;
    Button btnCPBack;
    Context ct;
    Boolean forceScan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseHelper = new DatabaseHelper(this);
        ct = this;
        //recyclerView = findViewById(R.id.recy);
        SharedPreferences settings = getSharedPreferences("Scouting", 0);
        Integer reqdp = settings.getInt("RequiredDataPoints", 0);
        Integer reqcp = settings.getInt("RequiredCapturePoints", 0);
        BlockName = settings.getString("BlockName", "");
        forceScan = settings.getBoolean("ForceScan", false);
//        forceScan = getIntent().getBooleanExtra("ForceScan", false);
        Log.d("FORCESCAN", "onCreate: " + forceScan);

        setContentView(R.layout.activity_capture);
        recyclerView = findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new RecyclerAdaptercp(this, getMyList(reqcp,reqdp));
        recyclerView.setAdapter(myAdapter);
        btnFin = findViewById(R.id.btnFin);
        btnFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);
                builder.setTitle("Please confirm");
                builder.setMessage("Are you sure you have finished scouting?");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences settings = getSharedPreferences("Scouting", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("busy", false);
                        editor.apply();
                        Intent mIntent = new Intent(getApplicationContext(), PostJobIntentService.class);
                        PostJobIntentService.enqueueWork(getApplicationContext(), mIntent,3);
                        onBackPressed();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        btnCPBack = findViewById(R.id.btnCPBack);
        btnCPBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences("Scouting", 0);
        Integer reqdp = settings.getInt("RequiredDataPoints", 0);
        Integer reqcp = settings.getInt("RequiredCapturePoints", 0);
        CapturePoint = settings.getString("CapturePoint", "");
        myAdapter = new RecyclerAdaptercp(this, getMyList(reqcp,reqdp));
        myAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(myAdapter);
    }

    private ArrayList<Model> getMyList(Integer requiredcp, Integer requireddp) {
        ArrayList<Model> myList = new ArrayList<Model>();

        for (int i = 1; i < requiredcp +1; i++) {
            Model m = new Model();
            m.setForceScan(forceScan);
            m.setCPID("Tree " + String.valueOf(i));
            m.setDetails(mDatabaseHelper.getCapCount(BlockName + " - " + i + " - " + CapturePoint) + " / " + String.valueOf(requireddp));
            m.setCount(mDatabaseHelper.getCapCount(BlockName + " - " + i + " - " + CapturePoint));
            m.setBarcode(mDatabaseHelper.getbarcode(BlockName + " - " + i + " - " + CapturePoint));
            m.setCountneeded(requireddp);
            m.setBlockName(BlockName);
            m.setCP(BlockName + " - " + i + " - " +  CapturePoint  );
            Log.d("CPcheck", "getMyList: " +BlockName + " - " + i);
            myList.add(m);
        }
        return myList;
    }


}
