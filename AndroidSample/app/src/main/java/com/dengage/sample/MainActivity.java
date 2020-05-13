package com.dengage.sample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.dengage.sdk.DengageManager;
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
        final DengageManager manager =  DengageManager
                .getInstance(context)
                .setLogStatus(true)
                .setIntegrationKey(Constants.INTEGRATION_KEY)
                .init();

        TextView txtIntegrationKey = (TextView)findViewById(R.id.txtIntegrationKey);
        TextView txtDeviceId = (TextView)findViewById(R.id.txtDeviceId);
        TextView txtAdvertisingId = (TextView)findViewById(R.id.txtAdvertisingId);
        TextView txtToken = (TextView)findViewById(R.id.txtToken);
        TextView txtContactKey = (TextView)findViewById(R.id.txtContactKey);

        txtIntegrationKey.setText(Constants.INTEGRATION_KEY);
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
                } else {
                    txtAdvertisingId.setText("Not available yet. try again later.");
                }
            }
        });

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

}
