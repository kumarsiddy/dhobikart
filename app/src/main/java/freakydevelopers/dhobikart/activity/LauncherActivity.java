package freakydevelopers.dhobikart.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.Logger;
import freakydevelopers.dhobikart.Resources.MyURL;
import okhttp3.OkHttpClient;

/**
 * Created by PURUSHOTAM on 9/22/2016.
 */

public class LauncherActivity extends Activity {
    boolean haveLoggedIn;
    public static OkHttpClient client;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        haveLoggedIn = sharedPreferences.getBoolean(getString(R.string.haveLoggedIn), false);
        client = new OkHttpClient();
        // Obtain the FirebaseAnalytics instance.
        context = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("baseurl");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String baseURL = dataSnapshot.getValue(String.class);
                MyURL.setBaseURL(baseURL);
                Logger.d(baseURL);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {

                if (haveLoggedIn) {
                    startActivity(new Intent(LauncherActivity.this, ClothActivity.class));
                } else {
//                    baseURL = "http://10.0.3.2:8080/";
                    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                }
                finish();

            }
        }, 3000);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "freaky.devs.dhobicart",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
               /* Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));*/
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
        }


    }
}
