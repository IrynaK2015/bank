package mybank.ex.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Client {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length=200, nullable = false, name="firstname")
    private String firstName;

    @Column(length=200, nullable = false, name="lastname")
    private String lastName;

    @Column(nullable = false)
    private long taxNumber;

    @Column(length=255, nullable = false)
    private String email;

    @Column(length=255, nullable = false)
    private String address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    final private List<Account> accounts = new ArrayList<>();

    public Client() {}

    public Client(String firstName, String lastName, String address, String email, long taxNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.taxNumber = taxNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(long taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullname() {
        return firstName + " " + lastName;
    }

    public void addAccount(Account account) {
        if (!accounts.contains(account)) {
            accounts.add(account);
            account.setClient(this);
        }
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Account getAccount(String currencyCode) {
        for (Account account : accounts) {
            if (currencyCode.equalsIgnoreCase(account.getCurrency().getCode())) {
                return account;
            }
        }
        throw new IllegalArgumentException("Account with currency " + currencyCode + " not found for " + getFullname());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(
            String.format(
                "Client ID = %d,  %s %s, %s, %s, tax number = %d",
                id, firstName, lastName, address, email, taxNumber
            )
        );

        for (Account account : accounts) {
            sb.append("\n\t" + account);
        }

        return sb.toString();
    }
}
