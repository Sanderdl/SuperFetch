package fetch.supermarkt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import fetch.supermarkt.R;
import fetch.supermarkt.model.Product;

/**
 * Created by sande on 06/04/2017.
 */

public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.add_product_item, null);
        }

        final Product p = getItem(position);

        if (p != null) {
            TextView product = (TextView)v.findViewById(R.id.txt_addproductname);
            TextView value = (TextView)v.findViewById(R.id.txt_product_prize);

            NumberFormat formatter = NumberFormat.getCurrencyInstance();

            product.setText(p.getProductName());
            value.setText(formatter.format(p.getProductPrice()));


        }
        return v;
    }
}
