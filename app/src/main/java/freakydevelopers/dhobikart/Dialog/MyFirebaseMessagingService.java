package freakydevelopers.dhobikart.Dialog;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.activity.ClothActivity;

/**
 * Created by PURUSHOTAM on 11/30/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, ClothActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle("Dhobi Cart");
        nBuilder.setContentText(remoteMessage.getNotification().getBody());
        nBuilder.setAutoCancel(true);
        nBuilder.setSmallIcon(R.mipmap.ic_launcher);
        nBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, nBuilder.build());
    }
}
