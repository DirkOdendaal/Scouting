package com.example.scoutingplatform;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class RecyclerAdapterDatapointContainer extends RecyclerView.Adapter<Holderdp> {

    private Context context;
    private ArrayList<Modeldp> models;
    private DatabaseHelper mDatabaseHelper;
    private Intent intent;
    private static final String BROADCAST_ACTION = "BROADCAST_ACTION";

    RecyclerAdapterDatapointContainer(Context context, ArrayList<Modeldp> models, Intent intent) {
        this.context = context;
        this.models = models;
        this.intent = intent;
        mDatabaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public Holderdp onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myrecycleritemdp, null);
        return new Holderdp(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holderdp holder, final int position) {
//        if (models.get(position).getCount() > 0) {
//            holder.RelativeLayoutDP.setBackground(ContextCompat.getDrawable(context, R.drawable.edgesgreen));
//        }
//       changeColor(position,holder);
        Log.d("ONBIND", "onBindViewHolder: ");
        if (models.get(position).getDetails().length() > 4) {
            if (models.get(position).getDetails().toLowerCase().contains("none")) {
                holder.RelativeLayoutDP.setBackground(ContextCompat.getDrawable(context, R.drawable.edgesgreen));
            } else {
                holder.RelativeLayoutDP.setBackground(ContextCompat.getDrawable(context, R.drawable.edgessome));
            }
            holder.btnNone.setEnabled(false);
        }
        holder.txtDetailsRecords.setText(models.get(position).getDetails());
        holder.txtDP.setText(models.get(position).getDataPoint());
        holder.btnNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                    if(!(models.get(position).getDetails().length() > 5)) {
                String Description = "None";
                SharedPreferences settings = context.getSharedPreferences("Scouting", 0);
                String Block = settings.getString("BlockName", "");
                Cursor data = mDatabaseHelper.getBlockID(Block);
                data.moveToFirst();
                String Blockid = data.getString(0);
                data.close();
                String ProductionUnit = mDatabaseHelper.getProductionUnit(Block);
                String Subblock = mDatabaseHelper.getsubblock(Block);
                String ScoutingMethod = settings.getString("ScoutingMethod", "");
                String Location = settings.getString("location", "");
                String CapturePoint = models.get(position).getCapPoint();
                String barcode = models.get(position).getBarcode();
                String Datapoint = models.get(position).getDataPoint();
                String Phase = "";
                String Pos = "";
                float Quan = 0f;
                String Sev = "";
                String gender = "";
                UUID idd = java.util.UUID.randomUUID();
                String guid = idd.toString().replace("-", "");
                boolean saved = mDatabaseHelper.addCapData(CapturePoint, gender, ScoutingMethod, Phase, Pos, Location, getCurrentTimeStamp(), ProductionUnit, Block, Subblock, Quan, Sev, Datapoint, Description, guid, "", Blockid, barcode);
                Log.d("cap", "onClick: " + CapturePoint + gender + ScoutingMethod + Phase + Pos + Location + getCurrentTimeStamp() + ProductionUnit + Block + Subblock + Quan + Sev + Datapoint + Description + Blockid);

                if (saved) {
//                            onBindViewHolder(holder,position);
//                            Intent intent = new Intent(BROADCAST_ACTION);
//                            context.sendBroadcast(intent);

                    holder.RelativeLayoutDP.setBackground(ContextCompat.getDrawable(context, R.drawable.edgesgreen));
                    holder.btnNone.setEnabled(false);
                    holder.txtDetailsRecords.setText("1 - None");
                    Log.d("cap", "onClick: Saved");
                } else {
                    Log.d("cap", "onClick: Not saved");
                }
//                    }
//                    else
//                    {
//                        Toast.makeText(context, "Already contains a pest. Cannot be \"None\".", Toast.LENGTH_SHORT).show();
//                    }

            }
        });
        holder.btnViewRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentls = new Intent(context, DataPointActivity.class);
                intentls.putExtra("CapturePoint", models.get(position).getCapPoint());
                intentls.putExtra("DataPoint", models.get(position).getDataPoint());
                intentls.putExtra("Barcode", models.get(position).getBarcode());
                intentls.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intentls);
            }
        });
        //this.notifyDataSetChanged();
    }

    private void changeColor(final int position, Holderdp holder) {

    }

    private static String getCurrentTimeStamp() {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(new Date());
        } catch (Exception ep) {
            Log.d("exeptions", ep.toString());
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
