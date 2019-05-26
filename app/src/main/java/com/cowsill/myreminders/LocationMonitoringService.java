package com.cowsill.myreminders;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationMonitoringService extends Service {

    private static final String TAG = "LocationMonitoringServ";
    FusedLocationProviderClient mLocationProviderClient;
    LocationRequest mLocationRequest;
    LocationMonitoringThread mLocationMonitoringThread;

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.i(TAG, "onLocationResult: " + locationResult.getLastLocation().toString());
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(Constants.LOCATION_SERVICE_ID, createNotification());
        getLocationUpdates();
        return START_STICKY;
    }

    private void getLocationUpdates() {

        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
        mLocationMonitoringThread = new LocationMonitoringThread();
        mLocationMonitoringThread.start();

    }

    private LocationRequest createLocationRequest() {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(15000);
        request.setFastestInterval(5000);
        return request;
    }

    private Notification createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.LOCATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_location_and_geofence)
                .setContentTitle(getString(R.string.my_reminders))
                .setContentText(getString(R.string.location_notification))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class LocationMonitoringThread extends Thread{

        Looper looper;

        @Override
        public void run() {
            // We already checked for permissions in the launcher activity
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED){
                return;
            }

            looper = Looper.myLooper();
            looper.prepare();
            mLocationProviderClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    looper
            );
            looper.loop();
        }
    }
}
