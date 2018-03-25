package ctc.transaction.files;

import com.opencsv.CSVWriter;
import ctc.transactions.Transaction;

import java.io.*;
import java.util.*;

/**
 * TransactionFile:
 * Abstract class for exchange-specific TransactionFiles
 * to inherit from
 */
public abstract class TransactionFile {

    private final ArrayList<Transaction> transactions;

    protected TransactionFile() {
        transactions = new ArrayList<Transaction>();
    }

    /**
     * addTransaction   - Filters out the majr
     * @param transaction - Adds the Transaction to the list of Transactions
     */
    protected void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * getTransactions  - Returns a copy of the list of Transactions
     * @return Iterable<Transaction>
     */
    public Iterable<Transaction> getTransactions() {
        return new ArrayList<Transaction>(transactions);
    }

    /**
     * numberOfCells  - Returns the number of cells used by the file
     * @return int
     */
    protected int numberOfCells() {
        // TODO throw
        assert (transactions.size() > 0);
        return transactions.get(0).getHeader().length;
    }

    /**
     * numberOfTransactions  - Returns the number of transactions recorded
     * @return int
     */
    public int numberOfTransactions() {
        return transactions.size();
    }

    /**
     * createHashMap    - Creates a HashMap for csv files, when the
     *                    keys are the header values and the values
     *                    the corresponding entries
     * @param header - the csv header
     * @param data  - the csv data
     * @return HashMap<String,String>
     */
    protected HashMap<String, String> createHashMap(String [] header, String [] data) {
        assert(header.length == data.length);
        HashMap<String, String> re = new HashMap<String, String>();
        for (int i = 0; i < header.length; i++) {
            re.put(header[i], data[i]);
        }
        return re;
    }

    /**
     * seeTransactions  - Output all the Transactions in the ArrayList
     */
    public void seeTransactions() {
        for (Transaction t : transactions) {
            System.out.println(t.toString());
            System.out.println();
        }
    }

    /**
     * writeToCsv   - Outputs all Transaction into a specified fileName
     * @param fileName - the name of the file to write to
     */
    public void writeToCsv(String fileName) {
        writeToCsv(fileName, null);
    }

    /**
     * writeToCsv   - Outputs all Transaction into a specified fileName
     * @param fileName - the name of the file to write to
     */
    public void writeToCsv(String fileName, String [][] additionalLines) {
        try {
            Writer writer = new FileWriter(fileName);

            CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END
            );

            csvWriter.writeNext(transactions.get(0).getHeader());
            for (Transaction t : transactions) {
                csvWriter.writeNext(t.toCsv());
            }

            if (additionalLines != null) {
                for (String [] line : additionalLines) {
                    csvWriter.writeNext(line);
                }
            }

            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            System.err.println("Unable to open " + fileName);
        }
    }


}
