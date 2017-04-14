package fetch.supermarkt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import fetch.supermarkt.GroceriesActivity;
import fetch.supermarkt.R;
import fetch.supermarkt.model.Product;
import fetch.supermarkt.model.Request;

/**
 * Created by sande on 13/04/2017.
 */

public class GroceriesAdapter extends ArrayAdapter<Request> {
    private Context mContext;

    public GroceriesAdapter(Context context, int resource, List<Request> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.groceries_list_item, null);
        }

        final Request r = getItem(position);

        if (r != null) {
            TextView count = (TextView)v.findViewById(R.id.txt_count);
            TextView eta = (TextView)v.findViewById(R.id.eta_value);
            TextView supermarketStatus = (TextView)v.findViewById(R.id.at_supermarket_value);
            TextView dropoffPlace = (TextView)v.findViewById(R.id.dropoff_place_value);
            TextView fee = (TextView)v.findViewById(R.id.earnings_value);
            TextView worth = (TextView)v.findViewById(R.id.worth_value);
            TextView txt_user = (TextView)v.findViewById(R.id.txt_username);
            CheckBox checkBox = (CheckBox)v.findViewById(R.id.chd_fetch);

            ListView products = (ListView)v.findViewById(R.id.listview_products);
            List<Product> allProducts = new ArrayList<>();
            allProducts.addAll(r.getProducts());

            ImageView imgstore = (ImageView) v.findViewById(R.id.img_store);
            imgstore.setImageResource(r.getImageId());

            txt_user.setText(r.getRequesterName());

            NumberFormat formatter = NumberFormat.getCurrencyInstance();

            String strWorth = formatter.format(r.getWorth());
            String strEarnings = formatter.format(r.getDeliveryFee());

            fee.setText(strEarnings);
            worth.setText(strWorth);

            //Todo:Fix values to show stuff
            count.setText(String.valueOf(r.getProductCount()));
            eta.setText(r.getEta());
            supermarketStatus.setText(r.getStatus());
            dropoffPlace.setText(r.getLocation());

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(mContext instanceof GroceriesActivity){
                        if (isChecked)
                            ((GroceriesActivity)mContext).addCheckedRequest(position);
                        else
                            ((GroceriesActivity)mContext).removeCheckedRequest(r);
                    }
                }
            });
            ListAdapter adapter = new ProductAdapter(mContext,R.layout.product_item, allProducts);
            products.setAdapter(adapter);
        }
        return v;
    }
}
