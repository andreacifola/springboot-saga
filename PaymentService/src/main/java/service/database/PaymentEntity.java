package service.database;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PaymentEntity {

    private String paymentID;
    private String user;
    private Float amount;
    //private String date;
    //private String hour;


    public PaymentEntity(String paymentID, String user, Float amount) {
        this.paymentID = paymentID;
        this.user = user;
        this.amount = amount;
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
}
