package fetch.supermarkt;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fetch.supermarkt.adapters.RequestListAdapter;
import fetch.supermarkt.model.Request;

public class MainActivity extends BaseActivity {

    private ListView requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestList = (ListView) findViewById(R.id.list_home);

        List<Request> allRequests = new ArrayList<>();

        Request r = new Request(2,10.50,3.22,"Fontys jonguh","Sander");
        Request r1 = new Request(4,11.50,3.22,"Fontys jonguh","Stef");
        Request r2 = new Request(3,12.50,3.22,"Fontys jonguh","Luuk");
        Request r3 = new Request(5,20.50,3.22,"Fontys jonguh","Evert");

        allRequests.add(r);
        allRequests.add(r1);
        allRequests.add(r2);
        allRequests.add(r3);

        ListAdapter listAdapter = new RequestListAdapter(this, R.layout.a_request_list_item, allRequests);
        requestList.setAdapter(listAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }
}
