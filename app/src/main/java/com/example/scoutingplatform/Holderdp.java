package com.example.scoutingplatform;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holderdp extends RecyclerView.ViewHolder {

    TextView txtDP, txtDetailsRecords ;
    Button btnViewRecords, btnNone;
    RelativeLayout RelativeLayoutDP;

    public Holderdp(@NonNull View itemView) {
        super(itemView);

        this.txtDP = itemView.findViewById(R.id.txtDP);
        this.txtDetailsRecords = itemView.findViewById(R.id.txtDetailsRecords);
        this.btnViewRecords = itemView.findViewById(R.id.btnViewRecords);
        this.btnNone = itemView.findViewById(R.id.btnNone);
        this.RelativeLayoutDP = itemView.findViewById(R.id.RelativeLayoutDP);
    }
}
