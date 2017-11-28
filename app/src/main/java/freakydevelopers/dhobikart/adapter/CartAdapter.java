package freakydevelopers.dhobikart.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.MyToast;
import freakydevelopers.dhobikart.activity.AddressActivity;
import freakydevelopers.dhobikart.activity.BaseApplication;
import freakydevelopers.dhobikart.activity.CartActivity;
import freakydevelopers.dhobikart.pojo.Cart;
import freakydevelopers.dhobikart.connection.CheckNet;
import okhttp3.Request;

import static freakydevelopers.dhobikart.Resources.MyURL.CARTADDONE;
import static freakydevelopers.dhobikart.Resources.MyURL.CARTDELETE;
import static freakydevelopers.dhobikart.Resources.MyURL.CARTREMOVEONE;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {


    static Context context;
    private static AlertDialog dialog;
    MyViewHolder myViewHolder;
    private List<Cart> carts = new ArrayList<>();
    CartActivity cartActivity;
    static int total;


    public CartAdapter(Context context, List<Cart> carts, int total) {
        this.context = context;
        this.carts = carts;
        this.total = total;
        BaseApplication.setTotalprice(total);
        Log.d("Total Value:", "" + total);
        cartActivity = (CartActivity) context;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cartrecycle, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Cart cart = carts.get(position);
        holder.clothNumber.setText(String.valueOf(cart.getNumber()));
        holder.clothName.setText(String.valueOf(cart.getName()));
        holder.clothPrice.setText(String.valueOf(cart.getPrice()));
        Log.d("Price", String.valueOf(cart.getPrice()));
        holder.totalPrice.setText("₹ " + String.valueOf(cart.getPrice() * cart.getNumber()));

    }

    @Override
    public int getItemCount() {
        if (carts.size() == 0) {
            cartActivity.emptyCart.setVisibility(View.VISIBLE);
            cartActivity.empty_text.setVisibility(View.VISIBLE);
        } else {
            cartActivity.emptyCart.setVisibility(View.GONE);
            cartActivity.empty_text.setVisibility(View.GONE);
        }

        Log.d("Size of Cart Adapter", "" + carts.size());
        if (CheckNet.checkNet(context)) {
            return carts.size();
        } else {
            return 0;
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView cartImage;
        TextView clothName, clothPrice, closeButton, totalPrice, clothNumber;
        ImageButton add, remove;


        public MyViewHolder(View itemView) {
            super(itemView);
            cartImage = (ImageView) itemView.findViewById(R.id.clothImage);
            clothName = (TextView) itemView.findViewById(R.id.clothName);
            clothPrice = (TextView) itemView.findViewById(R.id.clothPrice);
            clothNumber = (TextView) itemView.findViewById(R.id.clothNumber);
            totalPrice = (TextView) itemView.findViewById(R.id.total);
            add = (ImageButton) itemView.findViewById(R.id.add);
            remove = (ImageButton) itemView.findViewById(R.id.remove);
            closeButton = (TextView) itemView.findViewById(R.id.closebutton);

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new UpdateToCart().execute(clothName.getText().toString(),
                            clothPrice.getText().toString(),
                            String.valueOf(0),
                            CARTDELETE);
                    total = total - (carts.get(getAdapterPosition()).getNumber() * carts.get(getAdapterPosition()).getPrice());
                    BaseApplication.setTotalprice(total);
                    cartActivity.totalPay.setText("₹ " + total);
                    carts.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = Integer.valueOf(clothNumber.getText().toString());
                    i = i + 1;
                    new UpdateToCart().execute(clothName.getText().toString(),
                            clothPrice.getText().toString(),
                            String.valueOf(i),
                            CARTADDONE);
                    clothNumber.setText(String.valueOf(i));
                    totalPrice.setText("₹ " + String.valueOf(i * Integer.valueOf(clothPrice.getText().toString())));
                    carts.get(getAdapterPosition()).setNumber(i);

                    total = total + Integer.valueOf(clothPrice.getText().toString());
                    BaseApplication.setTotalprice(total);
                    BaseApplication.addNoOfCartItems(1);
                    cartActivity.totalPay.setText("₹ " + total);
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = Integer.valueOf(clothNumber.getText().toString());
                    String v;
                    if (i > 1) {
                        i = i - 1;
                        Log.d("Which one is clicked", carts.get(getAdapterPosition()).getName().toString());
                        new UpdateToCart().execute(clothName.getText().toString(),
                                clothPrice.getText().toString(),
                                String.valueOf(i),
                                CARTREMOVEONE);
                        clothNumber.setText(String.valueOf(i));
                        totalPrice.setText("₹ " + String.valueOf(i * Integer.valueOf(v = clothPrice.getText().toString())));

                        carts.get(getAdapterPosition()).setNumber(i);

                        total = total - Integer.valueOf(v);
                        BaseApplication.setTotalprice(total);
                        BaseApplication.removeNoOfCartItems(1);
                        cartActivity.totalPay.setText("₹ " + total);
                    }

                }
            });


        }
    }


    class UpdateToCart extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... info) {

            Log.d("Siddhartha", info[0]);
            Log.d("Siddhartha", info[1]);
            try {
                SharedPreferences sharedPref = context.getSharedPreferences(
                        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String token = sharedPref.getString(context.getString(R.string.token), "");
                Request request = new Request.Builder()
                        .url(info[3])
                        .addHeader("clothname", info[0])
                        .addHeader("token", token)
                        .build();
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public static class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if (total == 0) {

                MyToast.showToast(context, "Please add some items to cart!!");

            } else {

                context.startActivity(new Intent(context, AddressActivity.class));

            }

        }
    }

}
