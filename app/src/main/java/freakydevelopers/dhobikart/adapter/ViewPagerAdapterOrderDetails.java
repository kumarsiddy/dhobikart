package freakydevelopers.dhobikart.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.Serializable;
import java.util.List;

import freakydevelopers.dhobikart.fragment.DelieveredOrders;
import freakydevelopers.dhobikart.fragment.PendingOrders;
import freakydevelopers.dhobikart.pojo.OrderSummary;

/**
 * Created by PURUSHOTAM on 3/2/2017.
 */

public class ViewPagerAdapterOrderDetails extends FragmentStatePagerAdapter {

    List<OrderSummary> delievered;
    List<OrderSummary> pending;

    public ViewPagerAdapterOrderDetails(FragmentManager fm, List<OrderSummary> p, List<OrderSummary> d) {
        super(fm);
        delievered = d;
        pending = p;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", (Serializable) pending);

            PendingOrders pendingOrders = new PendingOrders();
            pendingOrders.setArguments(bundle);
            return pendingOrders;
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", (Serializable) delievered);

            DelieveredOrders delieveredOrders = new DelieveredOrders();
            delieveredOrders.setArguments(bundle);
            return delieveredOrders;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
