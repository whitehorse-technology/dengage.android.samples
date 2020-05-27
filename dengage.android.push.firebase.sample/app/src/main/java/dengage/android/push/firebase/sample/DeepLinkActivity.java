package dengage.android.push.firebase.sample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class DeepLinkActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_deeplink);

        Context context = getApplicationContext();

        if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Uri data = getIntent().getData();

            if (data != null) {

                Log.d("DenPush", data.toString());
                Log.d("DenPush", getIntent().getAction().toString());
            }
        }
    }
}
