package freakydevelopers.dhobikart.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import freakydevelopers.dhobikart.Interfaces.ClothActivityInterface;
import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.MyToast;
import freakydevelopers.dhobikart.adapter.ViewPagerAdapterList;
import freakydevelopers.dhobikart.connection.CheckNet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.CARTTOTAL;

public class ClothActivity extends AppCompatActivity implements ClothActivityInterface {

    private TabLayout tabLayout;
    private Context context;
    Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapterList viewPagerAdapter;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    public TextView cartnotice;
    String name = "New User", email = "newuser@gmail.com", gender, token;
    TextView user_name, user_email;
    ImageView profile;
    SharedPreferences sharedPref;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth);
        context = this;
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        cartnotice = (TextView) findViewById(R.id.cartnotice);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tab);

        View hView = navigationView.inflateHeaderView(R.layout.header);

        user_name = (TextView) hView.findViewById(R.id.user_name);
        user_email = (TextView) hView.findViewById(R.id.user_email);
        profile = (ImageView) hView.findViewById(R.id.profile_image);

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        name = sharedPref.getString(getString(R.string.saved_name), "New User");
        email = sharedPref.getString(getString(R.string.saved_email), "newuser@email.com");
        gender = sharedPref.getString(getString(R.string.gender), "male");
        token = sharedPref.getString(getString(R.string.token), "default");
        BaseApplication.setToken(token);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPagerAdapter = new ViewPagerAdapterList(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        final TabLayout.Tab men = tabLayout.newTab();
        final TabLayout.Tab women = tabLayout.newTab();
        final TabLayout.Tab others = tabLayout.newTab();
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;

                    // For rest of the options we just show a toast on click

                    case R.id.order_details:
                        startActivity(new Intent(ClothActivity.this, OrderDetails.class));
                        return true;


                  /*  case R.id.referandearn:
                        startActivity(new Intent(ClothActivity.this, Referral.class));
                        return true;*/

                    case R.id.wallet:
                        startActivity(new Intent(ClothActivity.this, Wallet.class));
                        return true;

                    case R.id.logout:

                        getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit().clear().apply();
                        MyToast.showToast(context, "You have been logged out!!");
                        Intent intent = new Intent(ClothActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(intent);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


        men.setText("MEN");
        women.setText("WOMEN");
        others.setText("OTHERS");

        //Creating the new tabs
        tabLayout.addTab(men, 0);
        tabLayout.addTab(women, 1);
        tabLayout.addTab(others, 2);


        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_text));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
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
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }


    @Override
    protected void onStart() {
        super.onStart();

        user_name.setText(name);
        user_email.setText(email);

        if (gender.equals("female")) {
            profile.setBackgroundResource(R.drawable.girl);
        } else {
            profile.setBackgroundResource(R.drawable.boy);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CheckNet.checkNet(context)) {
            fetchCartNo();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cloth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_cart:
                startActivity(new Intent(this, CartActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void fetchCartNo() {


        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(CARTTOTAL)
                            .addHeader("token", BaseApplication.getToken())
                            .build();
                    Response response = client.newCall(request).execute();
                    final String string = response.body().string();
                    Log.d("Value", string);
                    if (string.equals("null")) {
                        Log.d("Sid", "Inside1");
                        BaseApplication.setNoOfCartItems(0);
                    } else {
                        Log.d("Sid", "Inside2");
                        BaseApplication.setNoOfCartItems(Integer.valueOf(string));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cartnotice.setText(String.valueOf(BaseApplication.getNoOfCartItems()));
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        MyToast.showToast(context, "Please click BACK again to exit!!");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void restartActivity() {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

