package com.example.marcotoni.pihome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;

public class EventPullService extends IntentService {
    public EventPullService() { super("EventPullService"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager cnMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ||
                cnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED )
        {
            new CheckForNews().execute();
        }
    }

    private void NotifyEvent(JSONArray array){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (array.length()>0) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("New event found")
                    .setContentText("Someone accessed in your house")
                    .setSmallIcon(R.drawable.ic_stat_action_visibility)
                    .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap())
                    .setNumber(array.length())
                    .setSound(Uri.parse(sharedPreferences.getString("notifications_new_message_ringtone","content://settings/system/notification_sound")));
                    //.setLight()
            if(sharedPreferences.getBoolean("notifications_new_message_vibrate",true)) mBuilder.setVibrate(new long[] {1000,1000});
            Intent resultIntent = new Intent(this, main.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(main.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }
    }

    private class CheckForNews extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            RPiClient clt = new RPiClient(getApplicationContext());
            return clt.sendRequest("selectEventNotification");
        }

        @Override
        protected void onPostExecute(Object result){
            super.onPostExecute(result);
            if(result != null){ NotifyEvent((JSONArray) result); }
        }
    }
}
