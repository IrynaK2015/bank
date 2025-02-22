package mybank.ex.model;

import javax.persistence.*;

@Entity
@Table(name = "currency_rate")
public class Currency {
    @Id
    @GeneratedValue
    private int id;

    @Column(length=10, nullable = false)
    private String code;

    @Column(length=100, nullable = false)
    private String name;

    @Column(precision=13, scale=4, nullable = false)
    private double rate;

    public Currency() {}

    public Currency(String code, String name, double rate) {
        this.code = code;
        this.name = name;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
