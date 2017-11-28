package freakydevelopers.dhobikart.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import freakydevelopers.dhobikart.activity.AddressActivity;
import freakydevelopers.dhobikart.activity.NewAddress;
import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.pojo.Address;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    private List<Address> addresses = new ArrayList<Address>();
    private Context context;
    AddressActivity addressActivity;
    public static int lastCheckedPosition = 0;


    public AddressAdapter(Context context, List<Address> addresses) {
        this.addresses = addresses;
        this.context = context;
        addressActivity = (AddressActivity) context;
    }

    @Override
    public AddressAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.address_recycle, parent, false);
        return new AddressAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressAdapter.MyViewHolder holder, int position) {


        Address address = addresses.get(position);
        holder.Name.setText(address.getName());
        holder.Address.setText(address.getArea() + System.getProperty("line.separator") +
                address.getCity() + "," +
                address.getState() + "-" + address.getPin());
        holder.landmark.setText(address.getLandmark());
        if (holder.landmark.getText().toString().isEmpty() || holder.landmark.getText().toString() == null) {
            holder.landmark.setVisibility(View.GONE);
        } else {
            holder.landmark.setVisibility(View.VISIBLE);
        }
        holder.phone.setText(address.getPhone());
        holder.alterPhone.setText(address.getAlterphone());
        if (holder.alterPhone.getText().toString().isEmpty() || holder.alterPhone.getText().toString() == null) {
            holder.alterPhone.setVisibility(View.GONE);
        } else {
            holder.alterPhone.setVisibility(View.VISIBLE);
        }
        holder.isSelected.setChecked(position == lastCheckedPosition);
    }

    @Override
    public int getItemCount() {

        Log.d("Size of Address Adapter", "" + addresses.size());

        if (addresses.size() == 0 || addresses.isEmpty() || addresses.contains(null)) {

            return 0;

        } else {

            return addresses.size();

        }


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Name, Address, landmark, phone, alterPhone;
        RadioButton isSelected;
        Button editButton;

        public MyViewHolder(View v) {
            super(v);
            Name = (TextView) v.findViewById(R.id.name);
            Address = (TextView) v.findViewById(R.id.address1);
            landmark = (TextView) v.findViewById(R.id.landmark);
            phone = (TextView) v.findViewById(R.id.phone);
            alterPhone = (TextView) v.findViewById(R.id.alterPhone);
            isSelected = (RadioButton) v.findViewById(R.id.isSelected);
            editButton = (Button) v.findViewById(R.id.editButton);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Address address = addresses.get(getAdapterPosition());
                    Intent intent = new Intent(context, NewAddress.class);
                    intent.putExtra("address", String.valueOf(new Gson().toJson(address)));
                    intent.putExtra("whichActivity", true);
                    intent.putExtra("id", getAdapterPosition());
                    Log.d("Id of View", "" + getAdapterPosition());
                    AddressActivity addressActivity = (AddressActivity) context;
                    addressActivity.startActivityForResult(intent, 2);
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lastCheckedPosition = getAdapterPosition();
                    notifyItemRangeChanged(0, addresses.size());
                }
            });
            isSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lastCheckedPosition = getAdapterPosition();
                    notifyItemRangeChanged(0, addresses.size());
                }
            });

        }

    }


}
