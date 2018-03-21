package ctc.transaction.files;

import com.opencsv.CSVReader;
import ctc.transactions.Transaction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * BinanceTransactionFile:
 * Standardizes the transactions in a csv file provided by Binance
 */

public class BinanceTransactionFile extends TransactionFile {

    public BinanceTransactionFile(CSVReader csv) {

        try {
            String [] header = csv.readNext();
            String [] csvLine;

            while ((csvLine = csv.readNext()) != null) {

                HashMap <String, String> hm = createHashMap(header, csvLine);

                Transaction t = new Transaction("Binance")
                        .date(hm.get("Date"))
                        .type(hm.get("Type"))
                        .major(hm.get("Market").substring(0,3))
                        .minor(hm.get("Market").substring(3,6))
                        .localRate(hm.get("Price"))
                        .amount(hm.get("Amount"))
                        .feeCurrency(hm.get("Fee Coin"))
                        .feeAmount(hm.get("Fee"))
                        .build();

                addTransaction(t);
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to read next line");
        }
    }

    public static void main(String [] args) throws FileNotFoundException {
        BinanceTransactionFile tf = new BinanceTransactionFile(new CSVReader(new FileReader("Binance.csv")));
        tf.seeTransactions();
    }
}
