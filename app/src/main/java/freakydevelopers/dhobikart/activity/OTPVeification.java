package freakydevelopers.dhobikart.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.MyToast;
import freakydevelopers.dhobikart.connection.CheckNet;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.JSON;
import static freakydevelopers.dhobikart.Resources.MyURL.OTPRESEND;
import static freakydevelopers.dhobikart.Resources.MyURL.USERSIGNUP;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;

public class OTPVeification extends AppCompatActivity {

    String phoneNo;
    EditText otp;
    TextView submit, resend, verifyNo;
    Context context;
    JSONObject jsonObject;
    AVLoadingIndicatorView indicatorView;
    private final static String PHONE_HEADER = "phone";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpveification);
        submit = (TextView) findViewById(R.id.submit);
        resend = (TextView) findViewById(R.id.resend);
        resend.setClickable(false);
        verifyNo = (TextView) findViewById(R.id.verifyno);
        phoneNo = getIntent().getStringExtra("phoneNo");

        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("OTP Verification!!");

        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("jsonObject"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //This will bipass the phone verification and proceed for register
       /* if (new CheckNet(OTPVeification.this).checkNet()) {
            new Signup().execute(jsonObject);
        }*/


        otp = (EditText) findViewById(R.id.otpentry);
        context = this;


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckNet.checkNet(context)) {

                    new ReSendOTP().execute();

                    MyToast.showToast(context, "OTP has been sent to your number again!!");

                }
                setTimer();
            }
        });
        setTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        verifyNo.setText("+91 " + phoneNo);

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

    @Override
    protected void onResume() {
        super.onResume();


        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        otp.setFilters(FilterArray);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String otpString = otp.getText().toString();
                boolean check = false;
                if (otpString.equals("")) {
                    check = false;
                    MyToast.showToast(context, "OTP field is empty!!");

                } else if (otpString.length() != 4) {
                    check = false;

                    MyToast.showToast(context, "OTP field must have 4 digits!!");

                } else {
                    check = true;
                }

                if (check && CheckNet.checkNet(context)) {

                    try {
                        jsonObject.put("otp", otpString);
                        new VerifyOTP().execute(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private void setTimer() {
        resend.setEnabled(false);
        resend.setTextColor(ContextCompat.getColor(OTPVeification.this, R.color.gray));
        new CountDownTimer(30000, 1000) {
            int secondsLeft = 0;

            public void onTick(long ms) {
                if (Math.round((float) ms / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round((float) ms / 1000.0f);
                    resend.setText("Resend via call ( " + secondsLeft + " )");
                }
            }

            @Override
            public void onFinish() {

                final float scale = OTPVeification.this.getResources().getDisplayMetrics().density;
                int pixels = (int) (120 * scale + 0.5f);

                resend.setClickable(true);
                resend.setText("Resend via call");
                resend.setWidth(pixels);
                resend.setTextColor(ContextCompat.getColor(OTPVeification.this, R.color.white));
                resend.setEnabled(true);
            }
        }.start();
    }


    class ReSendOTP extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indicatorView.show();
        }

        @Override
        protected String doInBackground(Void... voids) {


            try {
                Request request = new Request.Builder()
                        .addHeader(PHONE_HEADER, phoneNo)
                        .url(OTPRESEND)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            indicatorView.hide();

            Log.d("Value", s);

            if (s.equals("success")) {
                MyToast.showToast(context, "Successfully initiated call for OTP !!");
            } else {
                MyToast.showToast(context, "Failed to Sent Try Again !!");
            }


        }
    }


    class VerifyOTP extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indicatorView.show();
            submit.setClickable(false);
        }

        @Override
        protected String doInBackground(JSONObject... jsonObjects) {


            try {
                RequestBody body = RequestBody.create(JSON, jsonObjects[0].toString());
                Request request = new Request.Builder()
                        .url(USERSIGNUP)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject responseObject = null;
            try {
                responseObject = new JSONObject(s);
                Log.d("Value of SignUpresponse", s);
                if (responseObject.getBoolean("success")) {
                    Intent intent = new Intent(context, ClothActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    SharedPreferences sharedPref = context.getSharedPreferences(
                            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    try {
                        editor.putBoolean(context.getString(R.string.haveLoggedIn), true);
                        editor.putString(context.getString(R.string.saved_name), jsonObject.getString("name"));
                        editor.putString(context.getString(R.string.saved_email), jsonObject.getString("userEmail"));
                        editor.putString(context.getString(R.string.gender), jsonObject.getString("gender"));
                        editor.putString(context.getString(R.string.token), responseObject.getString("token"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    editor.apply();

                    MyToast.showToast(context, "Welcome");

                } else if (responseObject.getString("otpCorrect").equals("error")) {
                    MyToast.showToast(context, "Wrong OTP ! Please Enter Again !");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            submit.setClickable(true);
            indicatorView.hide();

        }
    }
}
