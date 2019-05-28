package com.cowsill.myreminders;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class GeofenceManager {

    ArrayList<MyReminder> mReminderList;
    GeofencingClient mGeofencingClient;
    static ArrayList<Geofence> mGeofenceList;
    PendingIntent mGeofencePendingIntent;
    Context mContext;

    public GeofenceManager(Context context, ArrayList<MyReminder> reminderList) {

        mContext = context;
        mReminderList = reminderList;
    }

    public void createGeofences() {

        mGeofencingClient = LocationServices.getGeofencingClient(mContext);

        // When the app is first created, the list will be initialized but with a size of 0
        if(mReminderList.size() == 0){
            return;
        }

        if(mGeofenceList == null) {
            mGeofenceList = new ArrayList<>();
        }
        for (MyReminder reminder : mReminderList) {
            makeGeofence(reminder); // populating mGeofenceList
        }

        // We check permissions in the launcher activity

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGeofencingClient.addGeofences(
                getGeofencingRequest(),
                getGeofencePendingIntent()
        );


    }

    private void makeGeofence(MyReminder reminder){

        mGeofenceList.add(
                new Geofence.Builder()
                        .setRequestId(reminder.getName())
                        .setCircularRegion(
                                reminder.getGeofenceLatitude(),
                                reminder.getGeofenceLongtitude(),
                                Constants.GEOFENCE_RADIUS_IN_METERS
                        )
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .build()

        );
    }

    private GeofencingRequest getGeofencingRequest(){

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(){

        if(mGeofencePendingIntent != null){
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(mContext, GeofenceTransitionIntentService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.LIST_EXTRA, mReminderList);
        intent.putExtra(Constants.BUNDLE_EXTRA, bundle);
        mGeofencePendingIntent = PendingIntent.getService(
                mContext,
                Constants.GEOFENCE_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        return mGeofencePendingIntent;
    }
}
