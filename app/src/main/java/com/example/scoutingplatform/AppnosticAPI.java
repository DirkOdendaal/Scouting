package com.example.scoutingplatform;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface AppnosticAPI {

    String scoutingmethods = "Scouting%20Method/List%20All/select.json";

    @GET("Annual%20Census/ScoutingBlocks/select.json")
    Call<List<Block>> getBlocks(@Query("skip") String skip, @Header("Authorization") String credentials);

    @GET(scoutingmethods)
    Call<List<ScoutingMethods>> getScoutingMethods(@Query("skip") String skip, @Header("Authorization") String credentials);
}
