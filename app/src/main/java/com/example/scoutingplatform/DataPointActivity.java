package com.example.scoutingplatform;

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

public class DataPointActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    Button btnAdd;
    Button btnBackDPA;
    RecyclerAdapterdp mAdapter;
    BroadcastReceiver br;
    CheckBox selectall;
    TextView txtCount;
    Button btnDeleteEntries;
    Integer req;
    public final static String BROADCAST_ACTION = "BROADCAST_ACTION";

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
        txtCount = findViewById(R.id.txtCount);

        SharedPreferences settings = getSharedPreferences("Scouting", 0);
        req = settings.getInt("RequiredDataPoints", 0);
        Log.d("req", "onCreate: 2 " +req);

        txtCount.setText(mAdapter.getItemCount() + "/" + req);

        selectall = findViewById(R.id.cbSelectAll);
        selectall.setOnClickListener(v -> {
            if(selectall.isChecked()){
                mAdapter.selectAll();
            }
            else {
                mAdapter.unselectall();
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
        btnAdd.setOnClickListener(v -> {
            Intent intentls = new Intent(getApplicationContext(), CaptureActivity.class);
            Log.d("CPcheck", "dpact: " + getIntent().getStringExtra("CapturePoint"));
            intentls.putExtra("CapturePoint", getIntent().getStringExtra("CapturePoint"));
            intentls.putExtra("Barcode", getIntent().getStringExtra("Barcode"));
            intentls.putExtra("DataPoint", getIntent().getStringExtra("DataPoint"));
            startActivity(intentls);
        });

        btnBackDPA = findViewById(R.id.btnBackDPA);
        btnBackDPA.setOnClickListener(v -> onBackPressed());

        btnDeleteEntries = findViewById(R.id.btnDeleteEntries);
        btnDeleteEntries.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(DataPointActivity.this);
            builder.setTitle("Please confirm.");
            builder.setMessage("Are you sure you want to delete these records?");
            builder.setCancelable(true);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mAdapter.DeleteEntry();
                    mAdapter.swapCursor(getAllItems());
                    txtCount.setText("Count: " +mAdapter.getItemCount() );
                    selectall.setChecked(false);
                }
            });

            builder.setNegativeButton("No", (dialogInterface, i) -> {
            });
            AlertDialog dialog = builder.create();
            dialog.show();
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
    }

    private Cursor getAllItems() {
        try {
            return mDatabaseHelper.getCapDataforCP(getIntent().getStringExtra("CapturePoint"), getIntent().getStringExtra("DataPoint")); //Enter sql query for specific datapoint here
        } catch (Exception ep) {
            Log.d("exeptions", ep.toString());
            return null;
        }
    }
}
