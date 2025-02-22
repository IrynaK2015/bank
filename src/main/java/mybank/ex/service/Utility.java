package mybank.ex.service;

import mybank.ex.model.Currency;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;

public class Utility {
    public static String getRandomIban() {
        return "UA" + RandomStringUtils.random(27, false, true);
    }

    public static double moneyRound(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));

        return bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double convertMoneyValue(double moneyValue, Currency currencyFrom, Currency currencyTo) {
        double convertedValue = moneyValue * currencyFrom.getRate() / currencyTo.getRate();

        return moneyRound(convertedValue, 2);
    }
}
