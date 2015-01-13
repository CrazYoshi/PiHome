package com.example.marcotoni.pihome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EventAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.example.marcotoni.pihome.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, EventPullService.class);
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}
