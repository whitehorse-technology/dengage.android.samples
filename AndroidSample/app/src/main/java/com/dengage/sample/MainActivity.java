package com.dengage.sample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.dengage.sdk.DengageManager;
import com.dengage.sdk.models.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // PROD
    //final String integrationKey = "x9V_p_l_TEhVz_s_l_2Bho_s_l_lsfjMIsjEjGECyDzoldLHF7G3gi1GiUJBSJujlUqmeWN9dcOiTgpVSqlCybo83sNQVeTUbMo559FIeyL5XXOj_p_l_p4XidRgb2KA_s_l_3t_p_l_jcMpo_s_l_2aKrgG";

    // DEV
    //final String integrationKey = "x9V_p_l_TEhVz_s_l_2Bho_s_l_lsfjMIsjEjGECyDzoldLHF7G3gi1GiUJBSJujlUqmeWN9dcOiTgpVSqlCybo83sNQVeTUbMo559FIeyL5XXOj_p_l_p4XidRgb2KA_s_l_3t_p_l_jcMpo_s_l_2aKrgG";

    // TEST
    final String integrationKey = "Evke8NhhgLX6Gwo5QeggzCMFVloTnGjUSf0oWPUQM7mQxsia4pRkSuqCqhPtu_s_l_q7Ye9PemJYerIjEmd8qbEVNQL2_s_l__s_l_gN6vmS25Aj2EMk_s_l_2k1us1JkrjFgT3kbEDjuZqq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Context context = getApplicationContext();
        DengageManager.setLogStatus(true);
        DengageManager.setConfig(integrationKey, context);

        TextView txtIntegrationKey = (TextView)findViewById(R.id.txtIntegrationKey);
        TextView txtDeviceId = (TextView)findViewById(R.id.txtDeviceId);
        TextView txtAdvertisingId = (TextView)findViewById(R.id.txtAdvertisingId);
        TextView txtToken = (TextView)findViewById(R.id.txtToken);
        TextView txtContactKey = (TextView)findViewById(R.id.txtContactKey);

        txtIntegrationKey.setText(integrationKey);
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

        Intent intent = getIntent();
        sendOpen(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        sendOpen(intent);
    }

    private void sendOpen(Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras != null) {
            DengageManager.sendOpenEvent(new Message(extras));
        }
    }
}
