package freakydevelopers.dhobikart.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.Logger;
import freakydevelopers.dhobikart.Resources.MyToast;
import freakydevelopers.dhobikart.adapter.AddressAdapter;
import freakydevelopers.dhobikart.connection.CheckNet;
import freakydevelopers.dhobikart.connection.DividerItemDecoration;
import freakydevelopers.dhobikart.pojo.Address;
import okhttp3.Request;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.ADDRESSGET;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;

public class AddressActivity extends AppCompatActivity {

    TextView newAddress, pickupHere, emptyText;
    AVLoadingIndicatorView indicatorView;
    RecyclerView recyclerView;
    private List<Address> adds = new ArrayList<>();
    boolean fTime = true;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        newAddress = (TextView) findViewById(R.id.addNew);
        pickupHere = (TextView) findViewById(R.id.deliverHere);
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        emptyText = (TextView) findViewById(R.id.empty_text);
        context = this;
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pick Up Address");

    }


    @Override
    protected void onStart() {
        super.onStart();

        new GetResponse().execute(ADDRESSGET);

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


        newAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddressActivity.this, NewAddress.class));
            }
        });


        pickupHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckNet.checkNet(context)) {

                    Intent intent = new Intent(AddressActivity.this, PickupActivity.class);
                    try {
                        Address address = adds.get(AddressAdapter.lastCheckedPosition);
                        intent.putExtra("address", address);
                        startActivity(intent);
                    } catch (Exception e) {
                        MyToast.showToast(context, "Please Add an Address!!");
                    }

                }

            }
        });

    }

    class GetResponse extends AsyncTask<String, Void, String> {
        private List<Address> addresses;

        int total = 0;
        int noOfCloth = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indicatorView.show();
        }

        @Override
        protected String doInBackground(String... url) {
            String responseString = null;
            Request request = new Request.Builder()
                    .url(url[0])
                    .addHeader("token", BaseApplication.getToken())
                    .build();

            try {
                Response response = client.newCall(request).execute();
                responseString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            Logger.d("Address Response" + responseString);
            AddressAdapter addressAdapter;
            Gson gson = new Gson();
            Type type = new TypeToken<List<Address>>() {
            }.getType();
            addresses = gson.fromJson(responseString, type);

            if (addresses.size() == 0) {
                emptyText.setVisibility(View.VISIBLE);
                adds = addresses;
            } else {
                emptyText.setVisibility(View.GONE);
                adds = addresses;
            }

            fTime = false;
            addressAdapter = new AddressAdapter(AddressActivity.this, addresses);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AddressActivity.this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(addressAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(5));
            indicatorView.hide();

        }
    }

}
