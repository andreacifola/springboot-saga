package service.database;


import org.springframework.data.annotation.Id;

/**
 * This class is useful to store payment elements in Mongo db.
 */
public class PaymentEntity {

    @Id
    private String id;

    private String paymentId;
    private String accountId;
    private String user;
    private String amount;

    public PaymentEntity() {
    }

    public PaymentEntity(String paymentId, String accountId, String user, String amount) {
        this.paymentId = paymentId;
        this.accountId = accountId;
        this.user = user;
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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
