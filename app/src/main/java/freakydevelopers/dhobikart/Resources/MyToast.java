package freakydevelopers.dhobikart.Resources;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import freakydevelopers.dhobikart.R;

/**
 * Created by PURUSHOTAM on 8/11/2017.
 */

public class MyToast {

    private Context context;
    private String message;

    public MyToast(Context context, String message) {
        this.context = context;
        this.message = message;
    }

    public static void showToast(Context context,String message) {
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.toast_layout, null);
        TextView textView = (TextView) v.findViewById(R.id.toast_message);
        textView.setText(message);
        toast.setView(v);
        toast.show();
    }

}
