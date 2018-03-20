import ctc.enums.Currency;
import ctc.enums.TradeType;

import java.math.BigDecimal;
import java.util.*;

public class CalculatedTransactionFile extends TransactionFile {

    private final HashMap<ctc.enums.Currency, LinkedList<Asset>> assets;

    public CalculatedTransactionFile(ArrayList<Transaction> transactions) {

        assets = new HashMap<ctc.enums.Currency, LinkedList<Asset>>();
        for (ctc.enums.Currency currency : ctc.enums.Currency.values()) {
            assets.put(currency, new LinkedList<Asset>());
        }

        Collections.sort(transactions);

        for (Transaction t: transactions) {
            CalculatedTransaction ct = new CalculatedTransaction(t);

            if (t.getType() == TradeType.BUY) {
                // Add to assets
                assets.get(t.getMajor()).add(new Asset(
                        t.getDate(),
                        t.getMajor(),
                        t.getAmount(),
                        t.getMajorRate()
                ));
                // If minor was a cryptocurrency, then gain was likely realized
                // (minor was sold)
                if (!t.getMinor().isFiat()) {
                    ct.setGainLoss(sell(t.getMinor(), t.getAmount().multiply(t.getLocalRate()), t.getMinorRate()));
                } else {
                    ct.setGainLoss(BigDecimal.ZERO);
                }
            } else {
                // e.g. sell ETH for BTC
                //      sell ETH for CAD
                //      sell CAD for ETH
                ct.setGainLoss(sell(t.getMajor(), t.getAmount(), t.getMajorRate()));
                if (!t.getMinor().isFiat()) {
                    // Sold something for crypto.
                    // i.e. gained crypto

                    assets.get(t.getMinor()).add(new Asset(
                            t.getDate(),
                            t.getMinor(),
                            t.getAmount().multiply(t.getLocalRate()),
                            t.getMinorRate()
                    ));
                }
            }
            addTransaction(ct);
        }
    }

    private BigDecimal sell(ctc.enums.Currency currencySold, BigDecimal amountSold, BigDecimal rateSold) {
        BigDecimal amountBought;
        BigDecimal rateBought;
        BigDecimal gainLoss = BigDecimal.ZERO;

        while (!amountSold.equals(BigDecimal.ZERO)) {
            try {
                Asset earliest = assets.get(currencySold).element();
                amountBought = earliest.getAmount();
                rateBought = earliest.getRate();

                // Take amount. Calculate profit
                // If more amount, take next element. repeat
                if (amountBought.compareTo(amountSold) > 0) {
                    // This can cover all sold
                    earliest.setAmount(amountBought.subtract(amountSold));
                    gainLoss = gainLoss.add((rateSold.subtract(rateBought)).multiply(amountSold));
                    amountSold = BigDecimal.ZERO;

//                    if (earliest.getAmount().doubleValue() < 0.00001) {
//                        assets.get(currencySold).remove();  // Basically gone
//                    }
                } else {
                    // Need this and another
                    gainLoss = gainLoss.add((rateSold.subtract(rateBought)).multiply(amountBought));
                    amountSold = amountSold.subtract(amountBought);
                    assets.get(currencySold).remove();
                }


            } catch (NoSuchElementException e) {
                System.err.println("No history of " + currencySold + " being bought before being sold. (Is this a fork or token?)");
                return rateSold.multiply(amountSold);
            }
        }
        return gainLoss;
    }

    public void outputAssets() {
        LinkedList<Asset> currencyAssets;

        System.out.println("=== REMAINING ASSETS ===");
        for (Map.Entry<Currency, LinkedList<Asset> > entry : assets.entrySet()) {
            currencyAssets = entry.getValue();
            if (!currencyAssets.isEmpty()) {
                for (Asset asset : currencyAssets) {
                    System.out.println(asset.toString());
                }
                System.out.println();
            }
        }
    }

}
