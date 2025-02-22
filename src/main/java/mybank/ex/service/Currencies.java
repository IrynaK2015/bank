package mybank.ex.service;

import mybank.ex.model.Currency;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class Currencies {
    private static final Currencies INSTANCE = new Currencies();

    private EntityManager em;

    final private List<Currency> currencies = new ArrayList<>();

    private Currencies() {}

    public static Currencies getInstance() {
        return INSTANCE;
    }

    public void init(EntityManager em) {
        this.em = em;
        em.getTransaction().begin();
        Currency uah = new Currency("UAH", "hryvnja", 1);
        Currency usd = new Currency("USD", "USA dollar", 41.6814d);
        Currency eur = new Currency("EUR", "EURO", 43.4737d);
        try {
            em.persist(uah);
            em.persist(usd);
            em.persist(eur);
            em.getTransaction().commit();

            currencies.add(uah);
            currencies.add(usd);
            currencies.add(eur);
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }

    public Currency getCurrency(String code) {
        for (Currency curr : currencies) {
            if (curr.getCode().equals(code.toUpperCase())) {
                return curr;
            }
        }

        return null;
    }

    /*private void collectCurrencies() {
        if (currencies.isEmpty()) {
            currencies = em.createQuery("SELECT c FROM Currency c", Currency.class).getResultList();
        }
    }*/
}
