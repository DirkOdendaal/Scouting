package com.example.scoutingplatform;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;


public class LocationService extends Service {

    //Location Stuff
    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;

    //Binder
    private IBinder mBinder = new MyBinder();

    //Variables
    private Boolean lowSpec;
    List<Location> locationList;

    //Settings
    SharedPreferences userSettings;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        createNotificationChannel();
//
//        Intent mIntent = new Intent(this, MapsActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
//
//        Notification notification = new NotificationCompat.Builder(this, "channel")
//                .setContentTitle("FarmTrace Scouting")
//                .setContentText("Scouting")
//                .setSmallIcon(R.drawable.pin)
//                .setContentIntent(pendingIntent).build();
//
//        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startLocation();
    }

    private void startLocation() {
        adjustLocation();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    public void adjustLocation() {
        userSettings = getSharedPreferences("UserInfo", 0);
        lowSpec = userSettings.getBoolean("LowSpec", false);
        if (!lowSpec) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(3000);
            mLocationRequest.setFastestInterval(1);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(15000);
            mLocationRequest.setFastestInterval(10000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    public class MyBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            if (locationResult == null) {
                return;
            }
            locationList = locationResult.getLocations();
            Log.d("locationService", "onLocationResult: "+ locationList);
        }
    };

    //Notification Channel
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("channel", "ForegroundNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public List<Location> getLocation() {
        return locationList;
    }
}
