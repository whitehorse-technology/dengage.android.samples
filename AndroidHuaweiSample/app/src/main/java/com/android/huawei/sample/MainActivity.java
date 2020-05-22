package com.android.huawei.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;

public class MainActivity extends AppCompatActivity {
    private Button btnToken;
    private String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(Constants.LOG_TAG, "Main Activity Created.");

        getToken();
        btnToken = findViewById(R.id.btn_get_token);
        btnToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Constants.LOG_TAG, "Button Clicked.");
                getToken();
            }
        });
    }


    /**
     * get token
     */
    private void getToken() {
        Log.d(Constants.LOG_TAG, "Getting Token.");

        // get token
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    deviceToken = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, "HCM");
                    if(!TextUtils.isEmpty(deviceToken)) {
                        Log.d(Constants.LOG_TAG, "Token: "+ deviceToken);
                        showLog(deviceToken);
                    }
                } catch (Exception e) {
                    Log.d(Constants.LOG_TAG, "Token: "+ deviceToken);
                }
            }
        }.start();
    }

    private void showLog(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View tvView = findViewById(R.id.txt_log);
                if (tvView instanceof TextView) {
                    ((TextView) tvView).setText(log);
                    Toast.makeText(MainActivity.this, deviceToken, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
