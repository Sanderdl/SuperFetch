package fetch.supermarkt.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by sande on 30/03/2017.
 */

public class Request {
    private String requestId;
    private int productCount;
    private double worth;
    private double deliveryFee;
    private String location;
    private String requesterName;
    private String store;
    private String delivererName;
    private boolean atSupermarket;
    private boolean orderReceived;
    private boolean orderDelivered;
    private List<Product> products;

    public Request(){}

    public Request(int productCount, double worth, double deliveryFee, String location, String requesterName, String store) {
        this.productCount = productCount;
        this.worth = worth;
        this.deliveryFee = deliveryFee;
        this.location = location;
        this.requesterName = requesterName;
        this.store = store;
    }

    public int getProductCount() {
        return productCount;
    }

    public double getWorth() {
        return worth;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public String getLocation() {
        return location;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getStore() {
        return store;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<Product> getProducts() {
        return products;
    }



    public void setProducts(List<Product> products) {
        this.products = products;
        this.productCount = products.size();
    }

    public void addRequestToFirebase() {
        //Create Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Use push() to generate an unique key and insert this key in the database
        DatabaseReference mRef = database.getReference().child("requests").push();

        //Get this key and assign it to the object
        this.setRequestId(mRef.getKey());

        //Insert the object data in Firebase
        mRef.setValue(this);
    }
}
