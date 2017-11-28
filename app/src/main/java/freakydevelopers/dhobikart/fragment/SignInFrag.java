package freakydevelopers.dhobikart.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.dialog.widget.popup.BubblePopup;

import org.json.JSONException;
import org.json.JSONObject;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.Logger;
import freakydevelopers.dhobikart.Resources.MyToast;
import freakydevelopers.dhobikart.activity.ChangePassword;
import freakydevelopers.dhobikart.activity.ClothActivity;
import freakydevelopers.dhobikart.connection.CheckNet;
import freakydevelopers.dhobikart.connection.PhoneFormatException;
import freakydevelopers.dhobikart.connection.PhoneModel;
import freakydevelopers.dhobikart.connection.PhoneNumberVerifier;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.JSON;
import static freakydevelopers.dhobikart.Resources.MyURL.OTPRESEND;
import static freakydevelopers.dhobikart.Resources.MyURL.OTPWITHPHONE;
import static freakydevelopers.dhobikart.Resources.MyURL.USEREXIST;
import static freakydevelopers.dhobikart.Resources.MyURL.USERSIGNIN;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFrag extends Fragment {

    private static final String PHONE_NO = "55555";
    private View view;
    private EditText email, password;
    String mail, pass, phoneNO;
    Button signin;
    JSONObject jsonObject;
    private TextView forgot;
    Context context;

    private static AlertDialog dialog;
    LayoutInflater inflater;
    ProgressDialog progressDialog;

    View dialogView;
    EditText edt;

    public SignInFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        this.inflater = inflater;
        // inflater = LayoutInflater.from(context);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        signin = (Button) view.findViewById(R.id.signin);
        forgot = (TextView) view.findViewById(R.id.forgot);
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");
    }

    @Override
    public void onResume() {
        super.onResume();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isPassempty;//To check for the Field is empty
                boolean isValidMail;

                Logger.d("Fragemene" + USEREXIST);
                Logger.d("Fragemene" + USERSIGNIN);
                Logger.d("Fragemene" + OTPRESEND);

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                mail = email.getText().toString();
                pass = password.getText().toString();
                if (!(isValidMail = isValidEmail(mail)) && !(isValidMail = isPhoneValid(mail))) {
                    email.setError("Not valid Input!");

                }

                if ((isPassempty = pass.isEmpty())) {
                    password.setError("password is Empty!!");
                }

                if (CheckNet.checkNet(context) && isValidMail && !isPassempty) {

                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put("userName", mail);
                        jsonObject.put("passWord", pass);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SignIn().execute(jsonObject);

                }


            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckNet.checkNet(context);

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                dialogView = inflater.inflate(R.layout.dialog_forgot, null);
                edt = (EditText) dialogView.findViewById(R.id.edit1);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

                if (dialogView.getParent() == null) {
                    dialogBuilder.setView(dialogView);
                } else {
                    dialogView = null; //set it to null
                    // now initialized yourView and its component again
                    dialogView = inflater.inflate(R.layout.dialog_forgot, null);
                    edt = (EditText) dialogView.findViewById(R.id.edit1);
                    dialogBuilder.setView(dialogView);
                }

                edt.setFocusableInTouchMode(true);
                edt.setFocusable(true);

                dialogBuilder.setTitle("Reset Password");
                dialogBuilder.setMessage("Please Enter Your Phone No to reset the password!");
                dialogBuilder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String phone = edt.getText().toString();
                        boolean chec = false;

                        if (isPhoneValid(phone) && phone.length() == 10) {
                            chec = true;

                        } else {
                            chec = false;
                            MyToast.showToast(context, "Make sure Phone No. is Right!!");
                        }

                        if (CheckNet.checkNet(context) && chec) {
                            phoneNO = phone;
                            new checkAndSend().execute();
                            dialog.dismiss();
                        }


                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                dialog = dialogBuilder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }
        });


    }

    private class SignIn extends AsyncTask<JSONObject, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(JSONObject... jsonObjects) {
            String json = jsonObjects[0].toString();

            try {
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url(USERSIGNIN)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Error";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Value", s);
            try {
                SharedPreferences sharedPref = context.getSharedPreferences(
                        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                JSONObject jsonObject = new JSONObject(s);

                boolean success = jsonObject.getBoolean("success");
                boolean isUserExist = jsonObject.getBoolean("userExist");
                boolean isPassWrong = jsonObject.getBoolean("passWrong");

                if (success) {

                    MyToast.showToast(context, "Welcome Back");
                    editor.putBoolean(context.getString(R.string.haveLoggedIn), true);
                    editor.putString(context.getString(R.string.saved_name), jsonObject.getString("name"));
                    editor.putString(context.getString(R.string.saved_email), jsonObject.getString("mail"));
                    editor.putString(context.getString(R.string.gender), jsonObject.getString("gender"));
                    editor.putString(context.getString(R.string.token), jsonObject.getString("token"));
                    editor.apply();
                    Intent intent = new Intent(context, ClothActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getActivity().finish();
                    startActivity(intent);
                } else if (!isUserExist) {
                    showEmailInvalid("User doesn't Exist!");
                } else if (isPassWrong) {
                    showPasswordInvalid("Password is wrong!");
                }

                progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }
        }
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    private void showEmailInvalid(String message) {
        View inflate = View.inflate(context, R.layout.emailinvalid, null);
        TextView tv = (TextView) inflate.findViewById(R.id.tv_bubble);
        BubblePopup bubblePopup = new BubblePopup(context, inflate);
        tv.setText(message);
        bubblePopup.anchorView(email)
                .gravity(Gravity.TOP)
                .show();
    }

    private void showPasswordInvalid(String message) {
        View inflate = View.inflate(context, R.layout.passwordinvalid, null);
        TextView tv = (TextView) inflate.findViewById(R.id.tv_bubble);
        BubblePopup bubblePopup = new BubblePopup(context, inflate);
        tv.setText(message);
        bubblePopup.anchorView(password)
                .gravity(Gravity.BOTTOM)
                .show();
    }

    private boolean isPhoneValid(String phoneNo) {

        PhoneNumberVerifier.Countries country = PhoneNumberVerifier.Countries.India;
        try {
            PhoneModel model = country.isNumberValid(country, phoneNo);
            return model.isValidPhoneNumber();
        } catch (PhoneFormatException e) {
            e.printStackTrace();
        }

        return false;
    }


    class checkAndSend extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... json) {
            try {
                Request request = new Request.Builder()
                        .url(OTPWITHPHONE)
                        .addHeader("phone", phoneNO)
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
            Logger.d(response);
            JSONObject jsonObject;
            boolean phoneExist = false, success = false;
            boolean hasOtpSent = false;
            try {
                jsonObject = new JSONObject(response);
                success = jsonObject.getBoolean("success");
                phoneExist = jsonObject.getBoolean("phoneExist");
                hasOtpSent = jsonObject.getString("hasOtpSent").equals("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (success && phoneExist && hasOtpSent) {

                MyToast.showToast(context, "OTP has sent to your Number!!");
                Intent intent = new Intent(context, ChangePassword.class);
                intent.putExtra("phone", phoneNO);
                startActivity(intent);
            } else if (!phoneExist) {
                MyToast.showToast(context, "Phone No. is Not registered!!!");
            } else if (!hasOtpSent) {
                MyToast.showToast(context, "OTP could not send Please try Again!!");
            }
            progressDialog.hide();
        }
    }


}
