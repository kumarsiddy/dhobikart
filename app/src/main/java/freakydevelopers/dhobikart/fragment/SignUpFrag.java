package freakydevelopers.dhobikart.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.MyToast;
import freakydevelopers.dhobikart.activity.OTPVeification;
import freakydevelopers.dhobikart.activity.Refercheck;
import freakydevelopers.dhobikart.connection.CheckNet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.JSON;
import static freakydevelopers.dhobikart.Resources.MyURL.OTPGET;

public class SignUpFrag extends Fragment {

    private Button signUpBtn;

    private Context context;
    EditText name, email, phone, password, repassword;
    private String fName, mail, phoneNo, gender, passWord, rePassWord, referalCode;
    private View view;
    RadioGroup radioGroup;
    RadioButton maleButton, femaleButton;
    public CheckNet checkNet;
    private static boolean isPhoneVerified = false;
    AVLoadingIndicatorView indicatorView;
    LayoutInflater inflater;

    public SignUpFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        signUpBtn = (Button) view.findViewById(R.id.signup);
        name = (EditText) view.findViewById(R.id.firstName);
        email = (EditText) view.findViewById(R.id.email);
        phone = (EditText) view.findViewById(R.id.phone);
        password = (EditText) view.findViewById(R.id.password);
        repassword = (EditText) view.findViewById(R.id.repassword);
//        referalcode = (EditText) view.findViewById(R.id.referalcode);
        //radioGroup= (RadioGroup) view.findViewById(R.id.gender);
        maleButton = (RadioButton) view.findViewById(R.id.male);
        femaleButton = (RadioButton) view.findViewById(R.id.female);
        indicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
        maleButton.setChecked(true);
        maleButton.setTextColor(Color.WHITE);
    }

    @Override
    public void onResume() {
        super.onResume();
        //To change the color of radio button

        maleButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                maleButton.setTextColor(Color.WHITE);
                femaleButton.setTextColor(Color.BLACK);
                return false;
            }
        });

        femaleButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                femaleButton.setTextColor(Color.WHITE);
                maleButton.setTextColor(Color.BLACK);
                return false;
            }
        });

/*
        referalcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(context, Refercheck.class), 1);

            }
        });*/


        signUpBtn.setOnClickListener(new View.OnClickListener()

                                     {

                                         boolean isPassMatched = false;//To check for password mismatch
                                         boolean isValidName = false;//To check for field data is empty or not
                                         boolean isValidMail = false;//To check for field data is empty or not
                                         boolean isValidPhone = false;//To check for field data is empty or not
                                         boolean isPassEmpty = true;//To check Password is of length 6
                                         JSONObject jsonObject;

                                         @Override
                                         public void onClick(View view) {

                                             InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                             imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                                             fName = name.getText().toString();
                                             mail = email.getText().toString();
                                             phoneNo = phone.getText().toString();
                                             passWord = password.getText().toString();
                                             rePassWord = repassword.getText().toString();
//                                             referalCode = referalcode.getText().toString();

//                                             Log.d("Refer code is", referalCode);


                                             if (!(isValidName = (fName.length() > 1))) {

                                                 name.setError("First Name should not empty!");
                                             }


                                             if (!(isPassEmpty = (passWord.length() > 5))) {
                                                 password.setError("password must 6 characters!");
                                             }

                                             if (!(isValidMail = isValidEmail(mail))) {
                                                 email.setError("Email is not valid!");
                                             }

                                             if (!(isValidPhone = (phoneNo.length() == 10))) {
                                                 phone.setError("Phone No. is not valid!");
                                             }

                                             if (!(isPassMatched = passWord.equals(rePassWord))) {

                                                 repassword.setError("Password should match!");

                                             }
                                             if (femaleButton.isChecked()) {
                                                 gender = "Female";
                                             }

                                             if (maleButton.isChecked()) {
                                                 gender = "Male";
                                             }

                                             if (CheckNet.checkNet(context) && isValidName && isValidMail && isValidPhone && isPassEmpty && isPassMatched) {

                                                 try {
                                                     jsonObject = new JSONObject();
                                                     jsonObject.put("userEmail", mail);
                                                     jsonObject.put("password", passWord);
                                                     jsonObject.put("phoneNo", phoneNo);
                                                     jsonObject.put("name", fName);
                                                     jsonObject.put("gender", gender);
                                                     jsonObject.put("referCode", referalCode);
                                                     jsonObject.put("deviceId", Refercheck.getMacAddress(context));
                                                 } catch (JSONException e) {
                                                     e.printStackTrace();
                                                 }
                                                 new checkDetails().execute(jsonObject);
                                             }
                                         }
                                     }

        );


    }


    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    class checkDetails extends AsyncTask<JSONObject, Void, String> {
        String signUpJSONObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indicatorView.show();
        }

        @Override
        protected String doInBackground(JSONObject... json) {

            signUpJSONObject = json[0].toString();

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, json[0].toString());

                Log.d("Value", json[0].toString());

                Request request = new Request.Builder()
                        .url(OTPGET)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return "sid";
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            Log.d("Value", response);

            indicatorView.hide();
            JSONObject jsonObject;
            boolean phoneExist = false, mailExist = false, success = false;
            boolean hasOtpSent = false;
            try {
                jsonObject = new JSONObject(response);
                success = jsonObject.getBoolean("success");
                mailExist = jsonObject.getBoolean("mailExist");
                phoneExist = jsonObject.getBoolean("phoneExist");
                hasOtpSent = jsonObject.getString("hasOtpSent").equals("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (success && !phoneExist && !mailExist && hasOtpSent) {

                MyToast.showToast(context, "OTP has sent to your Number!!");

                Intent intent = new Intent(context, OTPVeification.class);
                intent.putExtra("phoneNo", phoneNo);
                intent.putExtra("jsonObject", signUpJSONObject);
                startActivity(intent);

            } else if (phoneExist && !mailExist) {

                MyToast.showToast(context, "Phone No. is already registered!!!");

            } else if (!phoneExist && mailExist) {

                MyToast.showToast(context, "Mail is already registered!!!");

            } else if (phoneExist && mailExist) {

                MyToast.showToast(context, "Both Mail and Phone Exist!!!");

            } else if (!hasOtpSent) {
                MyToast.showToast(context, "OTP could not send Please try Again!!");
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

//            referalcode.setText(data.getStringExtra("CODE"));

        }

    }
}
