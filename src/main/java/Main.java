import java.io.*;

import com.opencsv.CSVReader;

import java.util.ArrayList;

/**
 * Main:
 * Standardizes all transactions in all provided files and stores them
 * in an ArrayList passed to the CrytoTaxCalculator
 */

public class Main {

    public static final String USAGE_ERROR = "Usage: Main.java " +
            "[-binance binance.csv] " +
            "[-bitfinex bitfinex.csv] " +
            "[-coinbase coinbase.csv] " +
            "[-kraken kraken.csv] " +
            "[-quadriga quadriga.csv]";

    public static void main(String [] args) throws FileNotFoundException, UnsupportedEncodingException {

//        String [] args = test();
        String flag;
        String fileName = "";
        String outputFileName = "CryptoTaxCalculatorOutput.csv";
        int argNum = 0;
        int numFiles = 0;
        TransactionFile transactionFile;
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        if (args.length == 0) {
            throw new IllegalArgumentException(USAGE_ERROR);
        }

        // Process each file according to their associated flag
        // and store their Transactions
        while (argNum < args.length) {
            flag = args[argNum];

            if (flag.charAt(0) == '-') {
                try {
                    fileName = args[++argNum];

                    if (flag.equals("-output")) {
                        outputFileName = fileName;
                    } else {
                        // Process and store Transactions
                        transactionFile = processFile(flag, fileName);
                        for (Transaction t: transactionFile.getTransactions()) {
                            transactions.add(t);
                        }
                        numFiles++;
                    }

                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println(USAGE_ERROR);;
                    throw e;
                } catch (FileNotFoundException e) {
                    System.err.println("Unable to process the file " + fileName);;
                    throw e;
                } catch (UnsupportedEncodingException e) {
                    System.err.println("Unsupported Encoding when processing the file: " + fileName);
                    throw e;
                }
            } else {
                throw new IllegalArgumentException(USAGE_ERROR);
            }
            argNum++;
        }

        System.out.println("Number of files processed: " + numFiles);
        System.out.println("Number of transactions: " + transactions.size() + "\n");

        CryptoTaxCalculator ctc = new CryptoTaxCalculator(transactions);
        ctc.writeToCsv(outputFileName);
        ctc.outputAssets();
    }

    /**
     * processFile  - Takes in the type of file and the file path to create
     *                the corresponding TransactionFile class
     *
     * @param flag      - The type of file
     * @param filePath  - The path to the file
     * @throws FileNotFoundException    - if file was not found
     * @throws IllegalArgumentException - unrecognized flag passed
     * @throws UnsupportedEncodingException - error opening file
     * @return TransactionFile
     */
    private static TransactionFile processFile(String flag, String filePath)
            throws FileNotFoundException, IllegalArgumentException, UnsupportedEncodingException {

        try {
            CSVReader csvReader = new CSVReader( new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            if (flag.equals("-quadriga")) {
                return new QuadrigaTransactionFile(csvReader);
            } else if (flag.equals("-coinbase")) {
                return new CoinbaseTransactionFile(csvReader);
            } else if (flag.equals("-kraken")) {
                return new KrakenTransactionFile(csvReader);
            } else if (flag.equals("-bitfinex")) {
                return new BitfinexTransactionFile(csvReader);
            } else if (flag.equals("-binance")) {
                return new BinanceTransactionFile(csvReader);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not find file: " + filePath);
            throw e;
        } catch (UnsupportedEncodingException e) {
            System.err.println("Unsupported Encoding: " + filePath);
            throw e;
        }
        throw new IllegalArgumentException("Flag not found");
    }

    private static String [] test() {
        String [] re = new String[20];
        try {
            String line;
            int idx = 0;
            BufferedReader br = new BufferedReader(new FileReader("./ignore/test.txt"));
            while((line = br.readLine()) != null) {
                re[idx++] = line;
            }
            br.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        return re;
    }

}
