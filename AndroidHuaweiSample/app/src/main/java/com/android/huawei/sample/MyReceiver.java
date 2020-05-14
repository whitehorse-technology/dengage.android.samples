package com.android.huawei.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.LOG_TAG, "onReceive event called!");
        String intentAction = intent.getAction();
        if (intentAction != null) {
            switch (intentAction) {
                case Constants.PUSH_RECEIVE_EVENT:
                    onPushReceive(context, intent);
                    break;
            }
        }
    }

    protected void onPushReceive(Context context, Intent intent) {
        Log.i(Constants.LOG_TAG, "onPushReceive event called!");
        Bundle extras = intent.getExtras();
        if(extras == null) return;

    }
}
