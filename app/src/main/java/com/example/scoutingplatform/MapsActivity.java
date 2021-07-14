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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

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
import com.google.maps.android.PolyUtil;

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

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    Location mLastLocation;
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
    Spinner spinnerProd;
    Spinner spinnerblocks;
    Spinner spinnerMethods;
    Switch swtLowSpec;
    BroadcastReceiver brnw;
    TextView txtCount;
    Button btnLogOut;
    long lastsynctime = 0;
    final static int PERMISSION_ALL = 1;
    List<ProductionUnit> puList;
    SharedPreferences userSettings;
    SharedPreferences scoutSettings;
    public final static String BROADCAST_ACTION = "BROADCAST_ACTION";
    BroadcastReceiver br;
    List<Marker> markers = new ArrayList<>();
    LatLng latLng;
    LatLng prevLatlng;
    Location location;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(3);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    Retrofit retrofit;
    AppnosticAPI appnosticAPI;
    Call<List<Block>> blockCall;
    Call<List<ScoutingMethods>> scoutCall;
    List<Block> blocks;
    List<Block> blocksFinal = new ArrayList<>();
    List<ScoutingMethods> methods;
    List<ScoutingMethods> methodsFinal = new ArrayList<>();
    Call<List<ProductionUnit>> prodCall;
    List<ProductionUnit> productionUnits;
    List<ProductionUnit> productionUnitsFinal = new ArrayList<>();
    long skipBlock = 0;
    long skipMethod = 0;
    long skipPU = 0;
    Polygon polygon1;
    LatLng lastpoly;

    private LocationService locationService;
    private MapActivityViewModel mapActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ct = this;
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        userSettings = getSharedPreferences("UserInfo", 0);
        scoutSettings = getSharedPreferences("Scouting", 0);

        checkandrequestPermissions();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            TextView textview_version = findViewById(R.id.txt_version);
            textview_version.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        txtCount = findViewById(R.id.txtCount);
        spinnerhist = findViewById(R.id.spinnerHistory);
        swtLowSpec = findViewById(R.id.swtLowSpec);
        btnLogOut = findViewById(R.id.btnLogOut);
        imgbutTrack = findViewById(R.id.imgbutTrack);
        textViewpddtest = findViewById(R.id.textViewpddtest);
        btnRefresh = findViewById(R.id.btnRefresh);

        swtLowSpec.setChecked(userSettings.getBoolean("LowSpec", false));

        swtLowSpec.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (locationService != null) {
                if (userSettings.getBoolean("LowSpec", false)) {
                    SharedPreferences.Editor editor = userSettings.edit();
                    editor.putBoolean("LowSpec", false);
                    editor.apply();
                    locationService.adjustLocation();
                } else {
                    SharedPreferences.Editor editor = userSettings.edit();
                    editor.putBoolean("LowSpec", true);
                    editor.apply();
                    locationService.adjustLocation();
                }
            }
        });

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFrag != null;
        mapFrag.getMapAsync(this);
        buttonScout = findViewById(R.id.buttonScout);
        buttonScout.setOnClickListener(v -> {

            boolean isBusy = scoutSettings.getBoolean("busy", false);

            if (!isBusy) {

                final View update_layout = getLayoutInflater().inflate(
                        R.layout.selection_dialog, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

                builder.setPositiveButton("Confirm", (dialog, whichButton) -> {

                    if (spinnerblocks.getSelectedItem() == null) {
                        Toast.makeText(this, "Please select a block.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences.Editor editor = scoutSettings.edit();
                    editor.putBoolean("busy", true);
                    editor.apply();
                    Intent intentls = new Intent(getApplicationContext(), CapturePointActivity.class);

                    requireddatapointsAmount = mDatabaseHelper.getRequiredDatapoints(spinnerMethods.getSelectedItem().toString());
                    requiredCapturepointsAmount = mDatabaseHelper.getRequiredCapturepoints(spinnerMethods.getSelectedItem().toString());
                    ForceScan = mDatabaseHelper.getScan(spinnerMethods.getSelectedItem().toString());

                    if (requireddatapointsAmount > 0 && requiredCapturepointsAmount > 0) {
                        SharedPreferences.Editor editor2 = scoutSettings.edit();
                        editor2.putInt("RequiredDataPoints", requireddatapointsAmount);
                        editor2.putInt("RequiredCapturePoints", requiredCapturepointsAmount);
                        editor2.putString("BlockName", spinnerblocks.getSelectedItem().toString());
                        editor2.putString("ProductionUnit", spinnerProd.getSelectedItem().toString());
                        editor2.putString("ScoutingMethod", spinnerMethods.getSelectedItem().toString());
                        UUID idd = UUID.randomUUID();
                        editor2.putString("CapturePoint", idd.toString().replace("-", ""));
                        editor2.putBoolean("ForceScan", ForceScan);

                        editor2.apply();
                        for (int q = 0; q < markers.size(); q++) {
                            markers.get(q).remove();
                        }
                    }

                    intentls.putExtra("ForceScan", ForceScan);
                    startActivity(intentls);
                });
                builder.setNegativeButton("Cancel", (dialog, whichButton) -> {
                });

                spinnerMethods = update_layout.findViewById(R.id.spMethods);
                spinnerProd = update_layout.findViewById(R.id.spProd);
                spinnerblocks = update_layout.findViewById(R.id.spBlocks);
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
                Log.d("BlockName", "onClick: " + Blockname);
                if (!TextUtils.isEmpty(Blockname)) {
                    for (int i = 0; i < adapterb.getCount(); i++) {
                        if (adapterb.getItem(i).contains(Blockname)) {
                            spinnerblocks.setSelection(i);
                        }
                    }
                }

                puList = null;
                try {
                    puList = mDatabaseHelper.getProductionUnits();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                assert puList != null;
                ArrayAdapter<ProductionUnit> adapterPU = new ArrayAdapter<>(MapsActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, puList);

                adapterPU.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProd.setAdapter(adapterPU);

                //TODO-ROBIN Need to set production unit selection here from initial block selection

                spinnerProd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        List<String> blocks = null;
                        try {
                            blocks = mDatabaseHelper.getBlocksByPU(puList.get(position).getPuID());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        assert blocks != null;
                        Collections.sort(blocks);
                        ArrayAdapter<String> adapterb = new ArrayAdapter<>(MapsActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, blocks);

                        adapterb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerblocks.setAdapter(adapterb);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                builder.setView(update_layout);

                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Intent intentls = new Intent(getApplicationContext(), CapturePointActivity.class);

                intentls.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentls);
            }
        });

        //OnClick For track Button
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

        //Long Press for track Button
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

        //Layers Button on Click
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

            SharedPreferences.Editor editor = userSettings.edit();
            editor.putInt("mapstate", mapstate);
            editor.apply();
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
                    SharedPreferences.Editor editor = userSettings.edit();
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
//                    linehis.setPattern(PATTERN_POLYLINE_DOTTED);
                    linehis.setWidth(10);
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

        btnRefresh.setOnClickListener(v -> {
            btnRefresh.setEnabled(false);
            Boolean conn = CheckForConectivity();
            if (!conn) {
                Toast.makeText(getApplicationContext(), "No network found.", Toast.LENGTH_SHORT).show();
            } else {
                buttonScout.setEnabled(false);
                blocksFinal = new ArrayList<>();
                methodsFinal = new ArrayList<>();
                productionUnitsFinal = new ArrayList<>();
                skipBlock = 0;
                skipMethod = 0;
                skipPU = 0;
                buttonScout.setText("Loading Data");
                RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                btnRefresh.startAnimation(rotateAnimation);
                ApiProductionUnits();
                ApiBlocks();
                ApiMethods();
                SyncData();
            }
        });
    }

    private void startLocationService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);

        bindService();
    }

    private void bindService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        bindService(serviceIntent, mapActivityViewModel.getServiceConnection(), Context.BIND_AUTO_CREATE);
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
            lineh = mGoogleMap.addPolyline(plh.color(Color.argb(150, 0, 255, 0)));
//            lineh.setPattern(PATTERN_POLYLINE_DOTTED);
            lineh.setWidth(10);
            lineh.setJointType(JointType.ROUND);
            line = mGoogleMap.addPolyline(pl.color(Color.argb(150, 255, 0, 0)));
//            line.setPattern(PATTERN_POLYLINE_DOTTED);
            line.setWidth(10);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPause() {
        super.onPause();
        Log.d("msgs", "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("STOP", "onStop: ");

        try {
            unregisterReceiver(br);
            unregisterReceiver(brnw);
        } catch (Exception ep) {
            Log.d("exeptions", ep.toString());
        }
        if (mapActivityViewModel.getBinder() != null) {
            try {
                unbindService(mapActivityViewModel.getServiceConnection());
            } catch (IllegalArgumentException err) {
                return;
            }
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

        String Email = userSettings.getString("email", "");
        String Password = userSettings.getString("password", "");
        String DBID = userSettings.getString("DBID", "");
        boolean Authorized = userSettings.getBoolean("Authorized", false);
        Log.d("logindetails", "onResume: " + Email + " " + Password);
        if (Email.equals("") || Password.equals("") || DBID.equals("") || !Authorized) {
            setviewLogin();
        } else {
            setviewFunc();
        }
    }

    private void setviewFunc() {
        changeCountText();

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

                if (CheckForConectivity() && (unixTime - lastsynctime > 30) && !scoutSettings.getBoolean("busy", false)) {
                    lastsynctime = unixTime;
                    Intent mIntent = new Intent(context, PostJobIntentService.class);
                    PostJobIntentService.enqueueWork(context, mIntent, 2);
                }
            }
        };

        brnw.onReceive(getApplicationContext(), this.getIntent());
        IntentFilter netwfilt = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(brnw, netwfilt);

        long syncDate = userSettings.getLong("syncDate", 0);
        long unixTime = System.currentTimeMillis() / 1000L;
        Boolean conn = CheckForConectivity();
        if (conn) {
            if ((unixTime - syncDate) > 1500) {
                blocksFinal = new ArrayList<>();
                methodsFinal = new ArrayList<>();
                productionUnitsFinal = new ArrayList<>();
                skipBlock = 0;
                skipMethod = 0;
                skipPU = 0;
                ApiProductionUnits();
                ApiBlocks();
                ApiMethods();
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
        //Start Location Service
        mapActivityViewModel = ViewModelProviders.of(this).get(MapActivityViewModel.class);
        startLocationService();
        mapActivityViewModel.getBinder().observe(this, myBinder -> {
            if (myBinder != null) {
                Log.d("onChanged", "onMapReady: connected to service");
                locationService = myBinder.getService();
                mapActivityViewModel.setIsUpdating(true);
            } else {
                Log.d("onChanged", "onMapReady: unbound to service");
                locationService = null;
            }
        });

        mapActivityViewModel.getIsUpdating().observe(this, aBoolean -> {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (aBoolean) {
                        if (mapActivityViewModel.getBinder().getValue() != null) {
                            List<Location> locationList = locationService.getLocation();

                            if (locationList != null && locationList.size() > 0) {
                                if (locationList == mLastLocation) {
                                    Log.d("sameLocation", "run: Same Location");
                                }
                                location = locationList.get(locationList.size() - 1);
                                SharedPreferences.Editor editor2 = scoutSettings.edit();
                                editor2.putString("location", location.getLatitude() + "," + location.getLongitude());
                                editor2.apply();
                                if (!hist) {
                                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    if (prevLatlng != null) {
                                        if (prevLatlng == latLng)
                                            Log.d("TAG", "run: lat long Same");
                                    }
                                    if (mLastLocation != null) {

                                        if (line != null) {
                                            line.remove();
                                        }
                                        line = mGoogleMap.addPolyline(pl
                                                .add(latLng)
                                                .color(Color.argb(150, 255, 0, 0)));
//                                        line.setPattern(PATTERN_POLYLINE_DOTTED);
                                        line.setWidth(10);
                                        line.setJointType(JointType.ROUND);

                                    }
                                    if (mLastLocation == null) {
                                        if (!userSettings.getBoolean("LowSpec", false)) {
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

                                                        lineh = mGoogleMap.addPolyline(plh.color(Color.argb(150, 0, 255, 0)));
//                                                        lineh.setPattern(PATTERN_POLYLINE_DOTTED);
                                                        lineh.setWidth(10);
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
                                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 30)); //zoom
                                            imgbutTrack.performLongClick();
                                        }

                                    } else if (track) {
                                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30));
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
                                    Log.d("TAG", "run: History Saved");
                                }
                                mLastLocation = location;
                                prevLatlng = latLng;
                            }
                        } else {
                            Log.d("getUpdateing", "onMapReady: Something is wrong");
                        }
                        handler.postDelayed(this, 1000);
                    }
                }
            };

            if (aBoolean) {
                handler.postDelayed(runnable, 1000);
            }

        });

        mGoogleMap = googleMap;
        mGoogleMap.clear();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);

        }
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

        int mapstte = userSettings.getInt("mapstate", 0);
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

        LatLng Latlong = new LatLng(-29.454571, 24.708960);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Latlong, 5));

        mGoogleMap.clear();
        DrawBlocks();
        AddMarkers();
    }

    private void AddMarkers() {
        try {

            Log.d("Mark", "onResume: ");
            Cursor data = mDatabaseHelper.getCapMarks(scoutSettings.getString("CapturePoint", ""));
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
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();

                    mGoogleMap.setMyLocationEnabled(true);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showDialogOK(
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

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("Camera, Location Services and External Storage Permissions are required for this app.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public void ApiBlocks() {

        retrofit = new Retrofit.Builder()
                .baseUrl("https://appnostic.dbflex.net/secure/api/v2/" + userSettings.getString("DBID", "") + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        appnosticAPI = retrofit.create(AppnosticAPI.class);
        blockCall = appnosticAPI.getBlocks(String.valueOf(skipBlock), Credentials.basic(userSettings.getString("email", ""), userSettings.getString("password", "")));
        blockCall.enqueue(new Callback<List<Block>>() {
            @Override
            public void onResponse(@NonNull Call<List<Block>> blockcall, Response<List<Block>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.body() != null) {
                    blocks = response.body();
                    if (blocks.size() == 500) {
                        skipBlock += 500;
                        blocksFinal.addAll(blocks);
                        ApiBlocks();
                    } else {
                        blocksFinal.addAll(blocks);
                        mDatabaseHelper.deleteBlockData();
                        mDatabaseHelper.addBlockData(blocksFinal);
                        DrawBlocks();
                        Toast.makeText(ct, "Updated Blocks", Toast.LENGTH_SHORT).show();
                        buttonScout.setEnabled(true);
                        btnRefresh.setEnabled(true);
                        buttonScout.setText("Scout");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Block>> blockcall, Throwable t) {
                Log.d("failBlockCall", "onFailure: " + t);
            }
        });

        long unixTime = System.currentTimeMillis() / 1000L;
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putLong("syncDate", unixTime);

        editor.apply();
    }

    public void ApiProductionUnits() {


        retrofit = new Retrofit.Builder()
                .baseUrl("https://appnostic.dbflex.net/secure/api/v2/" + userSettings.getString("DBID", "") + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        appnosticAPI = retrofit.create(AppnosticAPI.class);

        prodCall = appnosticAPI.getProductionUnits(String.valueOf(skipPU), Credentials.basic(userSettings.getString("email", ""), userSettings.getString("password", "")));
        prodCall.enqueue(new Callback<List<ProductionUnit>>() {
            @Override
            public void onResponse(Call<List<ProductionUnit>> prodCall, Response<List<ProductionUnit>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.body() != null) {
                    productionUnits = response.body();
                    if (productionUnits.size() == 500) {
                        skipPU += 500;
                        productionUnitsFinal.addAll(productionUnits);
                        ApiProductionUnits();
                    } else {
                        productionUnitsFinal.addAll(productionUnits);
                        mDatabaseHelper.deletePUData();
                        mDatabaseHelper.addPUData(productionUnitsFinal);
                        Toast.makeText(ct, "Updated ProductionUnits", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductionUnit>> call, Throwable t) {
                Log.d("RESPONSES", "onFailure: " + t);
            }
        });

        long unixTime = System.currentTimeMillis() / 1000L;
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putLong("syncDate", unixTime);

        editor.apply();
    }

    public void ApiMethods() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://appnostic.dbflex.net/secure/api/v2/" + userSettings.getString("DBID", "") + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        appnosticAPI = retrofit.create(AppnosticAPI.class);

        scoutCall = appnosticAPI.getScoutingMethods(String.valueOf(skipMethod), Credentials.basic(userSettings.getString("email", ""), userSettings.getString("password", "")));
        scoutCall.enqueue(new Callback<List<ScoutingMethods>>() {
            @Override
            public void onResponse(Call<List<ScoutingMethods>> scoutcall, Response<List<ScoutingMethods>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.body() != null) {
                    methods = response.body();
                    if (methods.size() == 500) {
                        skipMethod += 500;
                        methodsFinal.addAll(methods);
                        ApiMethods();
                    } else {
                        methodsFinal.addAll(methods);
                        mDatabaseHelper.deleteMethods();
                        mDatabaseHelper.addScoutingMethods(methodsFinal);
                        Toast.makeText(ct, "Updated Methods", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ScoutingMethods>> scoutcall, Throwable t) {
                Log.d("RESPONSES", "onFailure: " + t);
            }
        });

        long unixTime = System.currentTimeMillis() / 1000L;
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putLong("syncDate", unixTime);

        editor.apply();
    }

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
                        for (String separateCoord : SeparateCoords) {
                            String[] latlong = separateCoord.split(",");
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
        return activeNetwork != null;
    }

    public void postmanUpload() {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
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
            end = Math.min(end, message.length());
            android.util.Log.d(TAG, "number" + i + " " + message.substring(start, end));
        }
    }

    //change to Retrofit Request on AppnosticAPI
    public void SyncData() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        Request request = new Request.Builder()
                .url("https://appnostic.dbflex.net/secure/api/v2/" + userSettings.getString("DBID", "") + "/Scouting%20PDDD%20setup/Default%20View/select.json")
                .header("Authorization", Credentials.basic(userSettings.getString("email", ""), userSettings.getString("password", "")))
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
                            int intAskForTrap = 0;
                            if (p.getAskforTrap()) {
                                intAskForTrap = 1;
                            }
                            mDatabaseHelper.addPDDDData(p.getDescription(), intsaskforgender, intmesurmentType, p.getScoutingMethods(), p.getPhases(), p.getPossiblepestlocations(), intAskForTrap);
                            //Toast.makeText(getApplicationContext(), "PDDD Updated.", Toast.LENGTH_SHORT).show();
                        } catch (Exception err) {
                            Log.d("DB", "onResponse: " + err);
                        }
                    }
                }
            }
        });
    }
}


