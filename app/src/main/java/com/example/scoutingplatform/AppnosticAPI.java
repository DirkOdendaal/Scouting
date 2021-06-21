package com.example.scoutingplatform;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AppnosticAPI {

    //Change to one url for blocks call, its calling all of the methods. Useless run time to do calls on empty lists on api. It creates a blocking call.
    String blockstring = "Annual%20Census/ScoutingBlocks/select.json";
    String blockstringnext = "Annual%20Census/ScoutingBlocks/select.json?skip=500";
    String blockstringnextnext = "Annual%20Census/ScoutingBlocks/select.json?skip=1000";
    String scoutingmethods = "Scouting%20Method/List%20All/select.json";

    @GET(blockstring)
    Call<List<Block>> getBlocks(@Header("Authorization") String credentials);

    @GET(blockstringnext)
    Call<List<Block>> getNextBlocks(@Header("Authorization") String credentials);

    @GET(blockstringnextnext)
    Call<List<Block>> getNextNextBlocks(@Header("Authorization") String credentials);

    @GET(scoutingmethods)
    Call<List<ScoutingMethods>> getScoutingMethods(@Header("Authorization") String credentials);

}
