package service.database;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BankAccountEntity {

    private String accountId;
    private String user;
    private String moneyAccount;

    public BankAccountEntity(String accountId, String user, String moneyAccount) {
        this.accountId = accountId;
        this.user = user;
        this.moneyAccount = moneyAccount;
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

    public String getMoneyAccount() {
        return moneyAccount;
    }

    public void setMoneyAccount(String moneyAccount) {
        this.moneyAccount = moneyAccount;
    }
}
