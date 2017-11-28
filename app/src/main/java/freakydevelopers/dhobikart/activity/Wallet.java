package freakydevelopers.dhobikart.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.connection.CheckNet;
import okhttp3.Request;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.DHPOINTURL;
import static freakydevelopers.dhobikart.Resources.MyURL.MESSAGETEXTURL;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;

public class Wallet extends AppCompatActivity implements View.OnClickListener {

    ImageView backButton, moneyAdd;
    TextView walletMoney, earnMore, message;
    AVLoadingIndicatorView indicatorView;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        backButton = (ImageView) findViewById(R.id.backbutton);
        walletMoney = (TextView) findViewById(R.id.walletmoney);
//        moneyAdd = (ImageView) findViewById(R.id.moneyadd);
//        earnMore = (TextView) findViewById(R.id.earnmorepoint);
        message = (TextView) findViewById(R.id.message);
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);
      /*  moneyAdd.setOnClickListener(this);
        earnMore.setOnClickListener(this);*/
        backButton.setOnClickListener(this);
        context = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (CheckNet.checkNet(context)) {
            new FetchMoney().execute();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Request request = new Request.Builder()
                            .url(MESSAGETEXTURL)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();

                        final String responseString = response.body().string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                message.setText(responseString);

                            }
                        });

                        Log.d("money", responseString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.backbutton:
                finish();
                break;
           /* case R.id.moneyadd:
                startActivity(new Intent(this, Referral.class));
                break;*/
           /* case R.id.earnmorepoint:
                startActivity(new Intent(this, Referral.class));
                break;*/

        }

    }

    private class FetchMoney extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indicatorView.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String responseString = "";
            Request request = new Request.Builder()
                    .url(DHPOINTURL)
                    .addHeader("token", BaseApplication.getToken())
                    .build();

            try {
                Response response = client.newCall(request).execute();

                responseString = response.body().string();

                Log.d("money", responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String money) {
            super.onPostExecute(money);
            indicatorView.hide();
            walletMoney.setText(money);

        }

    }

}
