package com.example.scoutingplatform;

//import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class DataPointActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
//    ExtendedFloatingActionButton fab;
    Button btnAdd;
    Button btnBackDPA;
    RecyclerAdapterdp mAdapter;
    ArrayList<RecyclerAdapterdp> mAdapterList = new ArrayList<>();
    BroadcastReceiver br;
    CheckBox selectall;
    TextView txtCount;
    Button btnDeleteEntries;
    Integer req;
    public final static String BROADCAST_ACTION = "BROADCAST_ACTION";
    private int requiredAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        setContentView(R.layout.test2);
        Context ct = this;





        RecyclerView recyclerView = findViewById(R.id.recyclerDataPoint);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapterdp(this, getAllItems());
        recyclerView.setAdapter(mAdapter);
//        mAdapter.
        txtCount = findViewById(R.id.txtCount);

//        requiredAmount = getIntent().getIntExtra("RequiredDataPoints",0);

//        if(requiredAmount > 0) {
//            Log.d("req", "onCreate: 1 " +req);
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putInt("RequiredDataPoints", requiredAmount);
//            editor.commit();
//
//        }
        SharedPreferences settings = getSharedPreferences("Scouting", 0);
        req = settings.getInt("RequiredDataPoints", 0);
        Log.d("req", "onCreate: 2 " +req);

//        syncDate = settings.getLong("syncDate", 0);
        txtCount.setText(mAdapter.getItemCount() + "/" + req);

        selectall = findViewById(R.id.cbSelectAll);
        selectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectall.isChecked()){
                    mAdapter.selectAll();
                }
                else {
                    mAdapter.unselectall();
                }

            }
        });

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);
        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                mAdapter.swapCursor(getAllItems());
                txtCount.setText(mAdapter.getItemCount() + "/" + req);
            }
        };


        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentls = new Intent(getApplicationContext(), CaptureActivity.class);
                intentls.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Log.d("CPcheck", "dpact: " + getIntent().getStringExtra("CapturePoint"));
                intentls.putExtra("CapturePoint", getIntent().getStringExtra("CapturePoint"));
                intentls.putExtra("Barcode", getIntent().getStringExtra("Barcode"));
                intentls.putExtra("DataPoint", getIntent().getStringExtra("DataPoint"));
                startActivity(intentls);
            }
        });

        btnBackDPA = findViewById(R.id.btnBackDPA);
        btnBackDPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });


        btnDeleteEntries = findViewById(R.id.btnDeleteEntries);
        btnDeleteEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DataPointActivity.this);
                builder.setTitle("Please confirm.");
                builder.setMessage("Are you sure you want to delete these records?");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        for (RecyclerAdapterdp ra :
//                             ) {
//
//                        }
                        mAdapter.DeleteEntry();
                        mAdapter.swapCursor(getAllItems());
                        txtCount.setText("Count: " +mAdapter.getItemCount() );
                        selectall.setChecked(false);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();




                //Toast.makeText(getApplicationContext(),String.valueOf(mAdapter.pos()) ,Toast.LENGTH_SHORT).show();
            }
        });



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

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);
        mAdapter.swapCursor(getAllItems());
        txtCount.setText("Count: " + mAdapter.getItemCount());
//        txtCount.setText(mAdapter.getItemCount() + "/" + req);

    }

    private Cursor getAllItems() {
        try {
            return mDatabaseHelper.getCapDataforCP(getIntent().getStringExtra("CapturePoint"), getIntent().getStringExtra("DataPoint")); //Enter sql query for specific datapoint here
//            getIntent().getStringExtra("CapturePoint")
        } catch (Exception ep) {
            Log.d("exeptions", ep.toString());
            return null;
        }
    }



}
