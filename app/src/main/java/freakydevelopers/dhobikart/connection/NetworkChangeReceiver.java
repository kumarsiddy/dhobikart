package freakydevelopers.dhobikart.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import freakydevelopers.dhobikart.Resources.MyToast;

/**
 * Created by PURUSHOTAM on 8/20/2017.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
/*
            if (CheckNet.checkNet(context))
                MyToast.showToast(context, "Connected");
            else
                MyToast.showToast(context, "Disconnected!!");*/
        }
    }
}