package freakydevelopers.dhobikart.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.Logger;
import freakydevelopers.dhobikart.adapter.ViewPagerAdapter;

import static freakydevelopers.dhobikart.Resources.MyURL.USEREXIST;
import static freakydevelopers.dhobikart.Resources.MyURL.USERSIGNIN;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager1);
        tabLayout = (TabLayout) findViewById(R.id.tab1);
        Logger.d("1CURL"+USEREXIST);
        Logger.d("2CURL"+USERSIGNIN);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        final TabLayout.Tab signin = tabLayout.newTab();
        final TabLayout.Tab signup = tabLayout.newTab();

        signin.setText("Sign In");
        signup.setText("Sign Up");

        //Creating the new tabs
        tabLayout.addTab(signin, 0);
        tabLayout.addTab(signup, 1);


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

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("1RURL"+USEREXIST);
        Logger.d("2RURL"+USERSIGNIN);
    }
}
