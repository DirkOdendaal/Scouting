package com.example.scoutingplatform;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import java.util.ArrayList;

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
        try {
            super.onCreate(savedInstanceState);
            mDatabaseHelper = new DatabaseHelper(this);
            ct = this;
            SharedPreferences settings = getSharedPreferences("Scouting", 0);
            Integer reqdp = settings.getInt("RequiredDataPoints", 0);
            Integer reqcp = settings.getInt("RequiredCapturePoints", 0);
            BlockName = settings.getString("BlockName", "");
            forceScan = settings.getBoolean("ForceScan", false);

            setContentView(R.layout.activity_capture);
            recyclerView = findViewById(R.id.recy);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            myAdapter = new RecyclerAdaptercp(this, getMyList(reqcp, reqdp));
            recyclerView.setAdapter(myAdapter);
            btnFin = findViewById(R.id.btnFin);

            btnFin.setOnClickListener(v -> {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);
                builder.setTitle("Please confirm");
                builder.setMessage("Are you sure you have finished scouting?");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                    SharedPreferences settings1 = getSharedPreferences("Scouting", 0);
                    SharedPreferences.Editor editor = settings1.edit();
                    editor.putBoolean("busy", false);
                    editor.apply();
                    Intent mIntent = new Intent(getApplicationContext(), PostJobIntentService.class);
                    PostJobIntentService.enqueueWork(getApplicationContext(), mIntent, 3);
                    onBackPressed();
                });

                builder.setNegativeButton("No", (dialogInterface, i) -> {
                });
                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();
            });
            btnCPBack = findViewById(R.id.btnCPBack);
            btnCPBack.setOnClickListener(v -> onBackPressed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            SharedPreferences settings = getSharedPreferences("Scouting", 0);
            Integer reqdp = settings.getInt("RequiredDataPoints", 0);
            Integer reqcp = settings.getInt("RequiredCapturePoints", 0);
            CapturePoint = settings.getString("CapturePoint", "");
            myAdapter = new RecyclerAdaptercp(this, getMyList(reqcp, reqdp));
            myAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(myAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Model> getMyList(Integer requiredcp, Integer requireddp) {
        ArrayList<Model> myList = new ArrayList<>();
        try {
            for (int i = 1; i < requiredcp + 1; i++) {
                Model m = new Model();
                m.setForceScan(forceScan);
                m.setCPID("Tree " + i);
                m.setDetails(mDatabaseHelper.getCapCount(BlockName + " - " + i + " - " + CapturePoint) + " / " + requireddp);
                m.setCount(mDatabaseHelper.getCapCount(BlockName + " - " + i + " - " + CapturePoint));
                m.setBarcode(mDatabaseHelper.getbarcode(BlockName + " - " + i + " - " + CapturePoint));
                m.setCountneeded(requireddp);
                m.setBlockName(BlockName);
                m.setCP(BlockName + " - " + i + " - " + CapturePoint);
                myList.add(m);
            }
            return myList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myList;
    }
}
