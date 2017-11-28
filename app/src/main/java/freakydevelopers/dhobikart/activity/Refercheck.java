package freakydevelopers.dhobikart.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.MyToast;
import freakydevelopers.dhobikart.connection.CheckNet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.JSON;
import static freakydevelopers.dhobikart.Resources.MyURL.REFERCHECKDEVICE;

public class Refercheck extends AppCompatActivity implements View.OnClickListener {

    EditText referalCode;
    Button referCheck;
    private static Context context;
    AVLoadingIndicatorView indicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refercheck);
        referCheck = (Button) findViewById(R.id.applyreferal);
        referalCode = (EditText) findViewById(R.id.referalcode);
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);
        referCheck.setOnClickListener(this);
        Refercheck.context = this;
    }

    @Override
    public void onClick(View view) {


//        final String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String android_id = getMacAddress(context);
        boolean isEmulator = isEmulator();
        boolean isrefralempty = true;

        if (CheckNet.checkNet(context) && (isrefralempty = referalCode.getText().toString().length() == 7) && !isEmulator) {
            new GetDeviceId().execute(android_id);
        } else if (!isrefralempty) {

            MyToast.showToast(context, "Please Enter Valid Referal Code!");
        } else if (isEmulator) {

            MyToast.showToast(context, "Hey You!! I got You.No Emulator allowed!!");
        } else {

            MyToast.showToast(context, "Please close the App and try Again!!");


        }


    }


    public class GetDeviceId extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();
        String responseData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indicatorView.show();
            referCheck.setClickable(false);
        }

        @Override
        protected String doInBackground(String... strings) {

            RequestBody body = RequestBody.create(JSON, strings[0]);
            Request request = new Request.Builder()
                    .url(REFERCHECKDEVICE)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                responseData = response.body().string();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseData;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            indicatorView.hide();

            referCheck.setClickable(true);

            if (string.equals("goodtogoyeah")) {

                Intent intent = new Intent();
                intent.putExtra("CODE", referalCode.getText().toString());
                setResult(1, intent);
                MyToast.showToast(context, "Successfully Applied!!");
                finish();

            } else {

                Intent intent = new Intent();
                intent.putExtra("CODE", referalCode.getText().toString());
                setResult(1, intent);
                MyToast.showToast(context, string);

            }


        }

    }

    public static String getMacAddress(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("android_id", androidId);
        return androidId;
    }

    public static boolean isEmulator() {
        try {
            BluetoothAdapter mbAdapter = BluetoothAdapter.getDefaultAdapter();
            String btMAC = mbAdapter.getAddress();
        } catch (NullPointerException e) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("CODE", "");
        setResult(1, intent);
        finish();
    }
}
