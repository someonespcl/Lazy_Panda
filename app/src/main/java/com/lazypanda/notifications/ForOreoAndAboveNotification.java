package com.lazypanda.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
// import androidx.core.app.RemoteInput;
import com.lazypanda.R;
import com.lazypanda.activities.WelcomeActivity;

public class ForOreoAndAboveNotification extends ContextWrapper {

    private static final String ID = "lazypanda_id";
    private static final String NAME = "LazyPanda";

    private final int NOTIFICATION_ID = 1;
    
    private NotificationManager notificationManager;

    public ForOreoAndAboveNotification(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel =
                new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_MAX);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public Notification.Builder getNotifications(
            String title, String body, PendingIntent pIntent, Uri soundUri, String icon) {

        Intent replyIntent = new Intent(getApplicationContext(), ReplyReceiver.class);
        replyIntent.putExtra("notification_id", NOTIFICATION_ID);

        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        2, // Use a unique request code
                        replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Create a RemoteInput for direct reply
        RemoteInput remoteInput = new RemoteInput.Builder("key_reply").setLabel("Reply").build();

        // Create a Notification.Action with the RemoteInput
        Notification.Action replyAction =
                new Notification.Action.Builder(R.drawable.send_outline, "Reply", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        return new Notification.Builder(getApplicationContext(), ID)
                .setContentIntent(pIntent)
                .setContentText(body)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon)).addAction(replyAction);
    }
}
