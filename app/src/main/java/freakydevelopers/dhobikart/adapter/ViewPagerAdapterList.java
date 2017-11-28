package freakydevelopers.dhobikart.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import freakydevelopers.dhobikart.fragment.MenClothList;
import freakydevelopers.dhobikart.fragment.OtherClothList;
import freakydevelopers.dhobikart.fragment.WomenClothList;

/**
 * Created by PURUSHOTAM on 9/14/2016.
 */
public class ViewPagerAdapterList extends FragmentStatePagerAdapter {

    public ViewPagerAdapterList(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("Siddhartha", "" + position);
        if (position == 0) {
            return new MenClothList();
        } else if (position == 1) {
            return new WomenClothList();
        } else if (position == 2) {
            return new OtherClothList();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
