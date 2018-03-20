import com.opencsv.CSVReader;
import ctc.enums.Currency;
import ctc.enums.TradeType;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * CoinbaseransactionFile:
 * Standardizes the transactions in a csv file provided by Coinbase
 */

public class CoinbaseTransactionFile extends TransactionFile {

    public CoinbaseTransactionFile(CSVReader csv) {

        try {
            // Remove misc lines
            for (int i = 0; i < 4; i++) {
                csv.readNext();
            }

            String [] header = csv.readNext();
            String [] csvLine;

            while ((csvLine = csv.readNext()) != null) {

                HashMap <String, String> hm = createHashMap(header, csvLine);

                // TODO: Modify for non-Canadian files (lack of reference file)
                Currency transactionNative = new Transaction().getNative();
                double amount = Double.parseDouble(hm.get("Amount"));
                double localRate;

                // Coinbase combines SELL and withdrawals into the same file
                // Currently no support for SELL
                if (amount < 0) {
                    continue;
                }
                localRate = Double.parseDouble(hm.get("Transfer Total")) / amount;  // Rate not provided

                Transaction t = new Transaction("Coinbase")
                        .date(hm.get("Timestamp"))
                        .type(TradeType.BUY)
                        .major(hm.get("Currency"))
                        .minor(transactionNative)
                        .localRate(localRate)
                        .amount(hm.get("Amount"))
                        .feeCurrency(transactionNative)
                        .feeAmount(0)
                        .build();

                addTransaction(t);
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to read next line");
        }
    }

    public static void main(String [] args) throws FileNotFoundException {
        CoinbaseTransactionFile tf = new CoinbaseTransactionFile(new CSVReader(new FileReader("Coinbase ETH.csv")));
        tf.seeTransactions();
    }

}
