package com.example.scoutingplatform;

public class Modeldp {

    private String DataPoint;
    private String CapPoint;
    private String Details;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    private String barcode;
    private Long count;

    public Long getCount() {return count;}

    public String getCapPoint() {
        return CapPoint;
    }

    public String getDetails() {return Details;}

    public String getDataPoint() {return DataPoint;}

    public void setCount(Long count) { this.count = count;}

    public void setCapPoint(String capPoint) {CapPoint = capPoint;}

    public void setDetails(String details) {Details = details;}

    public void setDataPoint(String dataPoint) {DataPoint = dataPoint;}

}
