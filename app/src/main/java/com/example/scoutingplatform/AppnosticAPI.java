package com.example.scoutingplatform;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AppnosticAPI {

    String blockstring = "Annual%20Census/ScoutingBlocks/select.json";
    String blockstringnext = "Annual%20Census/ScoutingBlocks/select.json?skip=500";
    String blockstringnextnext = "Annual%20Census/ScoutingBlocks/select.json?skip=1000";
    String scoutingmethods = "Scouting%20Method/List%20All/select.json";
//340    123122312    https://appnostic.dbflex.net/secure/api/v2/69065/90225A9B19414979BE70DCEDFBCE6E6C
       String postphoto = "phototestscouting/create.json";
    @GET(blockstring)
    Call<List<Block>> getBlocks(@Header("Authorization") String credentials);

    @GET(blockstringnext)
    Call<List<Block>> getNextBlocks(@Header("Authorization") String credentials);

    @GET(blockstringnextnext)
    Call<List<Block>> getNextNextBlocks(@Header("Authorization") String credentials);

    @GET(scoutingmethods)
    Call<List<ScoutingMethods>> getScoutingMethods(@Header("Authorization") String credentials);

//    @POST(postphoto)
//    CAll<St>

//    @Multipart
//    @POST("uploadImage")
//    Call<ResponseBody> uploadImage(@Part MultipartBody.Part image, @Part("image") RequestBody idnumber);
//@Multipart
//@POST(postphoto)
//Call<Obs> updateProfile(@Part MultipartBody.Part file,
//                                 @Part("file") RequestBody name);
//
//    @Multipart
//    @POST(postphoto)
//    Call<ResponseBody> uploadImage(@Part("Photo")RequestBody requestBodyFile, @Part("Photoname") RequestBody requestBodyJson);
//    @Headers({"\"Content-Type\": \"multipart/related\""})

    @Multipart
    @POST(postphoto)
    Call<ResponseBody> upload(@Part MultipartBody.Part image, @Part("Photoname") RequestBody requestBody);



    @POST(postphoto)
    Call<ResponseBody> uploadImage(String jsonstring);


//,@Part("id")RequestBody user


}
