package freakydevelopers.dhobikart.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import freakydevelopers.dhobikart.Interfaces.CustomMessenger;
import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.Logger;
import freakydevelopers.dhobikart.Resources.MyToast;
import freakydevelopers.dhobikart.activity.BaseApplication;
import freakydevelopers.dhobikart.fragment.PendingOrders;
import freakydevelopers.dhobikart.pojo.OrderSummary;
import freakydevelopers.dhobikart.pojo.OrderedCloth;
import okhttp3.Request;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.ORDERCANCEL;
import static freakydevelopers.dhobikart.Resources.MyURL.ORDERCLOTHES;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;

/**
 * Created by PURUSHOTAM on 11/21/2016.
 */

public class OrderStatusMainAdapter extends RecyclerView.Adapter<OrderStatusMainAdapter.MyViewHolder> {

    private List<OrderSummary> orderSummaries;
    private Context context;
    private static CustomMessenger customMessenger1, customMessenger2;


    public OrderStatusMainAdapter(Context context, List<OrderSummary> orderSummaries, Fragment fragment) {
        this.context = context;
        this.orderSummaries = orderSummaries;
        if (fragment instanceof PendingOrders)
            customMessenger1 = (CustomMessenger) fragment;
        else
            customMessenger2 = (CustomMessenger) fragment;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycleorderdetails, parent, false);
        return new OrderStatusMainAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final OrderSummary order = orderSummaries.get(position);
        String dateObject = order.getTime();
        String delieverystatus;

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = null;// converting String to date
        try {
            date = df.parse(dateObject);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        if (order.isDelivered() && order.isCancelled()) {
            delieverystatus = "CANCELLED";
        } else if (order.isDelivered()) {
            delieverystatus = "DELIVERED";
        } else {
            delieverystatus = "PENDING";
        }


        holder.orderDate.setText("" + df.format(date));
        holder.orderNo.setText(order.getOrderId());
        holder.totalPay.setText(String.valueOf(order.getTotalPrice()));
        holder.status.setText(delieverystatus);
        holder.listView.setAdapter(null);

        if (isCanclable(order.getTime()) && !order.isCancelled())
            holder.cancelButton.setVisibility(View.VISIBLE);
        else
            holder.cancelButton.setVisibility(View.GONE);

        setListViewHeightBasedOnChildren(holder.listView);
    }

    @Override
    public int getItemCount() {
        return orderSummaries.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView orderNo, totalPay, orderDate, orderTime, status, details;
        ListView listView;
        Button cancelButton;
        private int count = 0;

        public MyViewHolder(View itemView) {
            super(itemView);
            orderNo = (TextView) itemView.findViewById(R.id.orderno);
            orderDate = (TextView) itemView.findViewById(R.id.orderdate);
            orderTime = (TextView) itemView.findViewById(R.id.time);
            status = (TextView) itemView.findViewById(R.id.status);
            listView = (ListView) itemView.findViewById(R.id.clothList);
            totalPay = (TextView) itemView.findViewById(R.id.totalpayable);
            details = (TextView) itemView.findViewById(R.id.detailsview);
            cancelButton = (Button) itemView.findViewById(R.id.cancel);
            details.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.detailsview:
                    count++;
                    if (count % 2 == 1) {
                        OrderSummary orderSummary = orderSummaries.get(getAdapterPosition());
                        new LoadClothes(listView).execute(orderSummary.getOrderId());
                    } else {
                        listView.setVisibility(ListView.GONE);
                    }
                    break;
                case R.id.cancel:
                    OrderSummary orderSummary = orderSummaries.get(getAdapterPosition());
                    new CancelOrder(orderSummary, getAdapterPosition()).execute(orderSummary.getOrderId(), orderSummary.getAddressId() + "");
                    cancelButton.setClickable(false);
                    break;
            }


        }
    }

    private class LoadClothes extends AsyncTask<String, Void, String> {

        private ListView listView;

        public LoadClothes(ListView listView) {
            this.listView = listView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String string = "";
            try {
                Request request = new Request.Builder()
                        .url(ORDERCLOTHES)
                        .addHeader("token", BaseApplication.getToken())
                        .addHeader("orderId", strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                string = response.body().string();
                Logger.d(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return string;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);

            Logger.d(string);
            List<OrderedCloth> cloths;
            Gson gson = new Gson();
            Type type = new TypeToken<List<OrderedCloth>>() {
            }.getType();
            cloths = gson.fromJson(string, type);
            listView.setVisibility(ListView.VISIBLE);
            CustomListOrderAdapter adapter = new CustomListOrderAdapter(context, cloths);
            listView.setAdapter(adapter);
            setListViewHeightBasedOnChildren(listView);
            adapter.notifyDataSetChanged();
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = 0;
            listView.setLayoutParams(params);
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    private boolean isCanclable(String time) {
        DateTime startDate = DateTime.parse(time,
                DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss"));

        DateTimeZone zone = DateTimeZone.forID("Asia/Kolkata");
        DateTime dateTime = new DateTime(zone);
        int minutes = Minutes.minutesBetween(startDate, dateTime).getMinutes();
        Logger.d("Start Time " + time + " Minutes " + minutes + " Today time" + dateTime.toString());
        return Math.abs(minutes) < 11;
    }

    private class CancelOrder extends AsyncTask<String, Void, String> {

        private OrderSummary orderSummary;
        private int position;

        public CancelOrder(OrderSummary orderSummary, int position) {
            this.orderSummary = orderSummary;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String string = "";
            try {
                Request request = new Request.Builder()
                        .url(ORDERCANCEL)
                        .addHeader("token", BaseApplication.getToken())
                        .addHeader("orderId", strings[0])
                        .addHeader("addressId", strings[1])
                        .build();
                Response response = client.newCall(request).execute();
                string = response.body().string();
                Logger.d(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return string;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            if (string.equals("success")) {
                MyToast.showToast(context, "Successfully Cancelled !!");
                customMessenger1.refreshRecyclerView(orderSummary, position);
                customMessenger2.refreshRecyclerView(orderSummary, position);
            }
        }
    }


}
