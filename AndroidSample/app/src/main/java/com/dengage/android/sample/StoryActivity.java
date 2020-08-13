package com.dengage.android.sample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dengage.sdk.DengageManager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.huawei.hms.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private static StoriesProgressView storiesProgressView;
    private ImageView imageView;
    private ProgressBar progressBar;
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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_story);


        progressBar = (ProgressBar) findViewById(R.id.progress);
        imageView = (ImageView) findViewById(R.id.image);
        playerView = (PlayerView) findViewById(R.id.video_view);

        stories = getStories();

        if(stories != null && stories.length > 0) {
            load();
        } else {
            close();
        }
    }

    void load() {

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

        loadStroy(stories[counter].mediaUrl);

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

    void loadStroy(String mediaUrl) {
        Log.d("DenPush", stories[counter].mediaUrl);

        progressBar.setVisibility(View.VISIBLE);
        storiesProgressView.pause();

        if(mediaUrl.lastIndexOf(".mp4") > -1 || mediaUrl.lastIndexOf(".webm") > -1 ) {
            loadVideo(mediaUrl);
        } else {
            loadImage(mediaUrl);
        }

        Toast.makeText(getApplicationContext(),"Story "+ Integer.toString(counter + 1),Toast.LENGTH_LONG).show();
    }

    void loadImage(String mediaUrl) {
        playerView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
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
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        storiesProgressView.resume();
                        return false;
                    }
                })
                .into(imageView);
    }

    void loadVideo(String mediaUrl) {
        imageView.setVisibility(View.INVISIBLE);
        playerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        playerView.hideController();
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        Uri uri = Uri.parse(mediaUrl);
        MediaSource mediaSource = buildMediaSource(uri);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    playerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onNext() {

        if(stories == null || stories.length < 1) return;
        if(counter < stories.length -1)  ++counter; else return;

        loadStroy(stories[counter].mediaUrl);

    }

    @Override
    public void onPrev() {
        if(stories == null || stories.length < 1) return;
        if(counter > 0) --counter; else return;

        loadStroy(stories[counter].mediaUrl);
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
            req.setChannel("in_app_story");
            req.setContactKey(DengageManager.getInstance(getApplicationContext()).getSubscription().getContactKey());
            req.setDeviceId(DengageManager.getInstance(getApplicationContext()).getSubscription().getDeviceId());

            Map<String, Object> extraParams = new HashMap<>();
            req.setExtraParams(extraParams);

            String json = req.toJson();
            Log.d("DenPush", json);
            String response = sendRequest(url, json);
            Log.d("DenPush", "REsponse: "+  response);
            StoryResponse[] stories = new StoryResponse().fromJson(response);
            Log.d("DenPush", Integer.toString(stories.length));
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

    // TODO: response eksik geliyor.
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

            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                Log.d("DenPush", line);
                result.append(line);
            }

            int responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();

            conn.disconnect();

            if (responseCode <= 199 || responseCode >= 300) {
                throw new Exception("The remote server returned an error with the status code: " + responseCode);
            }

            return result.toString();

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


    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private PlayerView playerView;
    private SimpleExoPlayer player;

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "dengage-player");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            //initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            //initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }
}
