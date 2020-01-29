package com.dengage.application.prod;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.dengage.sdk.DengageManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final String integrationKey = "x9V_p_l_TEhVz_s_l_2Bho_s_l_lsfjMIsjEjGECyDzoldLHF7G3gi1GiUJBSJujlUqmeWN9dcOiTgpVSqlCybo83sNQVeTUbMo559FIeyL5XXOj_p_l_p4XidRgb2KA_s_l_3t_p_l_jcMpo_s_l_2aKrgG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Context context = getApplicationContext();
        DengageManager.setLogStatus(true);
        DengageManager.setConfig(integrationKey, context);

        TextView txtDeviceId = (TextView)findViewById(R.id.txtDeviceId);
        TextView txtAdvertisingId = (TextView)findViewById(R.id.txtAdvertisingId);
        TextView txtToken = (TextView)findViewById(R.id.txtToken);
        TextView txtContactKey = (TextView)findViewById(R.id.txtContactKey);

        txtDeviceId.setText(DengageManager.getDeviceId());
        txtAdvertisingId.setText(DengageManager.getAdvertisingId());
        txtToken.setText(DengageManager.getToken());
        txtContactKey.setText(DengageManager.getContactKey());

        Button btnContactKey = (Button) findViewById(R.id.btnContactKey);
        btnContactKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtContactKey = (TextView)findViewById(R.id.txtContactKey);
                String contactkey = txtContactKey.getText().toString();
                DengageManager.setContactKey(contactkey);
                DengageManager.syncSubscription();
                showMessage("Contact key has been successfully set.");
            }
        });

        Button btnDeviceId = (Button) findViewById(R.id.btnDeviceId);
        btnDeviceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtDeviceId = (TextView)findViewById(R.id.txtDeviceId);
                txtDeviceId.setText("Retrieving...");
                String udid = DengageManager.getDeviceId();
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
                String token = DengageManager.getToken();
                if(token != "") {
                    txtToken.setText(token);
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
                String adid = DengageManager.getAdvertisingId();
                if(adid != "") {
                    txtAdvertisingId.setText(adid);
                } else {
                    txtAdvertisingId.setText("Not available yet. try again later.");
                }
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
