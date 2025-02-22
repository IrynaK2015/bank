package mybank.ex.service;

import mybank.ex.model.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;

public class ClientFactory {

    final private EntityManager em;

    public ClientFactory(EntityManager em) {
        this.em = em;
    }

    public Client createClient(String firstName, String lastName, String address, String email, long taxNumber, List<String> currencyCodes) {
        Client client = new Client(firstName, lastName, address, email, taxNumber);
        addAccounts(client, currencyCodes);

        return saveClient(client) ? client : null;
    }

    public Client getClient(long clientId) {
        Client client = em.find(Client.class, clientId);
        if (Objects.equals(client, null)) {
            throw new RuntimeException("Client with id " + clientId + " not found");
        }

        return client;
    }

    public double getSummaryBalance(long clientId) {
        Query q = em.createNativeQuery(
            "SELECT SUM(c.rate * a.balance) AS uah_balance FROM account a"
                + " INNER JOIN currency_rate c ON c.id = a.currency_id"
                + " WHERE a.client_id = :client_id GROUP BY a.client_id"
        );
        q.setParameter("client_id", clientId);
        double balance = (Double) q.getSingleResult();

        return Utility.moneyRound(balance, 2);
    }

    private void addAccounts(Client client, List<String> currencyCodes) {
        Currencies currencies = Currencies.getInstance();
        for (String code : currencyCodes) {
            Currency currency = currencies.getCurrency(code);
            if (Objects.equals(currency, null)) {
                System.out.println("Currency " + code + " not found");
            } else {
                Account account = new Account(client, currency);
                client.addAccount(account);
            }
        }
    }

    private boolean saveClient(Client client) {
        if (!client.getAccounts().isEmpty()) {
            em.getTransaction().begin();
            try {
                em.persist(client);
                for (Account account : client.getAccounts()) {
                    em.persist(account);
                }
                em.getTransaction().commit();

                return true;
            } catch (Exception ex) {
                em.getTransaction().rollback();
            }
        }

        return false;
    }
}
