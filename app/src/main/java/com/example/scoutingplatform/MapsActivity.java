package com.example.scoutingplatform;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.google.maps.android.PolyUtil;

import retrofit2.Retrofit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    DatabaseHelper mDatabaseHelper;
    int mapstate;
    FrameLayout sbs;
    BottomSheetBehavior bottomSheetBehavior;
    Button buttonScout;
    ImageButton btnRefresh;
    Button btnPost;
    boolean track = false;
    boolean locked = false;
    ImageButton imgbutTrack;
    PolylineOptions pl = new PolylineOptions();
    PolylineOptions plh = new PolylineOptions();
    PolylineOptions plhis = new PolylineOptions();
    Polyline line;
    Spinner spinnerhist;
    private ArrayAdapter<String> Adapterhist;
    Boolean hist = false;
    Polyline lineh;
    Polyline linehis;
    TextView textViewpddtest;
    List<PDDDSetup> posts;
    Button btnClearHistory;
    Context ct;
    ArrayList<Polygon> polygons = new ArrayList<>();
    String Blockname = "";
    private int requireddatapointsAmount = 0;
    private int requiredCapturepointsAmount = 0;
    private boolean ForceScan;
    Spinner spinnerblocks;
    Spinner spinnerMethods;
    Switch swtLowSpec;
    BroadcastReceiver brnw;
    TextView txtCount;
    Button btnLogOut;
    long lastsynctime = 0;
    final static int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ct = this;
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        checkandrequestPermissions();

        txtCount = findViewById(R.id.txtCount);
        spinnerhist = findViewById(R.id.spinnerHistory);
        swtLowSpec = findViewById(R.id.swtLowSpec);
        btnLogOut = findViewById(R.id.btnLogOut);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        swtLowSpec.setChecked(settings.getBoolean("LowSpec", false));
        swtLowSpec.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences settings12 = getSharedPreferences("UserInfo", 0);
            if (settings12.getBoolean("LowSpec", false)) {
                SharedPreferences.Editor editor = settings12.edit();
                editor.putBoolean("LowSpec", false);
                editor.apply();
                mLocationRequest.setInterval(3000);
                mLocationRequest.setFastestInterval(2000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            } else {
                SharedPreferences.Editor editor = settings12.edit();
                editor.putBoolean("LowSpec", true);
                editor.apply();
                mLocationRequest.setInterval(15000);
                mLocationRequest.setFastestInterval(14000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            }
            if (mFusedLocationClient != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mGoogleMap.setMyLocationEnabled(true);
                }
            } else {
                assert mFusedLocationClient != null;
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            }
            Log.d("Lowspec", "onCheckedChanged: " + settings12.getBoolean("LowSpec", false));
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFrag != null;
        mapFrag.getMapAsync(this);
        buttonScout = findViewById(R.id.buttonScout);
        buttonScout.setOnClickListener(v -> {

            SharedPreferences settings13 = getSharedPreferences("Scouting", 0);
            boolean isBusy = settings13.getBoolean("busy", false);

            if (!isBusy) {

                final View update_layout = getLayoutInflater().inflate(
                        R.layout.selection_dialog, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);


                builder.setPositiveButton("Confirm", (dialog, whichButton) -> {
                    SharedPreferences settings1 = getSharedPreferences("Scouting", 0);
                    SharedPreferences.Editor editor = settings1.edit();
                    editor.putBoolean("busy", true);
                    editor.apply();
                    Intent intentls = new Intent(getApplicationContext(), CapturePointActivity.class);


                    requireddatapointsAmount = mDatabaseHelper.getRequiredDatapoints(spinnerMethods.getSelectedItem().toString());
                    requiredCapturepointsAmount = mDatabaseHelper.getRequiredCapturepoints(spinnerMethods.getSelectedItem().toString());
                    ForceScan = mDatabaseHelper.getScan(spinnerMethods.getSelectedItem().toString());
                    SharedPreferences settings2 = getSharedPreferences("Scouting", 0);

                    if (requireddatapointsAmount > 0 && requiredCapturepointsAmount > 0) {
                        SharedPreferences.Editor editor2 = settings2.edit();
                        editor2.putInt("RequiredDataPoints", requireddatapointsAmount);
                        editor2.putInt("RequiredCapturePoints", requiredCapturepointsAmount);
                        editor2.putString("BlockName", spinnerblocks.getSelectedItem().toString());
                        editor2.putString("ScoutingMethod", spinnerMethods.getSelectedItem().toString());
                        UUID idd = UUID.randomUUID();
                        editor2.putString("CapturePoint", idd.toString().replace("-", ""));
                        editor2.putBoolean("ForceScan", ForceScan);

                        editor2.apply();
                        for (int q = 0; q < markers.size(); q++) {
                            markers.get(q).remove();
                        }
                    }

                    intentls.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intentls.putExtra("ForceScan", ForceScan);
                    Log.d("FORCESCAN", "mapsact: " + ForceScan);
                    startActivity(intentls);
                });
                builder.setNegativeButton("Cancel", (dialog, whichButton) -> {
                });

                spinnerMethods = (Spinner) update_layout.findViewById(R.id.spMethods);
                spinnerblocks = (Spinner) update_layout.findViewById(R.id.spBlocks);
                List<String> methods = null;
                try {
                    methods = mDatabaseHelper.getMethods();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MapsActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, methods);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMethods.setAdapter(adapter);

                List<String> blocks = null;
                try {
                    blocks = mDatabaseHelper.getBlocks();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                assert blocks != null;
                Collections.sort(blocks);
                ArrayAdapter<String> adapterb = new ArrayAdapter<>(MapsActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, blocks);

                adapterb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerblocks.setAdapter(adapterb);
                Log.d("Blockname", "onClick: " + Blockname);
                if (!TextUtils.isEmpty(Blockname)) {
                    for (int i = 0; i < adapterb.getCount(); i++) {
                        if (adapterb.getItem(i).contains(Blockname)) {
                            spinnerblocks.setSelection(i);
                        }
                    }
                }
                builder.setView(update_layout);

                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Intent intentls = new Intent(getApplicationContext(), CapturePointActivity.class);

                intentls.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentls);
            }
        });

        imgbutTrack = findViewById(R.id.imgbutTrack);
        imgbutTrack.setOnClickListener(v -> {
            if (!locked)
                if (track) {
                    track = false;
                    imgbutTrack.setImageDrawable(getDrawable(R.drawable.recenter3));
                    mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);

                } else {
                    track = true;
                    imgbutTrack.setImageDrawable(getDrawable(R.drawable.recenteractive));

                }
        });


        imgbutTrack.setOnLongClickListener(v -> {
            if (!locked) {
                mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
                imgbutTrack.setImageDrawable(getDrawable(R.drawable.recenterlocked));
                track = true;
                locked = true;
            } else {
                mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
                imgbutTrack.setImageDrawable(getDrawable(R.drawable.recenteractive));
                track = false;
                locked = false;
            }


            return false;
        });


        ImageButton btn1 = findViewById(R.id.imgbutLayers);
        btn1.setOnClickListener(v -> {
            switch (mGoogleMap.getMapType()) {
                case GoogleMap.MAP_TYPE_SATELLITE:
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mapstate = 1;
                    break;
                case GoogleMap.MAP_TYPE_TERRAIN:
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    mapstate = 2;
                    break;
                case GoogleMap.MAP_TYPE_HYBRID:
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    mapstate = 3;
                    break;
                case GoogleMap.MAP_TYPE_NORMAL:
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    mapstate = 4;
                    break;

            }
            SharedPreferences settings14 = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings14.edit();
            editor.putInt("mapstate", mapstate);
            editor.apply();
            Log.d("mapstate", "onPause: ");
        });

        Button btnphoto = findViewById(R.id.buttonphoto);
        btnphoto.setOnClickListener(v -> {
            try {
                postmanUpload();
            } catch (Exception err) {
                err.printStackTrace();
            }
        });

        btnPost = findViewById(R.id.btnPost);
        btnPost.setOnClickListener(v -> {
            Intent mIntent = new Intent(getApplicationContext(), PostJobIntentService.class);
            PostJobIntentService.enqueueWork(getApplicationContext(), mIntent, 3);
        });
        Button btnLog = findViewById(R.id.btnLogs);
        btnLog.setOnClickListener(v -> {
            Intent intentlog = new Intent(getApplicationContext(), LogActivity.class);
            intentlog.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intentlog);
        });


        Button btninbx = findViewById(R.id.btnInbox);
        btninbx.setOnClickListener(v -> {
        });
        btnClearHistory = findViewById(R.id.btnClearHistory);
        btnClearHistory.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);
            builder.setTitle("Please confirm");
            builder.setMessage("Are you sure you want to clear your location history?");
            builder.setCancelable(true);

            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                mDatabaseHelper.deleteHistoryData();
                onBackPressed();
            });

            builder.setNegativeButton("No", (dialogInterface, i) -> {
            });
            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();
        });

        btnLogOut.setOnClickListener(v -> new AlertDialog.Builder(MapsActivity.this)
                .setTitle("Log Out")
                .setMessage("Are you sure you wish to log out?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    SharedPreferences settings15 = getSharedPreferences("UserInfo", 0);
                    SharedPreferences.Editor editor = settings15.edit();
                    editor.putString("email", "");
                    editor.putString("password", "");
                    editor.putString("DBID", "");
                    editor.putBoolean("Authorized", false);
                    editor.apply();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    setviewLogin();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());

        Button btnHist = findViewById(R.id.btnHistory);
        btnHist.setOnClickListener(v -> {
            btnClearHistory.setVisibility(View.VISIBLE);

            if (line != null) {
                line.remove();
            }
            if (lineh != null) {
                lineh.remove();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mGoogleMap.setMyLocationEnabled(false);
                }
            }

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            buttonScout.setVisibility(View.GONE);
            spinnerhist.setVisibility(View.VISIBLE);
            hist = true;
            Cursor Hdata = mDatabaseHelper.getLocationHistory();
            Hdata.moveToFirst();
            ArrayList<String> histDates = new ArrayList<>();
            for (int q = 0; q < Hdata.getCount(); ++q) {
                histDates.add(Hdata.getString(2));
                Hdata.moveToNext();
            }
            Hdata.close();
            Adapterhist = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, histDates);
            Adapterhist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerhist.setAdapter(Adapterhist);
            spinnerhist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    plhis = new PolylineOptions();
                    if (linehis != null) {
                        linehis.remove();
                    }
                    LatLng lastlt = new LatLng(mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude);
                    String Coords = mDatabaseHelper.getLocationHistorytoday(parent.getSelectedItem().toString());
                    String[] sp = Coords.split(";");
                    for (String s :
                            sp) {
                        String[] qs = s.split(",");
                        LatLng lt = new LatLng(Double.parseDouble(qs[0]), Double.parseDouble(qs[1]));
                        plhis.add(lt);
                        lastlt = lt;
                    }
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastlt, mGoogleMap.getCameraPosition().zoom));
                    linehis = mGoogleMap.addPolyline(plhis
                            .color(Color.argb(255, 0, 0, 255)));
                    linehis.setPattern(PATTERN_POLYLINE_DOTTED);
                    linehis.setJointType(JointType.ROUND);
                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        });


        sbs = findViewById(R.id.standardBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(sbs);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        bottomSheetBehavior.setPeekHeight(height / 10);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        final ImageButton imgb = findViewById(R.id.imageButton1);
        imgb.setImageDrawable(getDrawable(R.drawable.uparroww));
        sbs.getBackground().setAlpha(0);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheet.getBackground().setAlpha((int) (slideOffset * 255));
                if (slideOffset > 0.75f) {
                    imgb.setImageDrawable(getDrawable(R.drawable.downarroww));

                } else if (slideOffset > 0.25f) {
                    imgb.setImageDrawable(getDrawable(R.drawable.linew));

                } else {
                    imgb.setImageDrawable(getDrawable(R.drawable.uparroww));
                }
            }
        });
        imgb.setOnClickListener(v -> {

            switch (bottomSheetBehavior.getState()) {
                case BottomSheetBehavior.STATE_COLLAPSED:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    break;
                case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    break;
            }
        });
        textViewpddtest = findViewById(R.id.textViewpddtest);


        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(v -> {
            Boolean conn = CheckForConectivity();
            if (!conn) {

                Toast.makeText(getApplicationContext(), "No network found.", Toast.LENGTH_SHORT).show();
            } else {
                RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                btnRefresh.startAnimation(rotateAnimation);
                ApiBlocks();
                SyncData();
            }
        });
    }

    private void checkandrequestPermissions() {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (hist) {
            mGoogleMap.setMyLocationEnabled(true);
            buttonScout.setVisibility(View.VISIBLE);
            spinnerhist.setVisibility(View.GONE);
            btnClearHistory.setVisibility(View.GONE);
            lineh = mGoogleMap.addPolyline(plh
                    .color(Color.argb(150, 0, 255, 0)));
            lineh.setPattern(PATTERN_POLYLINE_DOTTED);
            lineh.setJointType(JointType.ROUND);
            line = mGoogleMap.addPolyline(pl
                    .color(Color.argb(150, 255, 0, 0)));
            line.setPattern(PATTERN_POLYLINE_DOTTED);
            line.setJointType(JointType.ROUND);
            if (linehis != null) {
                linehis.remove();
            }
            hist = false;
        } else {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Please confirm");
            builder.setMessage("Are you want to exit the app?");
            builder.setCancelable(true);

            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });

            builder.setNegativeButton("No", (dialogInterface, i) -> {
            });
            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("msgs", "onPause: ");

        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(br);
            unregisterReceiver(brnw);
        } catch (Exception ep) {
            Log.d("exeptions", ep.toString());
        }
        Log.d("msgs", "onStop: ");
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        String Email = settings.getString("email", "");
        String Password = settings.getString("password", "");
        String DBID = settings.getString("DBID", "");
        boolean Authorized = settings.getBoolean("Authorized", false);
        Log.d("logindetails", "onResume: " + Email + " " + Password);
        if (Email.equals("") || Password.equals("") || DBID.equals("") || !Authorized) {
            setviewLogin();
        } else {
            setviewFunc();
        }
    }

    public final static String BROADCAST_ACTION = "BROADCAST_ACTION";
    BroadcastReceiver br;

    private void setviewFunc() {
        changeCountText();
        if (mFusedLocationClient != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }
            }
        }

        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                changeCountText();

            }
        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);

        brnw = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                long unixTime = System.currentTimeMillis() / 1000L;
                SharedPreferences settings = getSharedPreferences("Scouting", 0);
                if (CheckForConectivity() && (unixTime - lastsynctime > 30) && !settings.getBoolean("busy", false)) {
                    lastsynctime = unixTime;
                    Intent mIntent = new Intent(context, PostJobIntentService.class);
                    PostJobIntentService.enqueueWork(context, mIntent, 2);

                }
            }
        };
        brnw.onReceive(getApplicationContext(), this.getIntent());
        IntentFilter netwfilt = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(brnw, netwfilt);

        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        long syncDate = settings.getLong("syncDate", 0);
        long unixTime = System.currentTimeMillis() / 1000L;
        Boolean conn = CheckForConectivity();
        if (conn) {
            if ((unixTime - syncDate) > 1500) {
                ApiBlocks();
                SyncData();
            }
        }
        if (mGoogleMap != null) {
            AddMarkers();
        }
    }

    private void changeCountText() {
        long mcount = mDatabaseHelper.getCapMAcount();
        Log.d("COUNT", "changeCountText: " + mcount);
        if (mcount > 0) {
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setText("Total: " + mcount);
        } else {
            txtCount.setVisibility(View.GONE);
        }
    }

    private void setviewLogin() {
        Intent intentlog = new Intent(this, LoginActivity.class);
        intentlog.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intentlog);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.clear();
        Log.d("checks", "onMapReady: ");
        mGoogleMap.setOnPolygonClickListener(polygonClick -> {
            if (polygonClick.getTag() != null)
                Toast.makeText(MapsActivity.this, polygonClick.getTag().toString(), Toast.LENGTH_LONG).show();
        });
        LatLngBounds RSA = new LatLngBounds(
                new LatLng(-34.343817, 18.235683), new LatLng(-16.738000, 36.777786));
        mGoogleMap.setLatLngBoundsForCameraTarget(RSA);
        mGoogleMap.setMinZoomPreference(5);
        UiSettings mapUiSettings = googleMap.getUiSettings();
        mapUiSettings.setZoomControlsEnabled(false);
        mapUiSettings.setCompassEnabled(true);
        mapUiSettings.setMyLocationButtonEnabled(false);
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        int mapstte = settings.getInt("mapstate", 0);
        switch (mapstte) {
            default:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 2:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 3:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case 4:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }
        mLocationRequest = new LocationRequest();
        if (!settings.getBoolean("LowSpec", false)) {
            mLocationRequest.setInterval(3000);
            mLocationRequest.setFastestInterval(2000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else {
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(9000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }

        LatLng Latlong = new LatLng(-29.454571, 24.708960);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Latlong, 5));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
        mGoogleMap.clear();
        DrawBlocks();
        AddMarkers();
    }

    List<Marker> markers = new ArrayList<>();

    private void AddMarkers() {
        try {

            Log.d("Mark", "onResume: ");
            SharedPreferences settings2 = getSharedPreferences("Scouting", 0);
            Cursor data = mDatabaseHelper.getCapMarks(settings2.getString("CapturePoint", ""));
            data.moveToFirst();
            ArrayList<LatLng> ltlngs = new ArrayList<>();
            for (int q = 0; q < data.getCount(); ++q) {
                String s = data.getString(0);
                Log.d("Mark", "onResume: " + s);
                String[] st = s.split(",");
                ltlngs.add(new LatLng(Double.parseDouble(st[0]), Double.parseDouble(st[1])));

                data.moveToNext();
            }
            data.close();

            for (LatLng lt :
                    ltlngs) {
                Log.d("Mark", "foreach: " + lt.latitude + "," + lt.longitude);
                MarkerOptions mrk = new MarkerOptions().position(lt);
                Marker m = mGoogleMap.addMarker(mrk);

                markers.add(m);
            }
        } catch (Exception err) {
            Log.d("EXCEPTIONS", "onResume: " + err);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        unregisterReceiver(brnw);
        unregisterReceiver(br);
    }

    LatLng latLng;
    Location location;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                location = locationList.get(locationList.size() - 1);
                SharedPreferences settings2 = getSharedPreferences("Scouting", 0);
                SharedPreferences.Editor editor2 = settings2.edit();
                editor2.putString("location", location.getLatitude() + "," + location.getLongitude());
                editor2.apply();
                if (!hist) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (mLastLocation != null) {
                        LatLng lastlatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        if (line != null) {
                            line.remove();
                        }
                        line = mGoogleMap.addPolyline(pl
                                .add(latLng)//, lastlatLng
                                .color(Color.argb(150, 255, 0, 0)));
                        line.setPattern(PATTERN_POLYLINE_DOTTED);
                        line.setJointType(JointType.ROUND);

                    }
                    SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                    if (mLastLocation == null) {
                        if (!settings.getBoolean("LowSpec", false)) {
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20), 4500, new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {
                                    try {
                                        Date c = Calendar.getInstance().getTime();
                                        DateFormat df = SimpleDateFormat.getDateInstance();
                                        String formattedDate = df.format(c);
                                        String Coords = mDatabaseHelper.getLocationHistorytoday(formattedDate);
                                        String[] sp = Coords.split(";");
                                        for (String s :
                                                sp) {
                                            String[] qs = s.split(",");
                                            LatLng lt = new LatLng(Double.parseDouble(qs[0]), Double.parseDouble(qs[1]));
                                            plh.add(lt);
                                        }

                                        lineh = mGoogleMap.addPolyline(plh
                                                .color(Color.argb(150, 0, 255, 0)));
                                        lineh.setPattern(PATTERN_POLYLINE_DOTTED);
                                        lineh.setJointType(JointType.ROUND);

                                    } catch (Exception err) {
                                        Log.d("errors", "onFinish: " + err.toString());
                                    }

                                    imgbutTrack.performLongClick();
                                }

                                @Override
                                public void onCancel() {
                                }
                            });
                        } else {
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
                            imgbutTrack.performLongClick();
                        }

                    } else if (track) {
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, mGoogleMap.getCameraPosition().zoom));
                        for (int i = 0; i < polygons.size(); i++) {
                            if (PolyUtil.containsLocation(latLng, polygons.get(i).getPoints(), false)) {
                                if (polygons.get(i).getTag() != null) {
                                    Blockname = Objects.requireNonNull(polygons.get(i).getTag()).toString();

                                    polygons.get(i).setFillColor(Color.argb(150, 0, 0, 255));
                                }
                            } else {
                                polygons.get(i).setFillColor(Color.argb(80, 0, 255, 64));
                            }
                        }
                    }
                }
                if (mLastLocation == null || (location.getLongitude() != mLastLocation.getLongitude() && location.getLatitude() != mLastLocation.getLatitude())) {
                    saveLocationHistory();
                }
                mLastLocation = location;
            }
        }
    };

    private void saveLocationHistory() {
        Date c = Calendar.getInstance().getTime();

        DateFormat df = SimpleDateFormat.getDateInstance();
        String formattedDate = df.format(c);
        String Coords = mDatabaseHelper.getLocationHistorytoday(formattedDate);

        if (!TextUtils.isEmpty(Coords)) {
            switch (Coords) {
                case "non-existant":
                    mDatabaseHelper.addLocationHistory(formattedDate);
                    break;
                case "error":
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.e("Error", "saveLocationHistory: ", null);
                    break;
                default:
                    mDatabaseHelper.appendLocationHistory(Coords + ";" + location.getLatitude() + "," + location.getLongitude(), formattedDate);
                    break;
            }
        } else {
            mDatabaseHelper.appendLocationHistory(location.getLatitude() + "," + location.getLongitude(), formattedDate);
        }
    }

    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(3);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_ALL) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "camera & location & write services permission granted");
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mGoogleMap.setMyLocationEnabled(true);
                } else {
                    Log.d("PERMISSIONS", "Some permissions are not granted ask again ");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showDialogOK("Camera, Location Services and External Storage Permissions are required for this app.",
                                (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            checkandrequestPermissions();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    Retrofit retrofit;
    AppnosticAPI appnosticAPI;
    Call<List<Block>> blockcall;
    Call<List<ScoutingMethods>> Scoutcall;
    long blockcount = 0;


    //Changes these calls to one method and one url with a count on the skip. Make recursive method. All 3 calls calling is using runtime.
    public void ApiBlocks() {
        mDatabaseHelper.deleteBlockData();
        blockcount = 0;
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://appnostic.dbflex.net/secure/api/v2/" + settings.getString("DBID", "") + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        appnosticAPI = retrofit.create(AppnosticAPI.class);
        blockcall = appnosticAPI.getBlocks(Credentials.basic(settings.getString("email", ""), settings.getString("password", "")));
        blockcall.enqueue(new Callback<List<Block>>() {
            @Override
            public void onResponse(Call<List<Block>> blockcall, Response<List<Block>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.body() != null) {
                    List<Block> blocks = response.body();
                    mDatabaseHelper.addBlockData(blocks);
                    DrawBlocks();
                }
            }

            @Override
            public void onFailure(Call<List<Block>> blockcall, Throwable t) {
            }
        });


        blockcall = appnosticAPI.getNextBlocks(Credentials.basic(settings.getString("email", ""), settings.getString("password", "")));
        blockcall.enqueue(new Callback<List<Block>>() {
            @Override
            public void onResponse(Call<List<Block>> blockcall, Response<List<Block>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.body() != null) {
                    List<Block> blocks = response.body();
                    mDatabaseHelper.addBlockData(blocks);
                    DrawBlocks();
                }
            }

            @Override
            public void onFailure(Call<List<Block>> blockcall, Throwable t) {
                Log.d("RESPONSES", "onFailure: " + t);
            }
        });

        blockcall = appnosticAPI.getNextNextBlocks(Credentials.basic(settings.getString("email", ""), settings.getString("password", "")));
        blockcall.enqueue(new Callback<List<Block>>() {
            @Override
            public void onResponse(Call<List<Block>> blockcall, Response<List<Block>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.body() != null) {
                    List<Block> blocks = response.body();
                    mDatabaseHelper.addBlockData(blocks);
                    DrawBlocks();
                }
            }

            @Override
            public void onFailure(Call<List<Block>> blockcall, Throwable t) {
            }
        });

        Scoutcall = appnosticAPI.getScoutingMethods(Credentials.basic(settings.getString("email", ""), settings.getString("password", "")));
        Scoutcall.enqueue(new Callback<List<ScoutingMethods>>() {
            @Override
            public void onResponse(Call<List<ScoutingMethods>> scoutcall, Response<List<ScoutingMethods>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.body() != null) {
                    mDatabaseHelper.deleteMethods();
                    List<ScoutingMethods> ScoutingMethods = response.body();
                    mDatabaseHelper.addScoutingMethods(ScoutingMethods);
                }
            }

            @Override
            public void onFailure(Call<List<ScoutingMethods>> scoutcall, Throwable t) {
                Log.d("RESPONSES", "onFailure: " + t);
            }
        });

        long unixTime = System.currentTimeMillis() / 1000L;
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("syncDate", unixTime);
        Toast.makeText(getApplicationContext(), "Updated.", Toast.LENGTH_SHORT).show();

        editor.apply();
    }

    Polygon polygon1;
    LatLng lastpoly;

    public void DrawBlocks() {

        try {
            Cursor data = mDatabaseHelper.getBlockData();
            data.moveToFirst();
            long count = data.getCount();
            for (int q = 0; q < count; ++q) {
                String Coords = data.getString(2);
                String blockname = data.getString(1);
                data.moveToNext();
                if (Coords != null) {
                    if (Coords.startsWith("p")) {
                        Coords = Coords.substring(8);
                        Coords = Coords.replace("_", "");
                        Coords = Coords.replace(" ", "");
                        String[] SeparateCoords = Coords.split(";");
                        PolygonOptions Polygon1 = new PolygonOptions()
                                .clickable(true);
                        for (int i = 0; i < SeparateCoords.length; i++) {
                            String[] latlong = SeparateCoords[i].split(",");
                            double latitude = Double.parseDouble(latlong[0]);
                            double longitude = Double.parseDouble(latlong[1]);
                            LatLng polyCoords = new LatLng(latitude, longitude);
                            Polygon1.add(polyCoords);
                            Polygon1.fillColor(Color.argb(80, 0, 255, 64));
                            lastpoly = polyCoords;
                        }
                        polygon1 = mGoogleMap.addPolygon(Polygon1);
                        polygon1.setPoints(Polygon1.getPoints());
                        polygon1.setTag(blockname);
                        polygon1.isClickable();
                    }

                    if (polygon1 != null) {
                        polygons.add(polygon1);
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    private Boolean CheckForConectivity() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return false;
        } else {
            return true;
        }
    }

    public void postmanUpload() {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("Photo", "/storage/emulated/0/Download/Img1.jpg",
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File("/storage/emulated/0/Download/Img1.jpg")))
                .addFormDataPart("Photoname", "myimg2")
                .build();
        Request request = new Request.Builder()
                .url("https://appnostic.dbflex.net/secure/api/v2/69065/90225A9B19414979BE70DCEDFBCE6E6C/phototestscouting/create.json")
                .method("POST", body)
                .addHeader("Cookie", "ARRAffinity=acf1e0b0911bcb68c16a85769d1346f4a80b7a1dcc3446a31df494f7c3da7d1f")
                .build();
        Log.d("okRESPONSE", "postmanUpload: build comp");
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("okRESPONSE", "postmanUpload: " + e.toString());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                Log.d("okRESPONSE", "postmanUpload: " + response.toString());
            }
        });
    }

    public static void d(String TAG, String message) {
        int maxLogSize = 2000;
        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            android.util.Log.d(TAG, "number" + i + " " + message.substring(start, end));
        }
    }

    public void SyncData() {
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        Request request = new Request.Builder()
                .url("https://appnostic.dbflex.net/secure/api/v2/" + settings.getString("DBID", "") + "/Scouting%20PDDD%20setup/Default%20View/select.json")
                .header("Authorization", Credentials.basic(settings.getString("email", ""), settings.getString("password", "")))
                .method("GET", null)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("RESPONSES", "SyncData: FAILED");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful() && (response.body() != null ? response.body().toString().length() : 0) > 2) {
                    String jsonOutput = response.body().string();
                    Type listType = new TypeToken<List<PDDDSetup>>() {
                    }.getType();
                    Gson gson = new Gson();
                    posts = gson.fromJson(jsonOutput, listType);
                    mDatabaseHelper.deletePDDDData();
                    for (final PDDDSetup p : posts) {
                        try {
                            Log.d("PDDD", "onResponse: " + p.getDescription());
                            int intsaskforgender = 0;
                            if (p.getAskforGender()) {
                                intsaskforgender = 1;
                            }
                            int intmesurmentType = 0;
                            if (p.getMeasurementType()) {
                                intmesurmentType = 1;
                            }
                            mDatabaseHelper.addPDDDData(p.getDescription(), intsaskforgender, intmesurmentType, p.getScoutingMethods(), p.getPhases(), p.getPossiblepestlocations());
                            Toast.makeText(getApplicationContext(), "PDDD Updated.", Toast.LENGTH_SHORT).show();
                        } catch (Exception err) {
                            Log.d("DB", "onResponse: " + err);
                        }
                    }
                }
            }
        });
    }
}


