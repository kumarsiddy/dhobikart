package freakydevelopers.dhobikart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.Resources.Logger;
import freakydevelopers.dhobikart.pojo.OrderedCloth;

/**
 * Created by PURUSHOTAM on 8/18/2017.
 */

public class CustomListOrderAdapter extends ArrayAdapter<OrderedCloth> {

    private List<OrderedCloth> orderedCloths;
    private Context context;

    public CustomListOrderAdapter(Context context, List<OrderedCloth> orderedCloths) {
        super(context, R.layout.recyclersecondorderdetails, orderedCloths);
        this.orderedCloths = orderedCloths;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        OrderedCloth orderedCloth = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.recyclersecondorderdetails, parent, false);
            viewHolder.clothName = (TextView) convertView.findViewById(R.id.clothname);
            viewHolder.clothPrice = (TextView) convertView.findViewById(R.id.price);
            viewHolder.totalNumber = (TextView) convertView.findViewById(R.id.number);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

       /* Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;*/

        String a, b, c;

        viewHolder.clothName.setText(a = orderedCloth.getName());
        viewHolder.clothPrice.setText(b = orderedCloth.getPrice() + "");
        viewHolder.totalNumber.setText(c = orderedCloth.getNumber() + "");
        // Return the completed view to render on screen
        Logger.d("From Custom Layout" + a + b + c);
        return convertView;
    }

    private class ViewHolder {
        TextView clothName, clothPrice, totalNumber;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
