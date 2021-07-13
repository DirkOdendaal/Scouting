package com.example.scoutingplatform;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapActivityViewModel extends ViewModel {

    private MutableLiveData<Boolean> mIsUpdatingLocation = new MutableLiveData<>();
    private MutableLiveData<LocationService.MyBinder> mBinder = new MutableLiveData<>();


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("serviceConnection", "onServiceConnected: connected to service");
            LocationService.MyBinder binder = (LocationService.MyBinder) service;
            mBinder.postValue(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder.postValue(null);
        }
    };

    public LiveData<Boolean> getIsUpdating() {
        return mIsUpdatingLocation;
    }

    public LiveData<LocationService.MyBinder> getBinder() {
        return mBinder;
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public void setIsUpdating(Boolean up){
        mIsUpdatingLocation.postValue(up);
    }

}
