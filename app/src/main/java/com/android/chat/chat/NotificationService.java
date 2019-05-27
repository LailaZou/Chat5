package com.android.chat.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class NotificationService extends NotificationListenerService {

    Context context;

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

    }
    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
            if(sbn.getNotification()!=null){


                String pack = sbn.getPackageName();
               // String ticker = sbn.getNotification().tickerText.toString();
                Bundle extras = sbn.getNotification().extras;
                String title = extras.getString("android.title");
                String text = extras.getCharSequence("android.text").toString();

                Log.i("Package",pack);
                //Log.i("Ticker",ticker);
                Log.i("Title",title);
                Log.i("Text",text);

                Intent msgrcv = new Intent("Msg");
                msgrcv.putExtra("package", pack);
                msgrcv.putExtra("ticker", "tocker");
                msgrcv.putExtra("title", title);
                msgrcv.putExtra("text", text);

                LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);

            }




    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }
}
