package servicepayment.entities;


public class Payment {

    private String sagaID;
    private String paymentID;
    private String user;
    private Float amount;
    private String date;
    private String hour;

    public Payment(String sagaID, String paymentID, String user, Float amount, String date, String hour) {
        this.sagaID = sagaID;
        this.paymentID = paymentID;
        this.user = user;
        this.amount = amount;
        this.date = date;
        this.hour = hour;
    }

    public Payment() {

    }

    public String getSagaID() {
        return sagaID;
    }

    public void setSagaID(String sagaID) {
        this.sagaID = sagaID;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
