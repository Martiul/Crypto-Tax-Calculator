package ctc.transaction.files;

import com.opencsv.CSVReader;
import ctc.transactions.Transaction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

public class QuadrigaTransactionFile extends TransactionFile {

    public QuadrigaTransactionFile(CSVReader csv) {

        try {
            String [] header = csv.readNext();
            String [] csvLine;

            while ((csvLine = csv.readNext()) != null) {
                HashMap <String, String> hm = createHashMap(header,csvLine);

                // Special properties:
                // The currency of amount is Major
                // The currency of fee is major for buys, minor for sells (incoming currency)

                String feeCurrency;
                if (hm.get("type").equals("buy")) {
                    feeCurrency = hm.get("major");
                } else {
                    feeCurrency = hm.get("minor");
                }

                Transaction t = new Transaction("Quadriga")
                        .date(hm.get("datetime"))
                        .type(hm.get("type"))
                        .major(hm.get("major"))
                        .minor(hm.get("minor"))
                        .localRate(hm.get("rate"))
                        .amount(hm.get("amount"))
                        .feeCurrency(feeCurrency)
                        .feeAmount(hm.get("fee"))
                        .build();

                addTransaction(t);
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to read next line in Quadriga file");
        }
    }

    public static void main(String [] args) throws FileNotFoundException {
        QuadrigaTransactionFile tf = new QuadrigaTransactionFile(new CSVReader(new FileReader("Quadriga December.csv")));
        tf.seeTransactions();
    }
}
