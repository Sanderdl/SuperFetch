package fetch.supermarkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fetch.supermarkt.adapters.GroceriesAdapter;
import fetch.supermarkt.database.FirebaseDB;
import fetch.supermarkt.database.IUpdatable;
import fetch.supermarkt.model.Request;

/**
 * Created by sande on 24/03/2017.
 */

public class GroceriesActivity extends BaseActivity implements IUpdatable {

    private List<Request> allJobs;
    private ListView listViewJobs;
    private List<Request> checkedRequests;
    private Button btn_fetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDB.instance.registerUpdatable("groceries",this);

        checkedRequests = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddRequest();
            }
        });

        listViewJobs = (ListView)findViewById(R.id.my_tasks_list);
        btn_fetch = (Button) findViewById(R.id.btn_fetch);

        btn_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSelectedTasks();
            }
        });

        update();
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
    protected int getActivityID() {
        return 1;
    }

    @Override
    public void update() {
        allJobs = FirebaseDB.instance.getYourJobsList();
        ListAdapter adapter = new GroceriesAdapter(this,R.layout.groceries_list_item,allJobs);
        listViewJobs.setAdapter(adapter);
    }

    public void addCheckedRequest(int index){
        checkedRequests.add(allJobs.get(index));
    }

    public void removeCheckedRequest(Request toRemove){
        checkedRequests.remove(toRemove);
    }

    private void closeSelectedTasks(){
        for(Request r : checkedRequests){
            removeCheckedRequest(r);
            r.setDelivererName(loginActivity.applicationUser);
            FirebaseDB.instance.completeRequest(r);
        }
    }
}
