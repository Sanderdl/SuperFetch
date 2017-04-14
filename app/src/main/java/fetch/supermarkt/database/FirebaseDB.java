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

import fetch.supermarkt.loginActivity;
import fetch.supermarkt.model.Product;
import fetch.supermarkt.model.Request;

/**
 * Created by sande on 06/04/2017.
 */


public class FirebaseDB {

    public static final FirebaseDB instance = new FirebaseDB();

    private final FirebaseDatabase database;
    private final DatabaseReference mRef;

    private final Map<String,IUpdatable> contextMap = new HashMap<>();
    private final Map<String, Request> requestList = new HashMap();

    private final List<Product> productList = new ArrayList<>();

    private final Map<String, Request> yourJobs = new HashMap();
    private final Map<String, Request> yourRequests = new HashMap();

    private FirebaseDB(){

        productList.add(new Product("Ham-kaas Croissant",0.80));
        productList.add(new Product("Cola",1.20));
        productList.add(new Product("Frikandel broodje",1.50));

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("requests");
        mRef.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request request = dataSnapshot.getValue(Request.class);
                if (request.getDelivererName()== null) {
                    requestList.put(request.getRequestId(),request);
                    if (contextMap.containsKey("main"))
                        notifyUpdater("main");
                }else if (request.getDelivererName().equals(loginActivity.applicationUser)){
                    yourJobs.put(request.getRequestId(), request);
                    if (contextMap.containsKey("groceries"))
                        notifyUpdater("groceries");
                }
                if(request.getRequesterName().equals(loginActivity.applicationUser)){
                    yourRequests.put(request.getRequestId(), request);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Request request = dataSnapshot.getValue(Request.class);

                requestList.remove(request.getRequestId());

                if (request.getDelivererName()== null) {
                    requestList.put(request.getRequestId(),request);
                }else if (request.getDelivererName().equals(loginActivity.applicationUser)){
                    yourJobs.put(request.getRequestId(), request);
                    if (contextMap.containsKey("groceries"))
                        notifyUpdater("groceries");
                }
                if (contextMap.containsKey("main"))
                    notifyUpdater("main");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String request = dataSnapshot.getValue(Request.class).getRequestId();
                requestList.remove(request);
                yourJobs.remove(request);
                yourRequests.remove(request);

                if (contextMap.containsKey("main"))
                    notifyUpdater("main");
                if (contextMap.containsKey("groceries"))
                    notifyUpdater("groceries");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<Request> getRequestList()
    {
        List<Request> temp = new ArrayList<>();
        for(Map.Entry<String, Request> r : requestList.entrySet()){
            temp.add(r.getValue());
        }
        return temp;
    }

    public void registerUpdatable(String name,IUpdatable toUpdate){
        contextMap.put(name,toUpdate);
    }

    public List<Product> getProductList() {
        return productList;
    }

    public List<Request> getYourJobsList() {
        List<Request> temp = new ArrayList<>();
        for(Map.Entry<String, Request> r : yourJobs.entrySet()){
            temp.add(r.getValue());
        }
        return temp;
    }

    public List<Request> getYourRequestsList() {
        List<Request> temp = new ArrayList<>();
        for(Map.Entry<String, Request> r : yourRequests.entrySet()){
            temp.add(r.getValue());
        }
        return temp;
    }

    public void pickupRequest(Request request){
        request.setStatus("Picked up");
        mRef.child(request.getRequestId()).setValue(request);
    }

    private void notifyUpdater(String name ){
        IUpdatable updatable = contextMap.get(name);
        updatable.update();
    }

    public void completeRequest(Request r){
        mRef.child(r.getRequestId()).removeValue();
    }

    public void updateRequestStatus(Request r, String status){
        r.setStatus(status);
        mRef.child(r.getRequestId()).setValue(r);
    }

}
