package freakydevelopers.dhobikart.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import freakydevelopers.dhobikart.fragment.SignInFrag;
import freakydevelopers.dhobikart.fragment.SignUpFrag;

/**
 * Created by PURUSHOTAM on 8/14/2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new SignInFrag();
        }
        return new SignUpFrag();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
