package freakydevelopers.dhobikart.Resources;

import android.util.Log;

/**
 * Created by PURUSHOTAM on 8/18/2017.
 */

public class Logger {

    public static void v(String data) {
        Log.v("VERBOSE", data);
    }

    public static void d(String data) {
        Log.d("DEBUG", data);
    }

    public static void i(String data) {
        Log.i("INFO", data);
    }

    public static void w(String data) {
        Log.w("WARN", data);
    }

    public static void e(String data) {
        Log.e("ERROR", data);
    }
}
