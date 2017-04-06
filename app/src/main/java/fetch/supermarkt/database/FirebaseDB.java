package fetch.supermarkt.database;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fetch.supermarkt.model.Request;

/**
 * Created by sande on 06/04/2017.
 */


public class FirebaseDB {

    public static final FirebaseDB instance = new FirebaseDB();

    private final FirebaseDatabase database;
    private final DatabaseReference mRef;

    private final Map<String,IUpdatable> contextMap = new HashMap<>();
    private final List<Request> requestList = new ArrayList<>();

    private FirebaseDB(){
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("requests");
        mRef.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                requestList.add(dataSnapshot.getValue(Request.class));
                notifyUpdater("main");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Request request = dataSnapshot.getValue(Request.class);
                int index = requestList.lastIndexOf(request);
                if (index > -1) {
                    requestList.remove(request);
                    requestList.add(index, request);
                }
                notifyUpdater("main");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                requestList.remove(dataSnapshot.getValue(Request.class));
                notifyUpdater("main");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void registerUpdatable(String name,IUpdatable toUpdate){
        contextMap.put(name,toUpdate);
    }

    private void notifyUpdater(String name ){
        IUpdatable updatable = contextMap.get(name);
        updatable.update();
    }
}
