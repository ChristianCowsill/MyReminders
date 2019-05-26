package com.cowsill.myreminders;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionIntentService extends IntentService {

    private static final String TAG = "GeofenceTransitionServ";
    private ArrayList<MyReminder> mReminderList;

    public GeofenceTransitionIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // Get reminderList from intent;  We need this to send custom notification message
        Bundle bundle = intent.getBundleExtra(Constants.BUNDLE_EXTRA);
        mReminderList = bundle.getParcelableArrayList(Constants.LIST_EXTRA);

        // Get geofencing event
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            Log.e(TAG, "onHandleIntent: GeofencingEvent Error: Code " + geofencingEvent.getErrorCode());
            return;
        }

        // Get transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Was transition of interest?
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // If so, link geofence to reminder using for loops and send user their custom notification
            for(Geofence gf : triggeringGeofences){
                for(MyReminder reminder : mReminderList){
                    if(gf.getRequestId().equals(reminder.getName())){
                        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
                        manager.notify(
                                Constants.GEOFENCE_TRANSITION_NOTIFICATION_ID,
                                createNotification(reminder.getMessage())
                        );
                    }
                }
            }
        }
    }

    // creates custom notification based on message from MyReminder object
    private Notification createNotification(String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.GEOFENCE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location_and_geofence)
                .setContentTitle(getString(R.string.my_reminder))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }
}
