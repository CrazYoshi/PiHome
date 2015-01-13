package com.example.marcotoni.pihome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
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
        if (array.length()>0) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("New event found")
                    .setContentText("Someone accessed in your house")
                    .setSmallIcon(R.drawable.ic_stat_action_visibility)
                    .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap())
                    .setNumber(array.length());
                    //.setSound();
                    //.setVibrate()
                    //.setLight()
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
            String address = getString(R.string.JSONRPCserver);
            RPiClient clt = new RPiClient(address);
            return clt.sendRequest("selectEventNotification");
        }

        @Override
        protected void onPostExecute(Object result){
            super.onPostExecute(result);
            if(result != null){ NotifyEvent((JSONArray) result); }
        }
    }
}
