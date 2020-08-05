package com.dengage.android.sample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.dengage.sdk.DengageManager;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private static StoriesProgressView storiesProgressView;
    private ImageView imageView;
    private ProgressBar progressBar;
    OnSwipeTouchListener onSwipeTouchListener;

    private int counter = 0;
    private StoryMessage[] stories;

    long pressTime = 0L;
    long limit = 500L;
    long storyDuration = 6000L;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    Log.d("DenPush", "action down");
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    Log.d("DenPush", "action up");
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_story);

        stories = getStories();

        if(stories != null && stories.length > 0) {
            load();
        } else {
            close();
        }
    }

    void load() {
        progressBar = (ProgressBar) findViewById(R.id.progress);
        imageView = (ImageView) findViewById(R.id.image);

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(stories.length);
        storiesProgressView.setStoryDuration(storyDuration);
        storiesProgressView.setStoriesListener(this);

        loadImage(stories[counter].mediaUrl);

        storiesProgressView.startStories(counter);
    }

    void close() {
        Toast
                .makeText(getApplicationContext(),"There is no message",Toast.LENGTH_LONG)
                .show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                onBackPressed();
            }
        }, 1000);
    }

    void loadImage(String mediaUrl) {
        Log.d("DenPush", stories[counter].mediaUrl);
        progressBar.setVisibility(View.VISIBLE);
        storiesProgressView.pause();
        Glide.with(getApplicationContext())
                .load(mediaUrl)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        storiesProgressView.setStoryDuration(storyDuration);
                        Toast
                            .makeText(getApplicationContext(),"Resource not found. Skipping",Toast.LENGTH_LONG)
                            .show();
                        storiesProgressView.resume();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        storiesProgressView.resume();
                        return false;
                    }
                })
                .into(imageView);

        Toast
                .makeText(getApplicationContext(),"Story "+ Integer.toString(counter + 1),Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onNext() {

        if(stories == null || stories.length < 1) return;
        if(counter < stories.length -1)  ++counter; else return;

        loadImage(stories[counter].mediaUrl);

    }

    @Override
    public void onPrev() {
        if(stories == null || stories.length < 1) return;
        if(counter > 0) --counter; else return;

        loadImage(stories[counter].mediaUrl);
    }

    @Override
    public void onComplete() {
        Log.d("DenPush", "Completed");

        Toast
            .makeText(getApplicationContext(),"Completed",Toast.LENGTH_LONG)
            .show();

        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.d("DenPush", "onDestory");

        if(storiesProgressView != null)
            storiesProgressView.destroy();

        super.onDestroy();
    }

    private StoryMessage[] getStories() {
        List<StoryMessage> messages = new ArrayList<>();
        Log.d("DenPush", "Getting stories...");
        try {

            String url = "https://cmadev.dengage.com/api/c/m/search";

            StoryRequest req = new StoryRequest();
            req.setAccountGuid("90db7e2a-5839-53cd-605f-9d3ffc328e21");
            req.setChannel("app_popup");
            req.setContactKey(DengageManager.getInstance(getApplicationContext()).getSubscription().getContactKey());
            req.setDeviceId(DengageManager.getInstance(getApplicationContext()).getSubscription().getDeviceId());

            Map<String, Object> extraParams = new HashMap<>();
            extraParams.put("a", "1");
            req.setExtraParams(extraParams);

            String json = req.toJson();
            Log.d("DenPush", json);
            String response = sendRequest(url, json);
            Log.d("DenPush", response);
            StoryResponse[] stories = new StoryResponse().fromJson(response);
            if(stories != null) {
                for (StoryResponse story : stories) {
                    messages.add(story.getInnerMessage());
                }
            }
        } catch (Exception e) {
            Log.e("DenPush", e.getMessage());
        }

        return messages.toArray(new StoryMessage[messages.size()]);
    }


    private String sendRequest(String url, String data) {
        String responseMessage = "";
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL uri = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

            int readTimeout = 10000;
            conn.setReadTimeout(readTimeout);

            int connectionTimeout = 15000;
            conn.setConnectTimeout(connectionTimeout);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setFixedLengthStreamingMode(data.getBytes().length);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.connect();

            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(data.getBytes());
            os.flush();

            BufferedReader is = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder res = new StringBuilder();
            for (String line; (line = is.readLine()) != null; ) {
                res.append(line).append('\n');
            }

            int responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();

            conn.disconnect();

            if (responseCode <= 199 || responseCode >= 300) {
                throw new Exception("The remote server returned an error with the status code: " + responseCode);
            }

            return res.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("DenPush", ex.getMessage());
        }
        return responseMessage;
    }


    private void showMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(StoryActivity.this).create();
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

    public class OnSwipeTouchListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector;
        Context context;
        OnSwipeTouchListener(Context ctx, View mainView) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            mainView.setOnTouchListener(this);
            context = ctx;
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("DenPush", "onTouch");
            return gestureDetector.onTouchEvent(event);
        }
        public class GestureListener extends
                GestureDetector.SimpleOnGestureListener {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
        void onSwipeRight() {
            Toast.makeText(context, "Swiped Right", Toast.LENGTH_SHORT).show();
            storiesProgressView.resume();
        }
        void onSwipeLeft() {
            Toast.makeText(context, "Swiped Left", Toast.LENGTH_SHORT).show();
            storiesProgressView.reverse();
        }
        void onSwipeTop() {
            Toast.makeText(context, "Swiped Up", Toast.LENGTH_SHORT).show();
        }
        void onSwipeBottom() {
            Toast.makeText(context, "Swiped Down", Toast.LENGTH_SHORT).show();
        }
    }
}
