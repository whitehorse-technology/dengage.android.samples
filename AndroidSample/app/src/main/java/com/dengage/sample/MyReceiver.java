package com.dengage.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.dengage.sdk.NotificationReceiver;
import com.dengage.sdk.DengageManager;
import com.dengage.sdk.models.Message;

public class MyReceiver extends NotificationReceiver {
    @Override
    protected void onPushOpen(Context context, Intent intent) {

        Log.v("DenPush", "onPushOpen tetiklendi.");

        String action = intent.getAction();
        Log.v("DenPush", action);
        Bundle extras = intent.getExtras();
        Message message = new Message(extras);
        Log.v("DenPush", message.toJson());

        DengageManager.getInstance(context).setLogStatus(true);

        super.onPushOpen(context, intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("DenPush", "onReceive tetiklendi.");
        super.onReceive(context, intent);
    }
}
