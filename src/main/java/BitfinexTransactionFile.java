import com.opencsv.CSVReader;
import ctc.enums.TradeType;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * BitfinexransactionFile:
 * Standardizes the transactions in a csv file provided by Bitfinex
 */

public class BitfinexTransactionFile extends TransactionFile {

    public BitfinexTransactionFile(CSVReader csv) {

        try {
            String [] header = csv.readNext();
            String [] csvLine;

            while ((csvLine = csv.readNext()) != null) {

                HashMap <String, String> hm = createHashMap(header, csvLine);

                // BUY and SELL are differentiated based on the sign of "Amount"
                double amount = Double.parseDouble(hm.get("Amount"));
                TradeType tradeType = (amount > 0)? TradeType.BUY : TradeType.SELL;

                // Sample pair: ABC/DEF
                String [] currencyPair = hm.get("Pair").split("/");
                assert (currencyPair.length == 2);

                Transaction t = new Transaction("Bitfinex")
                        .date(hm.get("Date"))
                        .type(tradeType)
                        .major(currencyPair[0])
                        .minor(currencyPair[1])
                        .localRate(hm.get("Price"))
                        .amount(Math.abs(amount))
                        .feeCurrency(hm.get("FeeCurrency"))
                        .feeAmount(Math.abs(Double.parseDouble(hm.get("Fee"))))
                        .build();

                addTransaction(t);
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to read line in Bitfinex File");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Incorrect Market format for Bitfinex File");
        }
    }

    public static void main(String [] args) throws FileNotFoundException {
        BitfinexTransactionFile tf = new BitfinexTransactionFile(new CSVReader(new FileReader("Bitfinex.csv")));
        tf.seeTransactions();
    }

}
