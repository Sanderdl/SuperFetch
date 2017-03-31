package fetch.supermarkt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fetch.supermarkt.R;
import fetch.supermarkt.model.Request;

/**
 * Created by sande on 30/03/2017.
 */

public class RequestListAdapter extends ArrayAdapter<Request> {

    public RequestListAdapter(Context context, int resource, List<Request> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.a_request_list_item, null);
        }

        Request r = getItem(position);

        if (r != null) {
            TextView count = (TextView)v.findViewById(R.id.txt_count);
            TextView worth = (TextView)v.findViewById(R.id.val_worth2);
            TextView earnings = (TextView)v.findViewById(R.id.val_earnings2);
            TextView userName = (TextView)v.findViewById(R.id.txt_userName);

            count.setText(String.valueOf(r.getProductCount()));
            worth.setText("€ "+String.valueOf(r.getWorth()));
            earnings.setText("€ "+String.valueOf(r.getDeliveryFee()));
            userName.setText(r.getRequesterName());
        }
        return v;
    }

}
