package mybank.ex.service;

import mybank.ex.model.Account;
import mybank.ex.model.Transaction;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {

    final private String CASH_DEPOSIT_COMMENT = "cash income";

    final private String CASH_WITHDRAW_COMMENT = "cash withdrow";

    protected final EntityManager em;

    public TransactionManager(EntityManager em) {
        this.em = em;
    }

    public void doDepositFromIban(Account toAccount, String fromIban, double amount, String comment) {
        validateTransactionRequest(toAccount, fromIban);
        Transaction depoTrans = new Transaction(toAccount, fromIban, "+", amount, comment);
        saveTransaction(depoTrans);
    }

    public void doDepositFromCash(Account toAccount, double amount) {
        Transaction depoTrans = new Transaction(toAccount,"+", amount, CASH_DEPOSIT_COMMENT);
        saveTransaction(depoTrans);
    }

    public void doWithdrawToIban(Account fromAccount, String toIban, double amount, String comment) {
        validateTransactionRequest(fromAccount, toIban);
        Transaction withdTrans = new Transaction(fromAccount, toIban, "-", amount, comment);
        isWithdrowAllowed(withdTrans);
        saveTransaction(withdTrans);
    }

    public void doWithdrawToCash(Account fromAccount, double amount) {
        Transaction withdTrans = new Transaction(fromAccount,"-", amount, CASH_WITHDRAW_COMMENT);
        isWithdrowAllowed(withdTrans);
        saveTransaction(withdTrans);
    }

    public void doInternalTransaction(Account fromAccount, Account toAccount, double amount, String comment) {
        List<Transaction> transList = new ArrayList<>();
        transList.add(new Transaction(fromAccount, toAccount.getIban(), "-", amount, comment));
        transList.add(new Transaction(toAccount, fromAccount.getIban(), "+", amount, comment));
        saveTransaction(transList);
        System.out.println("Money transfer successfull:\n" + transList.get(0) + "\n" + transList.get(1));
    }

    protected void saveTransaction(List<Transaction> transList) {
        em.getTransaction().begin();
        try {
            for (Transaction trans: transList) {
                recalculateBalance(trans);
                Account account = trans.getAccount();
                em.persist(trans);
                em.persist(account);
                account.addTransaction(trans);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }

    protected void saveTransaction(Transaction trans) {
        recalculateBalance(trans);
        Account account = trans.getAccount();

        em.getTransaction().begin();
        try {
            em.persist(trans);
            em.persist(account);
            em.getTransaction().commit();
            account.addTransaction(trans);
            System.out.println("Saved transaction " + trans);

        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }

    protected void recalculateBalance(Transaction trans) {
        Account account = trans.getAccount();
        double newBalance = account.getBalance();
        if (trans.isDeposit())          newBalance += trans.getAmount();
        else if (trans.isWithdraw())    newBalance -= trans.getAmount();
        account.setBalance(Utility.moneyRound(newBalance, 2));
    }

    private void isWithdrowAllowed(Transaction trans) {
        if (trans.getAccount().getBalance() <= trans.getAmount()) {
            throw new RuntimeException("Your balance is less then requested value");
        }
    }

    private void validateTransactionRequest(Account account, String iban) {
        if (account.getIban().equals(iban)) {
            throw new RuntimeException("You can't create transaction for the same receiver and recipient");
        }
    }
}
