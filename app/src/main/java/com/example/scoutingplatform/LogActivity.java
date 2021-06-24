package com.example.scoutingplatform;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {
    Spinner sp;
    DatabaseHelper mDatabasehelper;
    ArrayAdapter<String> dataAdapter;
    ListView lstView;
    private ArrayAdapter<String> dataAdapter2;
    Button btnClear;
    Context ct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabasehelper = new DatabaseHelper(getApplicationContext());
        ct = this;
        setContentView(R.layout.activity_log);
        sp = findViewById(R.id.spinnerlog);
        lstView = findViewById(R.id.lstView);
        btnClear = findViewById(R.id.btnClear);
        Cursor data = mDatabasehelper.getlogstatData();
        data.moveToFirst();
        ArrayList<String> statuscodes = new ArrayList<String>();
        for (int q = 0; q < data.getCount(); ++q) {
            statuscodes.add(data.getString(0));
            data.moveToNext();
        }
        data.close();
        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuscodes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(dataAdapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateList(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnClear.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);
            builder.setTitle("Please confirm");
            builder.setMessage("Are you sure you want to clear the log?");
            builder.setCancelable(true);

            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                mDatabasehelper.deletelogdata();
                onBackPressed();
            });

            builder.setNegativeButton("No", (dialogInterface, i) -> {
            });
            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void populateList(String statuscode) {
        Cursor data2 = mDatabasehelper.getlogDatabystat(statuscode);
        data2.moveToFirst();
        ArrayList<String> listString = new ArrayList<>();
        for (int q = 0; q < data2.getCount(); ++q) {
            StringBuilder st = new StringBuilder();
                st.append("Status:").append(data2.getInt(1));
                st.append(" id: ").append(data2.getInt(2));
            if(!TextUtils.isEmpty(data2.getString(3)))
            {
                st.append(" key: ").append(data2.getString(3));
            }
            if(!TextUtils.isEmpty(data2.getString(4)))
            {
                st.append(" error:").append(data2.getString(4));
            }
                st.append(" code: ").append(data2.getInt(5));
            if(!TextUtils.isEmpty(data2.getString(6)))
            {
                st.append(" source: ").append(data2.getString(6));
            }
            if(!TextUtils.isEmpty(data2.getString(7)))
            {
                st.append(" message: ").append(data2.getString(7));
            }
            listString.add(st.toString());
            data2.moveToNext();
        }
        data2.close();
        dataAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listString);
        lstView.setAdapter(dataAdapter2);
    }
}
