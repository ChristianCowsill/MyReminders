package com.cowsill.myreminders;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class App extends Application {

    public static final String LOCATION_CHANNEL_ID = "location_channel_id";
    public static final String GEOFENCE_CHANNEL_ID = "geofence_channel_id";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {

        createNotificationChannel(
                "Location Services",
                "Informs user that Location Services is monitoring device location",
                NotificationManager.IMPORTANCE_DEFAULT,
                LOCATION_CHANNEL_ID);

        createNotificationChannel(
                "MyReminder",
                "Informs user that a geofence transition has occured",
                NotificationManager.IMPORTANCE_HIGH,
                GEOFENCE_CHANNEL_ID
        );
    }

    private void createNotificationChannel(String name, String desc, int importance, String channelId) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(desc);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

}
