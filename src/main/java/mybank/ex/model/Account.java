package mybank.ex.model;

import mybank.ex.service.Utility;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Account {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length=29, nullable = false)
    private String iban;

    @OneToOne()
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @Column(precision=13, scale=2, nullable = false)
    private double balance;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    final private List<Transaction> transactions = new ArrayList<>();

    public Account() {}

    public Account(Client client, Currency currency) {
        this.client = client;
        this.currency = currency;
        this.iban = Utility.getRandomIban();
        this.balance = 0d;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void addTransaction(Transaction trans) {
        if (!transactions.contains(trans)) {
            transactions.add(trans);
            trans.setAccount(this);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "ID = %d, IBAN = %s, currency %s, balance = %.2f %s",
                id, iban, currency.getName(), balance, currency.getCode()
        );
    }
}
