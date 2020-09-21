package com.example.scoutingplatform;

import com.google.gson.annotations.SerializedName;

public class Block {
    @SerializedName("Id")
    private int rowId;

    @SerializedName("Scouting Block Display Name")
    private String  BlockNo;
//
//    @SerializedName("Block Block No")
//    private String  BlockNo;

    @SerializedName("Pushed Co-ords")
    private String  PushedCoords;

    @SerializedName("Production Unit")
    private String  Pucid;


    @SerializedName("Block Id")
    private String  blockid;

    public int getRowId() {
        return rowId;
    }

    public String getBlockNo() {
        return BlockNo;
    }

    public String getPushedCoords() {
        return PushedCoords;
    }

    public String getPucid() {
        return Pucid;
    }

    public String getBlockid() {
        return blockid;
    }

}
