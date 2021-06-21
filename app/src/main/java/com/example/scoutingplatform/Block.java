package com.example.scoutingplatform;

import com.google.gson.annotations.SerializedName;

public class Block {
    @SerializedName("Id")
    private final int rowId;

    @SerializedName("Scouting Block Display Name")
    private final String  BlockNo;

    @SerializedName("Pushed Co-ords")
    private final String  PushedCoords;

    @SerializedName("Production Unit")
    private final String  Pucid;

    @SerializedName("Block Id")
    private final String  blockid;

    public Block(int rowId, String blockNo, String pushedCoords, String pucid, String blockid) {
        this.rowId = rowId;
        this.BlockNo = blockNo;
        this.PushedCoords = pushedCoords;
        this.Pucid = pucid;
        this.blockid = blockid;
    }

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
