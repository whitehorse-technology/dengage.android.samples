package com.dengage.android.sample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.dengage.sdk.DengageManager;
import com.dengage.sdk.NotificationReceiver;
import com.dengage.sdk.Utils;
import com.dengage.sdk.models.CarouselItem;
import com.dengage.sdk.models.Message;

public class MyReceiver extends NotificationReceiver {

    @Override
    protected void onCarouselRender(Context context, Intent intent, Message message) {
        CarouselItem[] items = message.getCarouselContent();
        int size = items.length;
        int current = 0;
        int left = (current - 1 + size) % size;
        int right = (current + 1) % size;

        Bitmap imgLeft = Utils.loadImageFromStorage(items[left].getMediaFileLocation(), items[left].getMediaFileName());
        Bitmap imgCurrent = Utils.loadImageFromStorage(items[current].getMediaFileLocation(), items[current].getMediaFileName());
        Bitmap imgRight = Utils.loadImageFromStorage(items[right].getMediaFileLocation(), items[right].getMediaFileName());

        String itemTitle = items[current].getTitle();
        String itemDesc = items[current].getDescription();

        // set intets (right button, left button, item click)
        Intent itemIntent = getItemClickIntent(intent.getExtras(), context.getPackageName());
        Intent leftIntent = getLeftItemIntent(intent.getExtras(), context.getPackageName());
        Intent rightIntent = getRightItemIntent(intent.getExtras(), context.getPackageName());
        Intent deleteIntent = getDeleteIntent(intent.getExtras(), context.getPackageName());
        Intent contentIntent = getContentIntent(intent.getExtras(), context.getPackageName());
        PendingIntent carouseItemIntent = PendingIntent.getBroadcast(context, 0,
                itemIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent carouselLeftIntent = PendingIntent.getBroadcast(context, 1,
                leftIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent carouselRightIntent = PendingIntent.getBroadcast(context, 2,
                rightIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, 4,
                deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent contentPendingIntent = PendingIntent.getBroadcast(context, 5,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // set views for the layout
        RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.den_carousel_collapsed);
        collapsedView.setTextViewText(R.id.den_carousel_title, message.getTitle());
        collapsedView.setTextViewText(R.id.den_carousel_message, message.getMessage());
        RemoteViews carouselView = new RemoteViews(context.getPackageName(), R.layout.den_carousel_portrait);



        carouselView.setTextViewText(R.id.den_carousel_title, message.getTitle());
        carouselView.setTextViewText(R.id.den_carousel_message, message.getMessage());
        carouselView.setTextViewText(R.id.den_carousel_item_title, itemTitle);
        carouselView.setTextViewText(R.id.den_carousel_item_description, itemDesc);
        Utils.loadCarouselImageToView(
                carouselView,
                R.id.den_carousel_portrait_left_image,
                items[left]
        );
        Utils.loadCarouselImageToView(
                carouselView,
                R.id.den_carousel_portrait_current_image,
                items[current]
        );
        Utils.loadCarouselImageToView(
                carouselView,
                R.id.den_carousel_portrait_right_image,
                items[right]
        );
        carouselView.setOnClickPendingIntent(R.id.den_carousel_left_arrow, carouselLeftIntent);
        carouselView.setOnClickPendingIntent(R.id.den_carousel_portrait_current_image, carouseItemIntent);
        carouselView.setOnClickPendingIntent(R.id.den_carousel_item_title, carouseItemIntent);
        carouselView.setOnClickPendingIntent(R.id.den_carousel_item_description, carouseItemIntent);
        carouselView.setOnClickPendingIntent(R.id.den_carousel_right_arrow, carouselRightIntent);
        // create channelId
        String channelId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = getNotificationChannel();
            createNotificationChannel(context, notificationChannel);
            channelId = notificationChannel.getId();
        }
        // set views for the notification
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(carouselView)
                .setContentIntent(contentPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .build();
        // show message
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(message.getMessageSource(), message.getMessageId(), notification);
    }

    @Override
    protected void onCarouselReRender(Context context, Intent intent, Message message) {
        CarouselItem[] items = message.getCarouselContent();
        Bundle bundle = intent.getExtras();
        int prevIndex = bundle.getInt("current");
        String navigation = bundle.getString("navigation", "right");
        int size = items.length;
        int current = 0;
        if (navigation.equals("right")) {
            current = (prevIndex + 1) % size;
        } else {
            current = (prevIndex - 1 + size) % size;
        }
        int right = (current + 1) % size;
        int left = (current - 1 + size) % size;
        intent.putExtra("current", current);

        Bitmap imgLeft = Utils.loadImageFromStorage(items[left].getMediaFileLocation(), items[left].getMediaFileName());
        Bitmap imgCurrent = Utils.loadImageFromStorage(items[current].getMediaFileLocation(), items[current].getMediaFileName());
        Bitmap imgRight = Utils.loadImageFromStorage(items[right].getMediaFileLocation(), items[right].getMediaFileName());

        String itemTitle = items[current].getTitle();
        String itemDesc = items[current].getDescription();

        // set intents (next button, rigth button and item click)
        Intent itemIntent = getItemClickIntent(intent.getExtras(), context.getPackageName());
        Intent leftIntent = getLeftItemIntent(intent.getExtras(), context.getPackageName());
        Intent rightIntent = getRightItemIntent(intent.getExtras(), context.getPackageName());
        Intent deleteIntent = getDeleteIntent(intent.getExtras(), context.getPackageName());
        Intent contentIntent = getContentIntent(intent.getExtras(), context.getPackageName());
        PendingIntent carouseItemIntent = PendingIntent.getBroadcast(context, 0,
                itemIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent carouselLeftIntent = PendingIntent.getBroadcast(context, 1,
                leftIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent carouselRightIntent = PendingIntent.getBroadcast(context, 2,
                rightIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, 4,
                deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent contentPendingIntent = PendingIntent.getBroadcast(context, 5,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // set views for the layout
        RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.den_carousel_collapsed);
        collapsedView.setTextViewText(R.id.den_carousel_title, message.getTitle());
        collapsedView.setTextViewText(R.id.den_carousel_message, message.getMessage());
        RemoteViews carouselView = new RemoteViews(context.getPackageName(), R.layout.den_carousel_portrait);
        carouselView.setTextViewText(R.id.den_carousel_title, message.getTitle());
        carouselView.setTextViewText(R.id.den_carousel_message, message.getMessage());
        carouselView.setTextViewText(R.id.den_carousel_item_title, itemTitle);
        carouselView.setTextViewText(R.id.den_carousel_item_description, itemDesc);
        Utils.loadCarouselImageToView(
                carouselView,
                R.id.den_carousel_portrait_left_image,
                items[left]
        );
        Utils.loadCarouselImageToView(
                carouselView,
                R.id.den_carousel_portrait_current_image,
                items[current]
        );
        Utils.loadCarouselImageToView(
                carouselView,
                R.id.den_carousel_portrait_right_image,
                items[right]
        );
        carouselView.setOnClickPendingIntent(R.id.den_carousel_left_arrow, carouselLeftIntent);
        carouselView.setOnClickPendingIntent(R.id.den_carousel_portrait_current_image, carouseItemIntent);
        carouselView.setOnClickPendingIntent(R.id.den_carousel_item_title, carouseItemIntent);
        carouselView.setOnClickPendingIntent(R.id.den_carousel_item_description, carouseItemIntent);
        carouselView.setOnClickPendingIntent(R.id.den_carousel_right_arrow, carouselRightIntent);
        // create a channel id.
        String channelId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = getNotificationChannel();
            createNotificationChannel(context, notificationChannel);
            channelId = notificationChannel.getId();
        }
        // set your views for the notification
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(carouselView)
                .setContentIntent(contentPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .build();
        // show message again silently with next,prev and current item.
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(message.getMessageSource(), message.getMessageId(), notification);
    }
}
