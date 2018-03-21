package ctc.transaction.files;

import com.opencsv.CSVReader;
import ctc.enums.Currency;
import ctc.transactions.Transaction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * KrakenansactionFile:
 * Standardizes the transactions in a csv file provided by Kraken
 */

public class KrakenTransactionFile extends TransactionFile {

    public KrakenTransactionFile(CSVReader csv) {

        try {
            String [] header = csv.readNext();
            String [] csvLine;

            while ((csvLine = csv.readNext()) != null) {
                HashMap <String, String> hm = createHashMap(header, csvLine);

                // Almost all currency pairs are in the form XABCXDEF,
                // with the one exception of those with BCH
                String tradePair = hm.get("pair");
                String major;
                String minor;

                if (tradePair.length() == 6) {
                    // e.g. BCHXBT
                    major = hm.get("pair").substring(0, 3);
                    minor = hm.get("pair").substring(3, 6);
                } else {
                    // e.g. XETHXXBT
                    major = hm.get("pair").substring(1, 4);
                    minor = hm.get("pair").substring(5, 8);
                }

                Transaction t = new Transaction("Kraken")
                        .date(hm.get("time"))
                        .type(hm.get("type"))
                        .major(major)
                        .minor(minor)
                        .localRate(hm.get("price"))
                        .amount(hm.get("vol"))
                        .feeCurrency(Currency.BTC)      // Unique to Kraken
                        .feeAmount(hm.get("fee"))
                        .build();

                addTransaction(t);
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to read next line in Kraken file");
        }
    }

    public static void main(String [] args) throws FileNotFoundException {
        KrakenTransactionFile tf = new KrakenTransactionFile(new CSVReader(new FileReader("Kraken.csv")));
        tf.seeTransactions();
    }

}
