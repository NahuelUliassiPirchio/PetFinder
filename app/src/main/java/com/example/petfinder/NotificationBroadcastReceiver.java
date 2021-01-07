package com.example.petfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context,PetFindService.class));
    }
}