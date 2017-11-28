package freakydevelopers.dhobikart.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.activity.BaseApplication;
import freakydevelopers.dhobikart.activity.ClothActivity;
import freakydevelopers.dhobikart.connection.CheckNet;
import freakydevelopers.dhobikart.pojo.Cloth;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.CARTADD;
import static freakydevelopers.dhobikart.Resources.MyURL.JSON;
import static freakydevelopers.dhobikart.activity.LauncherActivity.client;


/**
 * Created by PURUSHOTAM on 9/17/2016.
 */
public class ClothAdapter extends RecyclerView.Adapter<ClothAdapter.MyViewHolder> {

    private List<Cloth> clothList;
    Context context;
    private static AlertDialog dialog;
    ClothActivity clothActivity;
    MyViewHolder holder;

    public ClothAdapter(Context context, List<Cloth> clothList) {
        this.clothList = clothList;
        this.context = context;
        clothActivity = (ClothActivity) context;
        clothActivity.cartnotice.setVisibility(View.VISIBLE);
        clothActivity.cartnotice.setText(String.valueOf(BaseApplication.getNoOfCartItems()));
        System.out.println(String.valueOf(clothList));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.clothrecycle, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Cloth cloth = clothList.get(position);
        Picasso.with(context).load(cloth.getImg())
                .centerCrop()
                .fit()
                .into(holder.clothImage);
        holder.clothName.setText(cloth.getName());
        holder.clothPrice.setText("₹ " + cloth.getPrice());
    }

    @Override
    public int getItemCount() {
        return clothList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView clothImage;
        public TextView clothName, clothPrice, clothCount;


        public MyViewHolder(View view) {
            super(view);
            clothImage = (ImageView) view.findViewById(R.id.clothImage);
            clothName = (TextView) view.findViewById(R.id.clothName);
            clothPrice = (TextView) view.findViewById(R.id.clothPrice);
            clothCount = (TextView) view.findViewById(R.id.cartnotice);


            clothImage.setOnClickListener(
                    new View.OnClickListener() {


                        @Override
                        public void onClick(View view) {
                            if (CheckNet.checkNet(context)) {

                                final int noCloth[] = new int[1];
                                View dialogView = LayoutInflater.from(context).inflate(R.layout.clothcalculator, null);
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                                dialogBuilder.setView(dialogView);

                                ImageButton plus = (ImageButton) dialogView.findViewById(R.id.plus);
                                ImageButton minus = (ImageButton) dialogView.findViewById(R.id.minus);
                                final TextView number = (TextView) dialogView.findViewById(R.id.number);

                                plus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        noCloth[0]++;
                                        number.setText(String.valueOf(noCloth[0]));
                                    }
                                });

                                minus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (noCloth[0] > 0) {
                                            noCloth[0]--;
                                        }

                                        if (noCloth[0] >= 0) {
                                            number.setText(String.valueOf(noCloth[0]));
                                        }

                                    }
                                });

                                dialogBuilder.setMessage("Please select the Number of clothes...");
                                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Cloth cloth = clothList.get(getAdapterPosition());
                                        String cloth_name = cloth.getName();
                                        String cloth_price = cloth.getPrice();
                                        BaseApplication.addNoOfCartItems(noCloth[0]);

                                        Log.d("Name", cloth_name);
                                        Log.d("price", cloth_price);
                                        Log.d("Number", String.valueOf(noCloth[0]));

                                        if (CheckNet.checkNet(context) && noCloth[0] > 0) {
                                            new UpdateToCart().execute(cloth_name, cloth_price, String.valueOf(noCloth[0]));
                                        }
                                        clothActivity.cartnotice.setText(String.valueOf(BaseApplication.getNoOfCartItems()));


                                        dialog.dismiss();
                                    }
                                });
                                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog = dialogBuilder.create();
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();

                                Button buttonbackground = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                buttonbackground.setTextColor(Color.BLUE);

                                Button buttonbackground1 = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                buttonbackground1.setTextColor(Color.RED);
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
                JSONObject json = new JSONObject();
                SharedPreferences sharedPref = context.getSharedPreferences(
                        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String token = sharedPref.getString(context.getString(R.string.token), "");
                json.put("clothName", info[0]);
                json.put("clothPrice", info[1]);
                json.put("totalNumber", info[2]);


                RequestBody body = RequestBody.create(JSON, String.valueOf(json));
                Request request = new Request.Builder()
                        .url(CARTADD)
                        .addHeader("token", token)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String string = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

}
