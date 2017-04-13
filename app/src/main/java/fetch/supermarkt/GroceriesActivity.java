package fetch.supermarkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import fetch.supermarkt.database.FirebaseDB;
import fetch.supermarkt.database.IUpdatable;

/**
 * Created by sande on 24/03/2017.
 */

public class GroceriesActivity extends BaseActivity implements IUpdatable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDB.instance.registerUpdatable("groceries",this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddRequest();
            }
        });
    }

    private void goToAddRequest(){
        Intent intent = new Intent(this, NewRequestActivity.class);
        startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_groceries;
    }

    @Override
    public void update() {

    }
}
