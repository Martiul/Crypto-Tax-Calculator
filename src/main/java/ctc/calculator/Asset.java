package ctc.calculator;

import ctc.enums.Currency;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Asset:
 * Represents an amount of cryptocurrency the user bought
 * at a specific date and rate. Used to calculate gain/losses
 */

public class Asset implements Comparable<Asset> {
    private Date dateBought;
    private Currency currency;
    private BigDecimal amount;
    private BigDecimal rate;

    public Asset(Date dateBought, Currency currency, BigDecimal amount, BigDecimal rate) {
        this.dateBought = dateBought;
        this.currency = currency;
        this.amount = amount;
        this.rate = rate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Date getDateBought() {
        return dateBought;
    }

    public void setDateBought(Date dateBought) {
        this.dateBought = dateBought;
    }

    public int compareTo(Asset that) {
        if (that == null) {
            throw new IllegalArgumentException("Cannot compare null Asset");
        }
        return this.dateBought.compareTo(that.dateBought);
    }

    public String toString() {
        return    "Date Bought:  " + dateBought +
                "\nCurrency:     " + currency +
                "\nAmount:       " + amount +
                "\nRate:         " + rate;
    }
}


