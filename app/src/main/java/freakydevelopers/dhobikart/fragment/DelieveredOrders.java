package freakydevelopers.dhobikart.fragment;

import android.content.Context;
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

public class DelieveredOrders extends Fragment implements CustomMessenger {

    private RecyclerView recyclerView;
    private OrderStatusMainAdapter adapter;
    AVLoadingIndicatorView indicatorView;
    private List<OrderSummary> delieveredOrderSummaries;
    TextView no_Orders;
    private View view;
    private Context context;
    private RecyclerView.LayoutManager mLayoutManager;

    public DelieveredOrders() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pending_orders, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        indicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
        no_Orders = (TextView) view.findViewById(R.id.noorders);
        Bundle bundle = getArguments();
        delieveredOrderSummaries = (List<OrderSummary>) bundle.getSerializable("list");
        context = getActivity();
        mLayoutManager = new LinearLayoutManager(context);
        if (delieveredOrderSummaries.isEmpty()) {
            no_Orders.setVisibility(View.VISIBLE);
            no_Orders.setText("No Delivered Orders!!");
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter = new OrderStatusMainAdapter(context, delieveredOrderSummaries, DelieveredOrders.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(5));
    }

    @Override
    public void refreshRecyclerView(OrderSummary orderSummary, int position) {
        orderSummary.setCancelled(true);
        orderSummary.setDelivered(true);
        delieveredOrderSummaries.add(0, orderSummary);
        adapter.notifyDataSetChanged();
    }
}
