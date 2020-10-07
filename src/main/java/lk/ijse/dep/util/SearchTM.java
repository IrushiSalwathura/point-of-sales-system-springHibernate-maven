package lk.ijse.dep.util;

public class SearchTM {
    private String orderID;
    private String orderDate;
    private String customerID;
    private String customerName;
    private double total;

    public SearchTM() {
    }

    public SearchTM(String orderID, String orderDate, String customerID, String customerName, double total) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.customerID = customerID;
        this.customerName = customerName;
        this.total = total;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
