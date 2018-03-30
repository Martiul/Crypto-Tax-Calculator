package ctc.transaction.files;

import com.opencsv.CSVReader;
import ctc.transactions.Transaction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * BinanceTransactionFile:
 * Standardizes the transactions in a csv file provided by Binance
 */

public class CTCTransactionFile extends TransactionFile {

    public CTCTransactionFile(CSVReader csv) {

        try {
            csv.readNext();
            String [] csvLine;

            while ((csvLine = csv.readNext()) != null && !csvLine[0].equals("")) {
                Transaction t = new Transaction(csvLine);
                addTransaction(t);
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to read next line");
        }
    }

    public static void main(String [] args) throws FileNotFoundException {
        CTCTransactionFile tf = new CTCTransactionFile(new CSVReader(new FileReader("output.csv")));
        tf.seeTransactions();
    }
}
