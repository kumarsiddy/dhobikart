package freakydevelopers.dhobikart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.connection.CheckNet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.JSON;
import static freakydevelopers.dhobikart.Resources.MyURL.REFERCODE;
import static freakydevelopers.dhobikart.Resources.MyURL.REFERTEXT;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;


public class Referral extends AppCompatActivity {

    private TextView shareText, referCode;
    private String referalCode = "";
    ImageButton whatsApp;
    Button shareButton;
    AVLoadingIndicatorView indicatorView;
    private Context context;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);
        shareText = (TextView) findViewById(R.id.sharetext);
        referCode = (TextView) findViewById(R.id.refercode);
        whatsApp = (ImageButton) findViewById(R.id.whatsapp);
        shareButton = (Button) findViewById(R.id.sharebutton);
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);

        whatsApp.setClickable(false);
        shareButton.setClickable(false);
        context = this;
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Refer & Earn");

    }

    @Override
    protected void onStart() {
        super.onStart();

        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PackageManager pm = getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = "Download Dhobi Cart from playstore.Link: https://play.google.com/store/apps/details?id=freaky.devs.dhobicart , And Sign Up using My referal code "
                            + referalCode +
                            " to earn Rs.50.";

                    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(Referral.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }


            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shareBody = "Download Dhobi Cart from playstore.Link: https://play.google.com/store/apps/details?id=freaky.devs.dhobicart ,And Sign Up using My referal code "
                        + referalCode +
                        " to earn Rs.50.";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Earn Rs.50");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (CheckNet.checkNet(context)) {
            indicatorView.show();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(REFERTEXT)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        final String shareString = response.body().string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                indicatorView.hide();
                                if (shareString.isEmpty() || shareString.equals(null)) {

                                    shareText.setText("REFERCODE");

                                } else {
                                    shareText.setText(shareString);

                                }

                            }
                        });

                        Log.d("REFERTEXT", shareString);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


            new Thread(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences sharedPref = getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    String authCode = sharedPref.getString(getString(R.string.token), "");
                    RequestBody body = RequestBody.create(JSON, String.valueOf(authCode));
                    Request request = new Request.Builder()
                            .url(REFERCODE)
                            .post(body)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        final String shareString = response.body().string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (shareString.isEmpty() || shareString.equals(null)) {

                                    referCode.setText("REFERCODE");

                                } else {
                                    referCode.setText(shareString);
                                    referalCode = shareString;
                                    whatsApp.setClickable(true);
                                    shareButton.setClickable(true);
                                }

                            }
                        });

                        Log.d("REFERCODE", shareString);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
