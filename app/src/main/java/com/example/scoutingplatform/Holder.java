package com.example.scoutingplatform;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holder extends RecyclerView.ViewHolder {

    TextView txtCP, txtDetails, txtBlock;
    Button btnView;
    RelativeLayout CPrelativeLayout ;

    public Holder(@NonNull View itemView) {
        super(itemView);

        this.txtCP = itemView.findViewById(R.id.txtCP);
        this.txtDetails = itemView.findViewById(R.id.txtDetails);
        this.txtBlock = itemView.findViewById(R.id.txtBlock);
        this.btnView = itemView.findViewById(R.id.btnViewDP);
        this.CPrelativeLayout = itemView.findViewById(R.id.CPrelativeLayout);

    }
}
