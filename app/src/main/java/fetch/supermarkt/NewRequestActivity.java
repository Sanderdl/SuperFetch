package fetch.supermarkt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import fetch.supermarkt.adapters.ProductAdapter;
import fetch.supermarkt.database.FirebaseDB;
import fetch.supermarkt.model.Product;
import fetch.supermarkt.model.Request;

/**
 * Created by sande on 31/03/2017.
 */

public class NewRequestActivity extends BaseActivity {

    private ImageButton addProduct;
    private EditText input_location;
    private EditText input_fee;
    private Spinner input_store;
    private EditText input_product;
    private ListView product_list;
    private Button add_request;

    private TextView fee;
    private TextView worth;


    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        input_location = (EditText) findViewById(R.id.input_location);
        input_fee = (EditText) findViewById(R.id.input_delivery_fee);
        input_store = (Spinner) findViewById(R.id.input_store);
        input_product = (EditText) findViewById(R.id.input_product);
        product_list = (ListView) findViewById(R.id.proudct_list);
        add_request = (Button) findViewById(R.id.btn_fetch);
        addProduct = (ImageButton) findViewById(R.id.btn_add);

        fee = (TextView) findViewById(R.id.val_earnings);
        worth = (TextView) findViewById(R.id.val_worth);

        List<String> spinnerlist = new ArrayList<>();

        spinnerlist.add("Albert Heijn");
        spinnerlist.add("Lidl");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, spinnerlist);

        productList = new ArrayList<>();

        input_store.setAdapter(adapter);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });
        add_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRequest();
            }
        });

        calculateTotal();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_new_request;
    }

    private void addProduct() {
        Product newProduct = null;

        for (Product p : FirebaseDB.instance.getProductList()) {
            String name = input_product.getText().toString().toLowerCase();
            if (p.getProductName().toLowerCase().contains(name)) {
                newProduct = p;
            }
        }
        if (newProduct == null)
            return;

        input_product.setText("");

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        productList.add(newProduct);

        ListAdapter productAdapter = new ProductAdapter(this, R.layout.add_product_item, productList);
        product_list.setAdapter(productAdapter);
        calculateTotal();

    }

    private void addRequest() {
        double fee = 0.0;
        if (input_fee.getText().length() > 0)
            fee = Double.parseDouble(input_fee.getText().toString());
        double total = calculateTotal();

        if (productList.size() == 0 || input_location.getText().length() == 0)
            return;


        Request r = new Request(5, total, fee, input_location.getText().toString()
                , loginActivity.applicationUser, input_store.getSelectedItem().toString());
        r.setProducts(productList);
        r.addRequestToFirebase();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private double calculateTotal() {
        double value = 0.0;
        double the_fee = 0.0;
        for (Product p : productList) {
            value += p.getProductPrice();
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        worth.setText(formatter.format(value));
        if (input_fee.getText().length() > 0)
            fee.setText(formatter.format(Double.parseDouble(input_fee.getText().toString())));
        else
            fee.setText(formatter.format(the_fee));

        return value;
    }

    public void updateProductList(Product product){
        productList.remove(product);
        ListAdapter productAdapter = new ProductAdapter(this, R.layout.add_product_item, productList);
        product_list.setAdapter(productAdapter);
    }
}
