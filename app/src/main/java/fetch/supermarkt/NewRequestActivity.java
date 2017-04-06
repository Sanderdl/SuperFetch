package fetch.supermarkt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import fetch.supermarkt.model.Request;

/**
 * Created by sande on 31/03/2017.
 */

public class NewRequestActivity extends BaseActivity {

    private ImageButton addProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addProduct = (ImageButton)findViewById(R.id.btn_add);


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
        Request r = new Request(5,20.50,3.22,"Fontys jonguh","Ruud","Jumbo");
        r.addRequestToFirebase();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
