package com.android.huawei.sample;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import java.util.Map;

public class MyPushService extends HmsMessageService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i(Constants.LOG_TAG, "receive token:" + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(Constants.LOG_TAG, "onMessageReceived method is called.");

        String data = remoteMessage.getData();
        if( data != null && !TextUtils.isEmpty(data)) {
            Log.i(Constants.LOG_TAG, data);
        }
    }

    private void sendBroadcast(String json, Map<String, String> data) {
        try {
            Context context = getApplicationContext();
            Intent intent = new Intent(Constants.PUSH_RECEIVE_EVENT);
            intent.putExtra("RAW_DATA", json);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
            intent.setPackage(context.getPackageName());
            context.sendBroadcast(intent);
        } catch (Exception e) {
            Log.i(Constants.LOG_TAG, "sendBroadcast: " + e.getMessage());
        }
    }
}