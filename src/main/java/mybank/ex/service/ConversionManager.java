package mybank.ex.service;

import mybank.ex.model.Account;
import mybank.ex.model.Currency;
import mybank.ex.model.Transaction;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ConversionManager extends TransactionManager{
    public ConversionManager(EntityManager em) {
        super(em);
    }

    public void doConversion(Account fromAccount, Account toAccount, double originalAmount) {
        Currency origCurr = fromAccount.getCurrency();
        Currency convCurr = toAccount.getCurrency();
        String comment = "Conversion from " + origCurr.getCode() + " to " + convCurr.getCode();
        double convertedAmount = Utility.convertMoneyValue(originalAmount, origCurr, convCurr);
        List<Transaction> transList = new ArrayList<>();
        transList.add(new Transaction(fromAccount, toAccount.getIban(), "-", originalAmount, comment));
        transList.add(new Transaction(toAccount, fromAccount.getIban(), "+", convertedAmount, comment));
        saveTransaction(transList);
        System.out.println("Conversion successfull:\n" + transList.get(0) + "\n" + transList.get(1));
    }
}
