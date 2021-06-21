package com.example.scoutingplatform;

import com.google.gson.annotations.SerializedName;

class ScoutingMethods {
    @SerializedName("@row.id")
    private int rowId;

    @SerializedName("Description")
    private String Description;

    @SerializedName("Amount of Data Points")
    private Integer AmountofDataPoints;


    @SerializedName("Force Scan Field")
    private boolean ForceScanField;

    @SerializedName("Amount of Capture Points")
    private Integer AmountofCapturePoints;

    public Integer getAmountofDataPoints() {
        return AmountofDataPoints;
    }

    public void setAmountofDataPoints(Integer amountofDataPoints) {
        AmountofDataPoints = amountofDataPoints;
    }

    public Integer getAmountofCapturePoints() {
        return AmountofCapturePoints;
    }

    public void setAmountofCapturePoints(Integer amountofCapturePoints) {
        AmountofCapturePoints = amountofCapturePoints;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public long isForceScanField() {
        if (ForceScanField){
            return 1;
        }
        else
        {
            return 0;
        }
    }
}
