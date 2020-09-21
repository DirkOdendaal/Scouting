package com.example.scoutingplatform;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class PDDDSetup implements Serializable{

    @SerializedName("Description")
    private String Description;

    @SerializedName("Ask for Gender")
    private Boolean AskforGender;

    @SerializedName("Measurement Type")
    private Boolean MeasurementType;

    @SerializedName("Scouting Methods")
    private String ScoutingMethods;

    @SerializedName("Phases")
    private String Phases;


    @SerializedName("Possible pest locations")
    private String Possiblepestlocations;


    public String getPossiblepestlocations() {
        return Possiblepestlocations;
    }

    public void setPossiblepestlocations(String possiblepestlocations) {
        Possiblepestlocations = possiblepestlocations;
    }



    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Boolean getAskforGender() {
        return AskforGender;
    }

    public void setAskforGender(Boolean askforGender) {
        AskforGender = askforGender;
    }

    public Boolean getMeasurementType() {
        return MeasurementType;
    }

    public void setMeasurementType(Boolean measurementType) {
        MeasurementType = measurementType;
    }

    public String getScoutingMethods() {
        return ScoutingMethods;
    }

    public void setScoutingMethods(String scoutingMethods) {
        ScoutingMethods = scoutingMethods;
    }

    public String getPhases() {
        return Phases;
    }

    public void setPhases(String phases) {
        Phases = phases;
    }




}
