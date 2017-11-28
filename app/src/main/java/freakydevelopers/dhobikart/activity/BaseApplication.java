package freakydevelopers.dhobikart.activity;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;

/**
 * Created by PURUSHOTAM on 8/19/2016.
 */
public class BaseApplication extends Application {

    private static int noOfCartItems;
    private static int totalprice;
    private static String token;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static int getNoOfCartItems() {
        return noOfCartItems;
    }

    public static void addNoOfCartItems(int noOfCartItems) {
        BaseApplication.noOfCartItems = BaseApplication.noOfCartItems + noOfCartItems;
    }

    public static void removeNoOfCartItems(int noOfCartItems) {
        BaseApplication.noOfCartItems = BaseApplication.noOfCartItems - noOfCartItems;
    }

    public static void setNoOfCartItems(int noOfCartItems) {
        BaseApplication.noOfCartItems = noOfCartItems;
    }

    public static int getTotalprice() {
        return totalprice;
    }

    public static void setTotalprice(int totalprice) {
        BaseApplication.totalprice = totalprice;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        BaseApplication.token = token;
    }
}
