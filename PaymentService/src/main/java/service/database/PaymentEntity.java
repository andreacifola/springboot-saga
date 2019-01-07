package service.database;


import org.springframework.data.annotation.Id;

public class PaymentEntity {

    @Id
    private String id;

    private String paymentId;
    private String user;
    private String amount;

    public PaymentEntity() {
    }

    public PaymentEntity(String paymentId, String user, String amount) {
        this.paymentId = paymentId;
        this.user = user;
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
