package ctc.calculator;

import ctc.transactions.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * CalculatedTransaction:
 * A Transaction with an additional gain/loss field
 */

public class CalculatedTransaction extends Transaction {
    private BigDecimal gainLoss;

    public CalculatedTransaction(Transaction t) {
        super(t);
    }

    public CalculatedTransaction gainLoss(double gainLoss) {
        this.gainLoss = BigDecimal.valueOf(gainLoss);
        return this;
    }

    public void setGainLoss(double gainLoss) {
        this.gainLoss = BigDecimal.valueOf(gainLoss);
    }

    public void setGainLoss(BigDecimal gainLoss) {
        this.gainLoss = gainLoss;
    }

    public BigDecimal getGainLoss() {
        return gainLoss;
    }

    public String [] getHeader() {
        ArrayList<String> re = new ArrayList<String>(Arrays.asList(super.getHeader()));
        re.add("Gain/Loss (" + super.getNative() + ")");
        return re.toArray(new String[re.size()]);
    }

    public String [] toCsv() {
        ArrayList<String> re = new ArrayList<String>(Arrays.asList(super.toCsv()));
        re.add(String.format("%.2f", gainLoss));
        return re.toArray(new String[re.size()]);
    }

    public String toString() {
        String re = super.toString();
        re = re + "\n Gain/Loss:   " + String.format("%.2f", gainLoss) + super.getNative();
        return re;
    }
}
