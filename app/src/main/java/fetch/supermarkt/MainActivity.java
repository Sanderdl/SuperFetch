package fetch.supermarkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import fetch.supermarkt.adapters.RequestListAdapter;
import fetch.supermarkt.database.FirebaseDB;
import fetch.supermarkt.database.IUpdatable;
import fetch.supermarkt.model.Request;


public class MainActivity extends BaseActivity implements IUpdatable {

    private TextView tvWorth;
    private TextView tvEarnings;

    private Button fetchRequest;

    private ListView requestList;
    private List<Request> allRequests;
    private List<Request> checkedRequests;

    private double productsWorth = 0.00;
    private double earnings = 0.00;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestList = (ListView) findViewById(R.id.list_home);
        tvWorth = (TextView) findViewById(R.id.val_worth);
        tvEarnings = (TextView) findViewById(R.id.val_earnings);

        fetchRequest = (Button) findViewById(R.id.btn_fetch);

        allRequests = new ArrayList<>();
        checkedRequests = new ArrayList<>();

        updateValues();
        update();

        allRequests = FirebaseDB.instance.getRequestList();
        FirebaseDB.instance.registerUpdatable("main",this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddRequest();
            }
        });

        fetchRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRequests();
            }
        });

    }

    private void goToAddRequest(){
        Intent intent = new Intent(this, NewRequestActivity.class);
        startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    public void addCheckedRequest(int index){
        checkedRequests.add(allRequests.get(index));
        updateValues();
    }

    public void removeCheckedRequest(Request toRemove){
        checkedRequests.remove(toRemove);
        updateValues();
    }

    private void updateValues(){
        productsWorth = 0.00;
        earnings = 0.00;
        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        for(Request r : checkedRequests){
            productsWorth += r.getWorth();
            earnings += r.getDeliveryFee();
        }

        String strWorth = formatter.format(productsWorth);
        String strEarnings = formatter.format(earnings);

        tvEarnings.setText(strEarnings);
        tvWorth.setText(strWorth);
    }

    @Override
    public void update(){
        allRequests = FirebaseDB.instance.getRequestList();
        ListAdapter listAdapter = new RequestListAdapter(this, R.layout.a_request_list_item, allRequests);
        requestList.setAdapter(listAdapter);
    }

    private void fetchRequests(){
        for(Request r : checkedRequests){
            removeCheckedRequest(r);
            r.setDelivererName(loginActivity.applicationUser);
            FirebaseDB.instance.pickupRequest(r);
        }
    }

}
