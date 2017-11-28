package freakydevelopers.dhobikart.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.DateComparator;
import freakydevelopers.dhobikart.Resources.Logger;
import freakydevelopers.dhobikart.adapter.ViewPagerAdapterOrderDetails;
import freakydevelopers.dhobikart.connection.CheckNet;
import freakydevelopers.dhobikart.pojo.OrderSummary;
import okhttp3.Request;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.ORDERGET;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;

public class OrderDetails extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapterOrderDetails viewPagerAdapter;
    private Toolbar toolbar;
    private TextView no_Orders;
    private AVLoadingIndicatorView indicatorView;
    private ImageView backButton;
    private boolean isFirstTime = true;
    private List<OrderSummary> delieveredorders = new ArrayList<>();
    private List<OrderSummary> pendingorders = new ArrayList<>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager1);
        tabLayout = (TabLayout) findViewById(R.id.tab1);
        backButton = (ImageView) findViewById(R.id.backbutton);
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);
        no_Orders = (TextView) findViewById(R.id.noorders);
        context = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        no_Orders.setVisibility(View.VISIBLE);
        if (CheckNet.checkNet(context)) {

            new LoadClothList().execute();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    class LoadClothList extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indicatorView.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String string = "";
            try {
                Request request = new Request.Builder()
                        .url(ORDERGET)
                        .addHeader("token", BaseApplication.getToken())
                        .build();
                Response response = client.newCall(request).execute();
                string = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return string;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Logger.d("Order Details" + string);
            Gson gson = new Gson();
            Type type = new TypeToken<List<OrderSummary>>() {
            }.getType();
            List<OrderSummary> orderSummaries = gson.fromJson(string, type);
            Collections.sort(orderSummaries, new DateComparator());

            Logger.d(string + "Size of List" + orderSummaries.size());

            no_Orders.setVisibility(View.GONE);

            for (OrderSummary orderSummary : orderSummaries) {
                if (!string.equals("[null]") && orderSummary.isDelivered())
                    delieveredorders.add(orderSummary);
                else if (!string.equals("[null]"))
                    pendingorders.add(orderSummary);
            }

            viewPagerAdapter = new ViewPagerAdapterOrderDetails(getSupportFragmentManager(), pendingorders, delieveredorders);
            viewPager.setAdapter(viewPagerAdapter);
            if (isFirstTime) {
                final TabLayout.Tab pending = tabLayout.newTab();
                final TabLayout.Tab delievered = tabLayout.newTab();
                pending.setText("Pending");
                delievered.setText("Delivered");
                //Creating the new tabs
                tabLayout.addTab(pending, 0);
                tabLayout.addTab(delievered, 1);
                tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(OrderDetails.this, R.color.white));
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {

                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {


                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                indicatorView.hide();
                isFirstTime = false;
            }
        }
    }


}
