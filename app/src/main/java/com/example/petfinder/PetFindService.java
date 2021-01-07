package com.example.petfinder;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResponse;

import java.security.Provider;

public class PetFindService extends Service {
    private final static String CHANNEL_NOTIFICATION_ID = "petfindservice_notification_id";
    private final static int ONGOING_NOTIFICATION_ID = 444;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedClient;

    public PetFindService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_NOTIFICATION_ID, "PetFindServiceNotification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("find pets");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        fusedClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    return;
                for (Location location : locationResult.getLocations()) {
                    //send updates to server
                    Toast.makeText(getApplicationContext(),location.toString(),Toast.LENGTH_SHORT).show();
                    Log.d("CallbackLocation", location.toString());
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = null;

        //notification's tap action
        Intent mapsActivityIntent = new Intent(getApplicationContext(),MapsActivity.class);
        mapsActivityIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent mapsActivityPendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, mapsActivityIntent,0);

        // notification's stop service button
        Intent stopServiceIntent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
        PendingIntent stopServicePendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),22, stopServiceIntent,0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(this,CHANNEL_NOTIFICATION_ID)
                    .setContentTitle("Recording Search")
                    .setContentText("Your location is being shared with our server")
                    .setColor(Color.RED)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(mapsActivityPendingIntent)
                    .addAction(new NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground,"Stop Service",stopServicePendingIntent).build())
                    .build();
        }
        startForeground(ONGOING_NOTIFICATION_ID,notification);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

        return START_STICKY; //TODO: for now
    }

    @Override
    public void onDestroy() {
        fusedClient.removeLocationUpdates(locationCallback);
        Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}