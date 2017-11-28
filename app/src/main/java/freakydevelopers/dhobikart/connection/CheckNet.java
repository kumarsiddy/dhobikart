package freakydevelopers.dhobikart.connection;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

/**
 * Created by PURUSHOTAM on 8/19/2016.
 */
public class CheckNet {
    private static Context context;

    public static Boolean checkNet(Context context) {
        CheckNet.context = context;
        Boolean iconnection = false;
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = conMgr.getAllNetworkInfo();

        for (int i = 0; i < info.length; i++) {

            if (info[i].getState() == NetworkInfo.State.DISCONNECTED || info[i].getState() == NetworkInfo.State.SUSPENDED) {
                iconnection = false;
            }
        }

        for (int i = 0; i < info.length; i++) {

            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                iconnection = true;
            }
        }
        if (!iconnection) {
            ShowInternetDialog();
        }
        return iconnection;
    }

    private static void ShowInternetDialog() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage("Ooops...Check Your Internet Connection!")
                .setCancelable(false);

        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}
