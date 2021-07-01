package com.example.scoutingplatform;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class ProductionUnit {

    @SerializedName("Production Unit Name")
    private String puName;

    @SerializedName("Id")
    private String puID;

    public ProductionUnit(String puName, String puID) {
        this.puName = puName;
        this.puID = puID;
    }

    public String getPuName() {
        return puName;
    }

    public void setPuName(String puName) {
        this.puName = puName;
    }

    public String getPuID() {
        return puID;
    }

    public void setPuID(String puID) {
        this.puID = puID;
    }

    @NonNull
    @Override
    public String toString() {
        return this.puName;
    }
}
