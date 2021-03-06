package fetch.supermarkt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import fetch.supermarkt.MainActivity;
import fetch.supermarkt.R;
import fetch.supermarkt.model.Request;

/**
 * Created by sande on 30/03/2017.
 */

public class RequestListAdapter extends ArrayAdapter<Request> {

    private Context mContext;

    public RequestListAdapter(Context context, int resource, List<Request> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.a_request_list_item, null);
        }

        final Request r = getItem(position);

        if (r != null) {
            TextView count = (TextView)v.findViewById(R.id.txt_count);
            TextView worth = (TextView)v.findViewById(R.id.eta_value);
            TextView earnings = (TextView)v.findViewById(R.id.at_supermarket_value);
            TextView userName = (TextView)v.findViewById(R.id.txt_userName);
            TextView loc = (TextView)v.findViewById(R.id.txt_detailedInfo);
            CheckBox checkBox = (CheckBox)v.findViewById(R.id.chd_fetch);

            ImageView imgstore = (ImageView) v.findViewById(R.id.img_store);
            imgstore.setImageResource(r.getImageId());

            NumberFormat formatter = NumberFormat.getCurrencyInstance();

            String strWorth = formatter.format(r.getWorth());
            String strEarnings = formatter.format(r.getDeliveryFee());

            count.setText(String.valueOf(r.getProductCount()));
            worth.setText(strWorth);
            earnings.setText(strEarnings);
            userName.setText(r.getRequesterName());
            loc.setText(r.getLocation());

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(mContext instanceof MainActivity){
                        if (isChecked)
                            ((MainActivity)mContext).addCheckedRequest(position);
                        else
                            ((MainActivity)mContext).removeCheckedRequest(r);
                    }
                }
            });
        }
        return v;
    }


}
