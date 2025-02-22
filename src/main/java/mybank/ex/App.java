package mybank.ex;

import mybank.ex.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

import mybank.ex.service.ConversionManager;
import mybank.ex.service.Currencies;
import mybank.ex.service.ClientFactory;
import mybank.ex.service.TransactionManager;

public class App {
    static EntityManagerFactory emf;
    static EntityManager em;
    static List<Client> clients = new ArrayList<>();

    public static void main(String[] args) {
        emf = Persistence.createEntityManagerFactory("BankApp");
        em = emf.createEntityManager();

        try {
            doInitialLoad();

            long wrongId = 5555555L;
            try {
                new ClientFactory(em).getClient(wrongId);
            } catch (Exception e) {
                System.out.println("Client with ID " + wrongId + " not found " + e.getMessage());
            }

            doInternalMoneyTransfer();
            printClients();

            doClientConversion();

            double clientBalance = new ClientFactory(em).getSummaryBalance(clients.get(1).getId());
            System.out.println("Client summary balance in UAH = " + clientBalance);

        } finally {
            em.close();
            emf.close();
        }
    }

    private static void doInitialLoad() {
        createCurrencies();
        createClients();
        doInitialDeposit();
        printClients();
    }

    private static void createCurrencies() {
        Currencies.getInstance().init(em);
    }

    private static void createClients() {
        ClientFactory clientFactory = new ClientFactory(em);
        clients.add(
                clientFactory.createClient(
                        "John", "Smith", "some fake address", "john.smith@nomail.com", 234567890L,
                        List.of("uah", "usd")
                )
        );
        clients.add(
                clientFactory.createClient(
                        "Jane", "Dou", "some fake address 1", "jane.smith@nomail.com", 2234567891L,
                        List.of("uah", "eur")
                )
        );
    }

    private static void doInitialDeposit() {
        TransactionManager tm = new TransactionManager(em);
        for (Client client : clients) {
            for (Account account : client.getAccounts()) {
                try {
                    tm.doDepositFromCash(account, 100);
                } catch (Exception ex) {
                    System.out.println("ERROR : " + account.getIban() + ", " + ex.getMessage());
                }

            }
        }
    }

    private static void doInternalMoneyTransfer() {
        Client sender = new ClientFactory(em).getClient(clients.getFirst().getId());
        Client recipient = new ClientFactory(em).getClient(clients.getLast().getId());
        TransactionManager tm = new TransactionManager(em);

        String testCurrency = "usd";
        double testValue = 110d;
        try {
            tm.doWithdrawToIban(
                    sender.getAccount(testCurrency),
                    recipient.getAccount(testCurrency).getIban(),
                    testValue,
                    "sent to " + recipient.getAccount(testCurrency).getClient().getFullname()
            );
        } catch (Exception ex) {
            System.out.println("Transaction failed : " + ex.getMessage());
        }

        testCurrency = "uah";
        try {
            tm.doWithdrawToIban(
                    sender.getAccount(testCurrency),
                    recipient.getAccount(testCurrency).getIban(),
                    testValue,
                    "sent to " + recipient.getAccount(testCurrency).getClient().getFullname()
            );
        } catch (Exception ex) {
            System.out.println("Transaction failed : " + ex.getMessage());
        }

        testValue = 50d;
        try {
            tm.doInternalTransaction(
                sender.getAccount(testCurrency),
                recipient.getAccount(testCurrency),
                testValue,
                "from " + sender.getAccount(testCurrency).getClient().getFullname()
                    + " to " + recipient.getAccount(testCurrency).getClient().getFullname()
            );
        } catch (Exception ex) {
            System.out.println("Transaction failed : " + ex.getMessage());
        }
    }

    private static void doClientConversion() {
        Client client = new ClientFactory(em).getClient(clients.getLast().getId());
        ConversionManager cm = new ConversionManager(em);
        try {
            Account accountFrom = client.getAccount("uah");
            Account accountTo = client.getAccount("eur");
            cm.doConversion(accountFrom, accountTo, accountFrom.getBalance()/2);

            System.out.println(client);
        }  catch (Exception ex) {
            System.out.println("Conversion failed : " + ex.getMessage());
        }
    }

    private static void printClients() {
        System.out.println("\n");
        for (Client client : clients) {
            System.out.println(client);
        }
    }
}
