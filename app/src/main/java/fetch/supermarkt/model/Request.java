package fetch.supermarkt.model;

/**
 * Created by sande on 30/03/2017.
 */

public class Request {
    private int productCount;
    private double worth;
    private double earnings;
    private String location;

    public Request(int productCount, double worth, double earnings, String location) {
        this.productCount = productCount;
        this.worth = worth;
        this.earnings = earnings;
        this.location = location;
    }

    public int getProductCount() {
        return productCount;
    }

    public double getWorth() {
        return worth;
    }

    public double getEarnings() {
        return earnings;
    }

    public String getLocation() {
        return location;
    }
}
