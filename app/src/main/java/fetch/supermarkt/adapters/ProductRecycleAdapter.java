package fetch.supermarkt.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import fetch.supermarkt.R;
import fetch.supermarkt.model.Product;

/**
 * Created by sande on 17/04/2017.
 */

public class ProductRecycleAdapter extends RecyclerView.Adapter<ProductRecycleAdapter.MyViewHolder> {

    private List<Product> myProducts;

    public ProductRecycleAdapter(List<Product> products){
        this.myProducts = products;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product product = myProducts.get(position);
        holder.product.setText(product.getProductName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String thePrice = formatter.format(product.getProductPrice());

        holder.price.setText(thePrice);
    }

    @Override
    public int getItemCount() {
        return myProducts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product, price;

        public MyViewHolder(View view) {
            super(view);
            product = (TextView) view.findViewById(R.id.txt_addproductname);
            price = (TextView) view.findViewById(R.id.txt_product_prize);
        }
    }

}
