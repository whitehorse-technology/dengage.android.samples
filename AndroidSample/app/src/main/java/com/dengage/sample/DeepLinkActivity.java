package com.dengage.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dengage.sdk.DengageEvent;
import com.dengage.sdk.models.CardItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeepLinkActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_deeplink);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Context context = getApplicationContext();

        DengageEvent.getInstance(context, getIntent()).homePage();
        DengageEvent.getInstance(context, getIntent()).productDetail("12", 1.99, 0.99,
                "usd", "1");
        DengageEvent.getInstance(context, getIntent()).promotionPage("1");
        DengageEvent.getInstance(context, getIntent()).categoryPage("1", "");
        DengageEvent.getInstance(context, getIntent()).searchPage("adidas", 10);

        HashMap<String, List<String>> bundle = new HashMap<>();

        ArrayList<String> brand = new ArrayList<>();
        brand.add("adidas");
        brand.add("puma");
        bundle.put("brand", brand);

        ArrayList<String> color = new ArrayList<>();
        color.add("black");
        color.add("white");
        bundle.put("color", color);

        DengageEvent.getInstance(context, getIntent()).refinement(bundle,10);

        DengageEvent.getInstance(context, getIntent()).loginPage();
        DengageEvent.getInstance(context, getIntent()).loginAction("112", true, "form");

        CardItem ci = new CardItem();
        ci.setCurrency("usd");
        ci.setDiscountedPrice(0.99);
        ci.setPrice(1.99);
        ci.setProductId("1");
        ci.setQuantity(1);
        ci.setVariantId("2");
        DengageEvent.getInstance(context, getIntent()).addToBasket(ci, "Product Detail Page", "1");

        DengageEvent.getInstance(context, getIntent()).removeFromBasket("1", "2", 1, "2");

        DengageEvent.getInstance(context, getIntent()).addToBasket(ci, "Product Detail Page", "1");

        ArrayList<CardItem> cis = new ArrayList<>();
        cis.add(ci);
        DengageEvent.getInstance(context, getIntent()).basketPage(cis.toArray(new CardItem[cis.size()]), 0.99, "1");

        DengageEvent.getInstance(context, getIntent()).orderSummary(cis.toArray(new CardItem[cis.size()]), "1", 0.99, "1", "paypal");


        if (getIntent().getAction() == Intent.ACTION_VIEW) {
            Uri data = getIntent().getData();

            if (data != null) {

                Log.d("DenPush", data.toString());
                Log.d("DenPush", getIntent().getAction().toString());
            }
        }
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
}
