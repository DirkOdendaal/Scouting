package com.example.scoutingplatform;

public class Model {

    public String getCPID() {
        return CPID;
    }

    public void setCPID(String CPID) {
        this.CPID = CPID;
    }

    public boolean isForceScan() {
        return forceScan;
    }

    public void setForceScan(boolean forceScan) {
        this.forceScan = forceScan;
    }

    public boolean forceScan;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String barcode;
    private String CPID;
    private String CP;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    private Long count;

    public Integer getCountneeded() {
        return Countneeded;
    }

    public void setCountneeded(Integer countneeded) {
        Countneeded = countneeded;
    }

    private Integer Countneeded;

    public String getBlockName() {
        return BlockName;
    }

    public void setBlockName(String blockName) {
        BlockName = blockName;
    }

    private String BlockName;

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    private String Details;

    public String getCP() {
        return CP;
    }

    public void setCP(String CP) {
        this.CP = CP;
    }


}
