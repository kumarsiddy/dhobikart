package freakydevelopers.dhobikart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.MyToast;
import freakydevelopers.dhobikart.connection.CheckNet;
import freakydevelopers.dhobikart.pojo.Address;
import okhttp3.Request;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.ORDERPLACE;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;

public class PickupActivity extends AppCompatActivity {

    TextView name, addressdetails, landmark, phone, alterphone, noofitems, total, totalPayable, dcpointtext, dcpoint, dhmoneytext, dhmoneyrupee, dhmoneytotal;
    Button placeOrder;
    Address address;
    CheckBox selectcod, dcpointselect;
    boolean iscodselected = false;
    AVLoadingIndicatorView indicatorView;
    ScrollView scroll;
    String DCpoint;
    int dhmoney, cartTotal;
    boolean isFirstOrder = false;
    String token;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);

        name = (TextView) findViewById(R.id.name);
        addressdetails = (TextView) findViewById(R.id.address1);
        landmark = (TextView) findViewById(R.id.landmark);
        phone = (TextView) findViewById(R.id.phone);
        alterphone = (TextView) findViewById(R.id.alterPhone);
        noofitems = (TextView) findViewById(R.id.noofitems);
//        total = (TextView) findViewById(R.id.total);
        totalPayable = (TextView) findViewById(R.id.totalpayable);
        selectcod = (CheckBox) findViewById(R.id.codselect);
        placeOrder = (Button) findViewById(R.id.placeorder);
//        dcpoint = (TextView) findViewById(R.id.dcpoint);
//        dhmoneytext = (TextView) findViewById(R.id.dhmoneytext);
//        dhmoneyrupee = (TextView) findViewById(R.id.dhmoneyrupee);
//        dhmoneytotal = (TextView) findViewById(R.id.dhmoneytotal);
//        dcpointselect = (CheckBox) findViewById(R.id.dcpointselect);
//        dcpointtext = (TextView) findViewById(R.id.dcpointtext);
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);
        scroll = (ScrollView) findViewById(R.id.scroll);
        Intent intent = getIntent();
        address = (Address) intent.getSerializableExtra("address");

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        token = sharedPref.getString(getString(R.string.token), "");
        context = this;
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Summary");

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (address != null) {
            name.setText(address.getName());
            addressdetails.setText(address.getArea() + System.getProperty("line.separator") +
                    address.getCity() + "," +
                    address.getState() + "-" + address.getPin());
            landmark.setText(address.getLandmark());
            phone.setText(address.getPhone());
            alterphone.setText(address.getAlterphone());
            phone.setText(address.getPhone());
            noofitems.setText(String.valueOf(BaseApplication.getNoOfCartItems()));
//            total.setText(String.valueOf(cartTotal = BaseApplication.getTotalprice()));
            totalPayable.setText(String.valueOf(cartTotal= BaseApplication.getTotalprice()));
        } else {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();


        selectcod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectcod.isChecked()) {

                    iscodselected = true;

                } else {
                    iscodselected = false;
                }

            }
        });

        /*dcpointselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dcpointselect.isChecked()) {

                    dhmoneytext.setVisibility(View.VISIBLE);
                    dhmoneyrupee.setVisibility(View.VISIBLE);
                    dhmoneytotal.setVisibility(View.VISIBLE);
                    checkPayable();


                } else {

                    dhmoneytext.setVisibility(View.GONE);
                    dhmoneyrupee.setVisibility(View.GONE);
                    dhmoneytotal.setVisibility(View.GONE);
                    totalPayable.setText(String.valueOf(cartTotal));
                }

            }
        });*/

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean checkCost = false;
                if (BaseApplication.getTotalprice() >= 50) {
                    checkCost = true;
                } else {
                    checkCost = false;

                    MyToast.showToast(context, "Minimum Order Value must be of Rs.50");

                }

                if (!iscodselected) {

                    MyToast.showToast(context, "Please select Payment mode!");
                    scroll.fullScroll(View.FOCUS_DOWN);

                }

                if (CheckNet.checkNet(context) && checkCost && iscodselected) {

                    new PlaceOrder().execute();

                }
            }
        });


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


    class PlaceOrder extends AsyncTask<Void, Void, String> {

        int addressId = address.getId();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            placeOrder.setClickable(false);
            indicatorView.setVisibility(View.VISIBLE);
            indicatorView.show();

        }

        @Override
        protected String doInBackground(Void... voids) {
            String respo = "fail";
            Request request = new Request.Builder()
                    .url(ORDERPLACE)
                    .addHeader("token", token)
                    .addHeader("addressId", addressId + "")
                    .addHeader("isDHPoint", String.valueOf(false))
                    .build();

            try {
                Response response = client.newCall(request).execute();
                respo = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return respo;
        }


        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            Log.d("Value", "OrderPlaceResponse" + response);

            if (response.equals("success")) {

                MyToast.showToast(context, "Order successfully placed");

                Intent intent = new Intent(getApplicationContext(), ClothActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);

            } else {
                MyToast.showToast(context, "Please try again!!");
            }

            indicatorView.hide();

        }
    }

   /* private void checkPayable() {

        if (isFirstOrder && dhmoney >= 50) {

            dcpointtext.setVisibility(View.VISIBLE);
            dcpoint.setText("(₹" + dhmoney + ")");
            dcpoint.setVisibility(View.VISIBLE);
            dcpointselect.setChecked(true);
            dcpointselect.setVisibility(View.VISIBLE);

            dhmoneytext.setVisibility(View.VISIBLE);
            dhmoneytext.setText("DC Points(Only Rs.50 can be used.)");
            dhmoneyrupee.setVisibility(View.VISIBLE);
            dhmoneytotal.setText("₹ 50");
            dhmoneytotal.setVisibility(View.VISIBLE);
            totalPayable.setText(String.valueOf(Math.abs(cartTotal - 50)));


        } else if (isFirstOrder && dhmoney <= 25) {

            dcpointtext.setVisibility(View.VISIBLE);
            dcpoint.setText("(₹" + dhmoney + ")");
            dcpoint.setVisibility(View.VISIBLE);
            dcpointselect.setChecked(true);
            dcpointselect.setVisibility(View.VISIBLE);

            dhmoneytext.setVisibility(View.VISIBLE);
            dhmoneytext.setText("DC Points(Only Rs." + dhmoney + " can be used.)");
            dhmoneyrupee.setVisibility(View.VISIBLE);
            dhmoneytotal.setText("₹ 25");
            dhmoneytotal.setVisibility(View.VISIBLE);
            totalPayable.setText(String.valueOf(Math.abs(cartTotal - dhmoney)));

        } else if (!isFirstOrder && dhmoney >= 25 && cartTotal >= 25) {

            dcpointtext.setVisibility(View.VISIBLE);
            dcpoint.setText("(₹" + dhmoney + ")");
            dcpoint.setVisibility(View.VISIBLE);
            dcpointselect.setChecked(true);
            dcpointselect.setVisibility(View.VISIBLE);

            dhmoneytext.setVisibility(View.VISIBLE);
            dhmoneytext.setText("DC Points(Only Rs.25 can be used.)");
            dhmoneyrupee.setVisibility(View.VISIBLE);
            dhmoneytotal.setText("₹ 25");
            dhmoneytotal.setVisibility(View.VISIBLE);
            totalPayable.setText(String.valueOf(Math.abs(cartTotal - 25)));

        } else {

            totalPayable.setText(String.valueOf(BaseApplication.getTotalprice()));
            dhmoneytext.setVisibility(View.GONE);
            dhmoneyrupee.setVisibility(View.GONE);
            dhmoneytotal.setVisibility(View.GONE);

        }
        }*/


   /* private class CheckDCPoint extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indicatorView.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String authCode = sharedPref.getString(getString(R.string.token), "");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", authCode);
                jsonObject.put("pin", address.getPin());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(REFERWALLET)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String responseString = response.body().string();
                Log.d("money", responseString);
                return responseString;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);

            Log.d("SID", string);

            indicatorView.hide();
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(string);
                dhmoney = jsonObject.getInt("dhPoint");
                isFirstOrder = jsonObject.getBoolean("isFirstOrder");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            checkPayable();

        }
    }*/


}
