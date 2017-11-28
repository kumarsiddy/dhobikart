package freakydevelopers.dhobikart.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.LayoutInflater;
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
import static freakydevelopers.dhobikart.Resources.MyURL.USERCHANGEPWD;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;

public class ChangePassword extends AppCompatActivity {


    String phoneNo, newPass;
    EditText otp;
    TextView submit, resend, verifyNo;
    LayoutInflater inflater;
    EditText pwd, repwd;
    AlertDialog dialog;
    AVLoadingIndicatorView indicatorView;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpveification);
        submit = (TextView) findViewById(R.id.submit);
        resend = (TextView) findViewById(R.id.resend);
        resend.setClickable(false);
        verifyNo = (TextView) findViewById(R.id.verifyno);
        phoneNo = getIntent().getStringExtra("phone");
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);
        inflater = LayoutInflater.from(this);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password!!");

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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        verifyNo.setText("+91 " + phoneNo);
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
                    MyToast.showToast(context, "OTP Field is Empty!!");
                } else if (otpString.length() != 4) {
                    check = false;
                    MyToast.showToast(context, "OTP Field must have 4 Characters!!");
                } else {
                    check = true;
                }

                if (check && CheckNet.checkNet(context)) {
                    showPwdChangeDialog(otpString);
                }
            }
        });


    }

    private void setTimer() {
        resend.setEnabled(false);
        resend.setTextColor(ContextCompat.getColor(ChangePassword.this, R.color.gray));
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

                final float scale = ChangePassword.this.getResources().getDisplayMetrics().density;
                int pixels = (int) (120 * scale + 0.5f);

                resend.setClickable(true);
                resend.setText("Resend via call");
                resend.setWidth(pixels);
                resend.setTextColor(ContextCompat.getColor(ChangePassword.this, R.color.white));
                resend.setEnabled(true);
            }
        }.start();
    }


    class changePassword extends AsyncTask<String, Void, String> {

        JSONObject jsonObject = new JSONObject();

//        ProgressDialog p1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            indicatorView.show();

            try {
                jsonObject.put("userName", phoneNo);
                jsonObject.put("passWord", newPass);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(USERCHANGEPWD)
                        .addHeader("otp", strings[0])
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
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            indicatorView.hide();
            if (string.equals("success")) {
                MyToast.showToast(context, "Your Password has been changed!!");
                finish();
            } else if (string.equals("wrongotp")) {
                MyToast.showToast(context, "Wrong OTP entered!!");
            } else {
                MyToast.showToast(context, "Please Try Again Later!!");
            }
        }
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
                        .addHeader("phone", phoneNo)
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
            if (s.equals("success")) {
                MyToast.showToast(context, "Successfully initiated call for OTP !!");
            } else {
                MyToast.showToast(context, "Failed to Sent Try Again !!");
            }
        }
    }

    public void showPwdChangeDialog(final String otpString) {
        View dialogView;
        dialogView = inflater.inflate(R.layout.change_password, null);
        pwd = (EditText) dialogView.findViewById(R.id.pwd);
        repwd = (EditText) dialogView.findViewById(R.id.repwd);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Reset Password");
        dialogBuilder.setMessage("Please Enter Your Password to reset!!");
        dialogBuilder.setPositiveButton("CHANGE PASSWORD", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pass = pwd.getText().toString();
                String repass = repwd.getText().toString();
                boolean check, checkLength;
                if (pass.length() < 6) {
                    checkLength = false;
                    MyToast.showToast(context, "Password must be 6 characters!!");
                } else {
                    checkLength = true;
                }
                if (check = !pass.equals(repass)) {
                    MyToast.showToast(context, "Password not Matching!!");
                }
                if (CheckNet.checkNet(context) && !check && checkLength) {
                    newPass = pass;
                    new changePassword().execute(otpString);
                    dialog.dismiss();
                }
            }
        });
        dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}