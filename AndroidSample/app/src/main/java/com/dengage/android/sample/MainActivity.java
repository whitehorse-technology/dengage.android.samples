package com.dengage.android.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dengage.sdk.DengageEvent;
import com.dengage.sdk.DengageManager;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        final DengageManager manager = DengageManager
                .getInstance(context)
                .setLogStatus(true)
                .setFirebaseIntegrationKey(Constants.FIREBASE_INTEGRATION_KEY)
                .setHuaweiIntegrationKey(Constants.HUAWEI_INTEGRATION_KEY)
                .init();

        TextView txtIntegrationKey = (TextView)findViewById(R.id.txtIntegrationKey);
        TextView txtDeviceId = (TextView)findViewById(R.id.txtDeviceId);
        TextView txtAdvertisingId = (TextView)findViewById(R.id.txtAdvertisingId);
        TextView txtToken = (TextView)findViewById(R.id.txtToken);
        TextView txtContactKey = (TextView)findViewById(R.id.txtContactKey);

        txtIntegrationKey.setText(manager.getSubscription().getIntegrationKey());
        txtDeviceId.setText(manager.getSubscription().getDeviceId());
        txtAdvertisingId.setText(manager.getSubscription().getAdvertisingId());
        txtToken.setText(manager.getSubscription().getToken());
        txtContactKey.setText(manager.getSubscription().getContactKey());

        Button btnContactKey = (Button) findViewById(R.id.btnContactKey);
        btnContactKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtContactKey = (TextView)findViewById(R.id.txtContactKey);
                String contactkey = txtContactKey.getText().toString();
                manager.setContactKey(contactkey);
                manager.syncSubscription();
                showMessage("Contact key has been successfully set.");
            }
        });

        Button btnDeviceId = (Button) findViewById(R.id.btnDeviceId);
        btnDeviceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtDeviceId = (TextView)findViewById(R.id.txtDeviceId);
                txtDeviceId.setText("Retrieving...");
                String udid = manager.getSubscription().getDeviceId();
                if(udid != "") {
                    txtDeviceId.setText(udid);
                } else {
                    txtDeviceId.setText("Not available yet. try again later.");
                }
            }
        });

        Button btnToken = (Button) findViewById(R.id.btnToken);
        btnToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtToken = (TextView)findViewById(R.id.txtToken);
                txtToken.setText("Retrieving...");
                String token = manager.getSubscription().getToken();
                if(token != "") {
                    txtToken.setText(token);
                    Log.d("DenPush", "Token: "+ token);
                } else {
                    txtToken.setText("Not available yet. try again later.");
                }
            }
        });

        Button btnAdvertisingId = (Button) findViewById(R.id.btnAdvertisingId);
        btnAdvertisingId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtAdvertisingId = (TextView)findViewById(R.id.txtAdvertisingId);
                txtAdvertisingId.setText("Retrieving...");
                String adid = manager.getSubscription().getAdvertisingId();
                if(adid != "") {
                    txtAdvertisingId.setText(adid);
                    Log.d("DenPush", "AdId: "+ adid);
                } else {
                    txtAdvertisingId.setText("Not available yet. try again later.");
                }
            }
        });

        final Intent intent = new Intent(this, StoryActivity.class);
        Button btnGetStories = (Button) findViewById(R.id.btnGetStories);
        btnGetStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
    }

    private void showMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
