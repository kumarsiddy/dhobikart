package freakydevelopers.dhobikart.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import freakydevelopers.dhobikart.Interfaces.CustomMessenger;
import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.activity.BaseApplication;
import freakydevelopers.dhobikart.adapter.OrderStatusMainAdapter;
import freakydevelopers.dhobikart.connection.DividerItemDecoration;
import freakydevelopers.dhobikart.pojo.OrderSummary;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingOrders extends Fragment implements CustomMessenger {

    private RecyclerView recyclerView;
    private OrderStatusMainAdapter adapter;
    AVLoadingIndicatorView indicatorView;
    private List<OrderSummary> pendingOrders;
    SharedPreferences sharedPref;
    TextView no_Orders;
    private View view;
    private Context context;
    private RecyclerView.LayoutManager mLayoutManager;


    public PendingOrders() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pending_orders, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        indicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
        no_Orders = (TextView) view.findViewById(R.id.noorders);
        context = getActivity();
        Bundle bundle = getArguments();
        pendingOrders = (List<OrderSummary>) bundle.getSerializable("list");
        mLayoutManager = new LinearLayoutManager(context);
        if (pendingOrders.isEmpty()) {
            no_Orders.setVisibility(View.VISIBLE);
            no_Orders.setText("No Pending Orders!!");
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter = new OrderStatusMainAdapter(context, pendingOrders, PendingOrders.this);
        mLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(5));

    }

    @Override
    public void refreshRecyclerView(OrderSummary orderSummary, int position) {
        pendingOrders.remove(position);
        recyclerView.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, pendingOrders.size());
    }
}
