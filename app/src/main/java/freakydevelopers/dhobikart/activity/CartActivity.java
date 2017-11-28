package freakydevelopers.dhobikart.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.adapter.CartAdapter;
import freakydevelopers.dhobikart.connection.DividerItemDecoration;
import freakydevelopers.dhobikart.pojo.Cart;
import okhttp3.Request;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.CARTGETALL;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;


public class CartActivity extends AppCompatActivity {

    public AVLoadingIndicatorView indicatorView;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    public Button continueButton;
    public TextView totalPay, empty_text;
    private List<Cart> carts = new ArrayList<>();
    public ImageView emptyCart;
    private boolean fTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = (RecyclerView) findViewById(R.id.cartRecycle);
        totalPay = (TextView) findViewById(R.id.total);
        emptyCart = (ImageView) findViewById(R.id.empty_cart);
        empty_text = (TextView) findViewById(R.id.empty_text);
        continueButton = (Button) findViewById(R.id.continuee);
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cart");
        continueButton.setOnClickListener(new CartAdapter.MyClickListener());

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (fTime) {
            new GetResponse().execute();
            continueButton.setClickable(false);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

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


    class GetResponse extends AsyncTask<String, Void, String> {
        int total = 0;
        int noOfCloth = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indicatorView.show();
        }

        @Override
        protected String doInBackground(String... url) {
            String string = null;

            Request request = new Request.Builder()
                    .url(CARTGETALL)
                    .addHeader("token", BaseApplication.getToken())
                    .build();

            try {
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
            Log.d("Value", string);

            int total = 0;
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(string);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Cart cart = new Cart();
                    JSONObject jsonObject = null;

                    int a, b;
                    jsonObject = jsonArray.getJSONObject(i);
                    cart.setName(jsonObject.getString("clothName"));
                    cart.setPrice(a = jsonObject.getInt("clothPrice"));
                    cart.setNumber(b = jsonObject.getInt("totalNumber"));
                    total = total + (a * b);

                    carts.add(cart);
                }
            } catch (JSONException e) {
                empty_text.setVisibility(View.VISIBLE);
                emptyCart.setVisibility(View.VISIBLE);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            fTime = false;

            totalPay.setText("₹ " + total);

            cartAdapter = new CartAdapter(CartActivity.this, carts, total);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CartActivity.this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(cartAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(10));
            indicatorView.hide();
            continueButton.setClickable(true);

        }
    }

}
