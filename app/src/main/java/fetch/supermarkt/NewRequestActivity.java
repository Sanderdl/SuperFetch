package fetch.supermarkt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by sande on 31/03/2017.
 */

public class NewRequestActivity extends BaseActivity {

    private ImageButton addProduct;
    private LinearLayout productsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addProduct = (ImageButton)findViewById(R.id.btn_add);
        productsLayout = (LinearLayout)findViewById(R.id.products_LL);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_new_request;
    }

    private void addProduct() {

        View row = LayoutInflater.from(getBaseContext()).inflate(R.layout.add_product_item, null);
        productsLayout.addView(row);
    }
}
