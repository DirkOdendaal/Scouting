package com.example.scoutingplatform;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.http.POST;

public class PostJobIntentService extends JobIntentService {
    private static final String BROADCAST_ACTION = "BROADCAST_ACTION";
    private static final String TAG = "POSTJOBINTENTSERVICE";

    DatabaseHelper mDatabaseHelper;
    static final int JOB_ID = 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service1", "create");
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        // mDatabaseHelper.delDebug();
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //toast("onHandle");
        Log.d("service1", "start onhandle");
        try {
            if (CheckForConectivity()) {
                POSTDATA();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ArrayList<RecordClass> PostItems = new ArrayList<RecordClass>();
    ArrayList<RecordClass> PostItemsBULK = new ArrayList<RecordClass>();
    int actives = 0;
    String bulkWhereto;
    String LastWhereto = "";
    int retries = 0;
    int maxretries = 0;
    int newdescount;
    boolean tryagain = true;

    public void POSTDATA() throws IOException {

        PostItems = new ArrayList<RecordClass>();
        if (CheckForConectivity()) {
            Cursor data = mDatabaseHelper.getCapData();
            data.moveToFirst();
            for (int q = 0; q < data.getCount(); ++q) {
                final RecordClass postItem = new RecordClass();
                postItem.setID(data.getInt(0));
                String CapturePoint = "";
                String Gender = "";
                String ScoutingMethod = "";
                String Phase = "";
                String PestLocation = "";
                String Location = "";
                String Timestamp = "";
                String ProductionUnit = "";
                String Block = "";
                String SubBlock = "";
                String Quantity = "";
                String Severity = "";
                String DataPoint = "";
                String PestDescription = "";
                String GUID = "";
                String BlockID = "";
                String barcode = "";

                if (!TextUtils.isEmpty(data.getString(1))) {
                    CapturePoint = "\"Capture Point\":" + "\"" + data.getString(1) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(2))) {
                    Gender = "\"Gender\":" + "\"" + data.getString(2) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(3))) {
                    ScoutingMethod = "\"Scouting Method\":" + "\"" + data.getString(3) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(4))) {
                    Phase = "\"Phase\":" + "\"" + data.getString(4) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(5))) {
                    PestLocation = "\"Pest Location\":" + "\"" + data.getString(5) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(6))) {
                    Location = "\"Location\":" + "\"" + data.getString(6) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(7))) {
                    Timestamp = "\"Timestamp\":" + "\"" + data.getString(7).replace(' ', 'T') + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(8))) {
                    ProductionUnit = "\"Production Unit\":" + "\"" + data.getString(8) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(9))) {
                    Block = "\"Block\":" + "\"" + data.getString(9) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(10))) {
                    SubBlock = "\"Sub-Block\":" + "\"" + data.getString(10) + "\",";
                }

                if (data.getFloat(11) != 0f) {
                    Quantity = "\"Quantity\":" + "\"" + String.valueOf(data.getFloat(11)) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(12))) {
                    Severity = "\"Severity\":" + "\"" + data.getString(12) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(13))) {
                    DataPoint = "\"Data Point\":" + "\"" + data.getString(13) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(14))) {
                    PestDescription = "\"Pest Description\":" + "\"" + data.getString(14) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(15))) {
                    GUID = "\"Id\":" + "\"" + data.getString(15) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(18))) {
                    BlockID = "\"BlockID\":" + "\"" + data.getString(18) + "\",";
                }

                if (!TextUtils.isEmpty(data.getString(19))) {
                    barcode = "\"Scanned Field\":" + "\"" + data.getString(19) + "\",";
                }


                postItem.setJSon("{" + CapturePoint + Gender + ScoutingMethod + Phase + PestLocation + Location + Timestamp + ProductionUnit + Block + SubBlock + Quantity + Severity + DataPoint + PestDescription + GUID +BlockID+ barcode + "}");
                Log.d(TAG, "JSON: " + postItem.getJSon());
                PostItems.add(postItem);

                data.moveToNext();
            }
            data.close();

            if (retries == 0) {
                maxretries = PostItems.size() / 100;
            }
            if (PostItems.size() > 0) {
                PostItemsBULK = new ArrayList<RecordClass>();

                for (int b = 0; b < PostItems.size(); ++b) {
                    if (PostItemsBULK.size() < 100) {
                        PostItemsBULK.add(PostItems.get(b));
                    }
                }
                retries++;
                doCallBulk(PostItemsBULK);
            } else {
                POSTPHOTOS();
            }

        }

    }

    private void POSTPHOTOS() throws IOException {
        Cursor data = mDatabaseHelper.getCapDataImages();
        data.moveToFirst();
        for (int q = 0; q < data.getCount(); ++q) {
            String CapturePoint = "";
            String Gender = "";
            String ScoutingMethod = "";
            String Phase = "";
            String PestLocation = "";
            String Location = "";
            String Timestamp = "";
            String ProductionUnit = "";
            String Block = "";
            String SubBlock = "";
            String Quantity = "";
            String Severity = "";
            String DataPoint = "";
            String PestDescription = "";
            String GUID = "";
            String ImagePath = "";
            String BlockID = "";
            String barcode = "";
            if (!TextUtils.isEmpty(data.getString(1))) {
                CapturePoint = data.getString(1);
            }

            if (!TextUtils.isEmpty(data.getString(2))) {
                Gender = data.getString(2);
            }

            if (!TextUtils.isEmpty(data.getString(3))) {
                ScoutingMethod = data.getString(3);
            }

            if (!TextUtils.isEmpty(data.getString(4))) {
                Phase = data.getString(4);
            }

            if (!TextUtils.isEmpty(data.getString(5))) {
                PestLocation = data.getString(5);
            }

            if (!TextUtils.isEmpty(data.getString(6))) {
                Location = data.getString(6);
            }

            if (!TextUtils.isEmpty(data.getString(7))) {
                Timestamp = data.getString(7).replace(' ', 'T');
            }

            if (!TextUtils.isEmpty(data.getString(8))) {
                ProductionUnit = data.getString(8);
            }

            if (!TextUtils.isEmpty(data.getString(9))) {
                Block = data.getString(9);
            }

            if (!TextUtils.isEmpty(data.getString(10))) {
                SubBlock = data.getString(10);
            }

            if (data.getFloat(11) != 0f) {
                Quantity = String.valueOf(data.getFloat(11));
            }

            if (!TextUtils.isEmpty(data.getString(12))) {
                Severity = data.getString(12);
            }

            if (!TextUtils.isEmpty(data.getString(13))) {
                DataPoint = data.getString(13);
            }

            if (!TextUtils.isEmpty(data.getString(14))) {
                PestDescription = data.getString(14);
            }

            if (!TextUtils.isEmpty(data.getString(15))) {
                GUID = data.getString(15);
            }
            if (!TextUtils.isEmpty(data.getString(17))) {
                ImagePath = data.getString(17);
            }
            if (!TextUtils.isEmpty(data.getString(18))) {
                BlockID = data.getString(18);
            }
            if (!TextUtils.isEmpty(data.getString(19))) {
                barcode = data.getString(19);
            }
            photoUpload( CapturePoint,  Gender, ScoutingMethod, Phase, PestLocation, Location,  Timestamp,  ProductionUnit,  Block,  SubBlock,  Quantity,  Severity,  DataPoint,  PestDescription,  GUID,  ImagePath, BlockID, barcode);

            data.moveToNext();
        }
        data.close();

        toast("Records synced.");

    }


    private void doCallBulk(ArrayList<RecordClass> postItemsblk) throws IOException {

        if (CheckForConectivity()) {
            StringBuilder blkjson = new StringBuilder();
            Log.d("vars", "POSTitembulksize: " + postItemsblk.size());
            blkjson.append("[");
            for (int b = 0; b < postItemsblk.size(); ++b) {
                blkjson.append(postItemsblk.get(b).getJSon()).append(",");
            }
            blkjson.append("]");
            Log.d("RESPONSES", "BULKJSON: " + blkjson.toString());
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), blkjson.toString());
//            SharedPreferences settings = getSharedPreferences("UserInfo", 0);


            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .build();
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            okhttp3.Request request = new okhttp3.Request.Builder()
//                    .url("https://appnostic.dbflex.net/secure/api/v2/" + settings.getString("DBID", "") + "/" + bulkWhereto + "/create.json")
                    .url("https://appnostic.dbflex.net/secure/api/v2/" + settings.getString("DBID", "") + "/Scouting%20Data/upsert.json")
                    .header("Authorization", Credentials.basic(settings.getString("email", ""), settings.getString("password", "")))
                    .post(body)
                    .addHeader("Content-Type", " application/json")
//                    .addHeader("Authorization", Credentials.basic(settings.getString("email", ""), settings.getString("password", "")))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                try {
                    ArrayList<ApiResp> responses = new ArrayList<>();
                    JSONArray array = new JSONArray(response.body().string());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Log.d(TAG, "doCallBulk: JObject " + object);
                        int status = 0;
                        int id = 0;
                        Integer code = 0;
                        String key = "";
                        String error = "";
                        String source = "";
                        String message = "";
                        if (object.has("errors")) {
                            JSONArray er = object.getJSONArray("errors");
                            JSONObject errors = (JSONObject) er.get(0);
                            Log.d(TAG, "doCallBulk: errorsarr = " + errors.getInt("error") + " " + errors.getInt("code") + " " + errors.getString("source") + " " + errors.getString("message"));

                            if (errors.has("status")) {
                                status = errors.getInt("status");
                            }
                            if (errors.has("id")) {
                                id = errors.getInt("id");
                            }
                            if (errors.has("key")) {
                                key = errors.getString("key");
                            }
                            if (errors.has("error")) {
                                error = errors.getString("error");
                            }
                            if (errors.has("code")) {
                                code = errors.getInt("code");
                            }
                            if (errors.has("source")) {
                                source = errors.getString("source");
                            }
                            if (errors.has("message")) {
                                message = errors.getString("message");
                            }
                        } else {
                            if (object.has("status")) {
                                status = object.getInt("status");
                            }
                            if (object.has("id")) {
                                id = object.getInt("id");
                            }
                            if (object.has("key")) {
                                key = object.getString("key");
                            }
                        }
                        Log.d(TAG, "doCallBulk: status:" + status + " id:" + id + " key:" + key + " error:" + error + " code:" + code + " source:" + source + " " + message);
                        ApiResp apiresp = new ApiResp(status, id, key, error, code, source, message);
                        responses.add(apiresp);

                    }
                    mDatabaseHelper.addLog(responses);
                    for (int q = 0; q < responses.size(); q++) {
                        Log.d(TAG, "doCallBulk: CHECK " + q + "/" + responses.size());
                        if ((responses.get(q).getStatus() < 400 && responses.get(q).getStatus() >= 200) && (!TextUtils.isEmpty(responses.get(q).getKey()))) {
                            Log.d(TAG, "doCallBulk: CHECK" + responses.get(q).getKey());
                            mDatabaseHelper.flagCaptured(responses.get(q).getKey());

                        }
                    }
                    Intent intent = new Intent(BROADCAST_ACTION);
                    sendBroadcast(intent);
                    toast("Records synced.");

                } catch (JSONException | IOException e) {
                    Log.d(TAG, "doCallBulk: " + e.toString());
                }


            }else
            {
                String resp1 = response.body().string();
                Log.d("RESPONSES", "FAILED: " + resp1);
                if(resp1.contains("No such user"))
                {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("Authorized", false);
                    editor.commit();
                }
            }

            if (retries <= maxretries) {
                POSTDATA();
            }
        }
    }

    public void photoUpload(String CapturePoint, String Gender, String ScoutingMethod, String Phase, String PestLocation, String Location, String Timestamp, String ProductionUnit, String Block, String SubBlock, String Quantity, String Severity, String DataPoint, String PestDescription, String GUID, String ImagePath, String BlockID, String barcode) throws IOException { //
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("photo", GUID + ".jpg",
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(Objects.requireNonNull(ImagePath))))
                .addFormDataPart("Capture Point", CapturePoint)
                .addFormDataPart("Gender", Gender)
                .addFormDataPart("Scouting Method", ScoutingMethod)
                .addFormDataPart("Phase", Phase)
                .addFormDataPart("Pest Location", PestLocation)
                .addFormDataPart("Location", Location)
                .addFormDataPart("Timestamp", Timestamp)
                .addFormDataPart("Production Unit", ProductionUnit)
                .addFormDataPart("Block", Block)
                .addFormDataPart("Sub-Block", SubBlock)
                .addFormDataPart("Quantity", Quantity)
                .addFormDataPart("Severity", Severity)
                .addFormDataPart("Data Point", DataPoint)
                .addFormDataPart("Id", GUID)
                .addFormDataPart("Pest Description", PestDescription)
                .addFormDataPart("BlockID", BlockID)
                .addFormDataPart("Scanned Field", barcode)
                .build();
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        Request request = new Request.Builder()
                .url("https://appnostic.dbflex.net/secure/api/v2/" + settings.getString("DBID", "") + "/Scouting%20Data/upsert.json")
                .header("Authorization", Credentials.basic(settings.getString("email", ""), settings.getString("password", "")))
                .method("POST", body)
                .build();
        Log.d("okRESPONSE", "postmanUpload: build comp");
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            try {
                ArrayList<ApiResp> responses = new ArrayList<>();
                JSONArray array = new JSONArray(response.body().string());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Log.d(TAG, "photoUpload: JObject " + object);
                    int status = 0;
                    int id = 0;
                    Integer code = 0;
                    String key = "";
                    String error = "";
                    String source = "";
                    String message = "";
                    if (object.has("errors")) {
                        JSONArray er = object.getJSONArray("errors");
                        JSONObject errors = (JSONObject) er.get(0);
                        Log.d(TAG, "photoUpload: errorsarr = " + errors.getInt("error") + " " + errors.getInt("code") + " " + errors.getString("source") + " " + errors.getString("message"));

                        if (errors.has("status")) {
                            status = errors.getInt("status");
                        }
                        if (errors.has("id")) {
                            id = errors.getInt("id");
                        }
                        if (errors.has("key")) {
                            key = errors.getString("key");
                        }
                        if (errors.has("error")) {
                            error = errors.getString("error");
                        }
                        if (errors.has("code")) {
                            code = errors.getInt("code");
                        }
                        if (errors.has("source")) {
                            source = errors.getString("source");
                        }
                        if (errors.has("message")) {
                            message = errors.getString("message");
                        }
                    } else {
                        if (object.has("status")) {
                            status = object.getInt("status");
                        }
                        if (object.has("id")) {
                            id = object.getInt("id");
                        }
                        if (object.has("key")) {
                            key = object.getString("key");
                        }
                    }
                    Log.d(TAG, "photoUpload: status:" + status + " id:" + id + " key:" + key + " error:" + error + " code:" + code + " source:" + source + " " + message);
                    ApiResp apiresp = new ApiResp(status, id, key, error, code, source, message);
                    responses.add(apiresp);

                }
                mDatabaseHelper.addLog(responses);
                for (int q = 0; q < responses.size(); q++) {
                    Log.d(TAG, "photoUpload: CHECK " + q + "/" + responses.size());
                    if ((responses.get(q).getStatus() < 400 && responses.get(q).getStatus() >= 200) && (!TextUtils.isEmpty(responses.get(q).getKey()))) {
                        Log.d(TAG, "photoUpload: CHECK" + responses.get(q).getKey());
                        mDatabaseHelper.flagCaptured(responses.get(q).getKey());

                    }
                }
                Intent intent = new Intent(BROADCAST_ACTION);
                sendBroadcast(intent);
//                toast("Records synced.");

            } catch (JSONException | IOException e) {
                Log.d(TAG, "photoUpload: " + e.toString());
            }

        }
    }


    private Boolean CheckForConectivity() {
        Log.d("service1", "checkcon");
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void enqueueWork(Context context, Intent intent, int number) {
        Log.d("service1", "enq " + intent.hashCode() + " number: " + number);
        enqueueWork(context, PostJobIntentService.class, JOB_ID, intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("service1", "destroy");
        try {
        } catch (Exception e) {
            Log.d("DebugEx", "doCallBulk: " + e.toString());
        }
    }

    final Handler mHandler = new Handler();

    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PostJobIntentService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
