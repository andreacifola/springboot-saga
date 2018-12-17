package servicesagaorchestrator.entities;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BankAccountEntity {

    private String userId;
    private String moneyAccount;

    public BankAccountEntity(String userId, String moneyAccount) {
        this.userId = userId;
        this.moneyAccount = moneyAccount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMoneyAccount() {
        return moneyAccount;
    }

    public void setMoneyAccount(String moneyAccount) {
        this.moneyAccount = moneyAccount;
    }
}
