import java.io.*;

import com.opencsv.CSVReader;
import ctc.calculator.CryptoTaxCalculator;
import ctc.calculator.ExchangeDetector;
import ctc.enums.Exchange;
import ctc.transaction.files.*;
import ctc.transactions.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

    public static void main(String [] argss) throws NullPointerException {

        String [] args = inputByFile("sample.txt");
        String arg;
        int argNum = 0;
        int numFiles = 0;
        boolean validFiles = false;
        String outputFileName = "CryptoTaxCalculatorOutput.csv";

        TransactionFile transactionFile;
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        HashMap<String, Exchange> matchedExchanges = new HashMap<String, Exchange>();

        if (args.length == 0) {
            throw new IllegalArgumentException(USAGE_ERROR);
        }

        // Go through arguments and detect the Exchange each csv came from
        Exchange exchangeOfFile;
        ExchangeDetector detector = new ExchangeDetector();
        CSVReader csvReader = null;
        while (argNum < args.length) {
            arg = args[argNum];
            validFiles = false;

            try {
                if (arg.equals("-output")) {
                    outputFileName = args[++argNum];
                    validFiles = true;
                } else {
                    csvReader = new CSVReader(new InputStreamReader(new FileInputStream(arg), "UTF-8"));
                    String[] signature = csvReader.readNext();

                    exchangeOfFile = detector.determineExchange(signature);
                    matchedExchanges.put(arg, exchangeOfFile);

                    numFiles++;
                    validFiles = true;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println(USAGE_ERROR);;
            } catch (FileNotFoundException e) {
                System.err.println("Unable to find the file " + arg);;
            } catch (UnsupportedEncodingException e) {
                System.err.println("Unsupported Encoding when processing the file: " + arg);
            } catch (IOException e) {
                System.err.println("IO Error");
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            } finally {
                if (csvReader != null) {
                    try {
                        csvReader.close();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }

            if (!validFiles) {
                break;
            }
            argNum++;
        }

        if (validFiles && numFiles > 0) {
            for (Map.Entry<String, Exchange> file : matchedExchanges.entrySet()) {
                System.out.println(file.getKey() + " - " + file.getValue());
            }
            System.out.println("Output file: " + outputFileName);
            System.out.print("Is this the correct information (Y/N)? ");
            Scanner sc = new Scanner(System.in);
            char input = sc.next().charAt(0);
            if (input == 'y' || input == 'Y') {
                try {
                    for (Map.Entry<String, Exchange> matching : matchedExchanges.entrySet()) {
                        transactionFile = processFile(matching.getKey(), matching.getValue());
                        for (Transaction t : transactionFile.getTransactions()) {
                            transactions.add(t);
                        }
                    }
                    System.out.println("Number of files processed: " + numFiles);
                    System.out.println("Number of transactions: " + transactions.size() + "\n");

                    CryptoTaxCalculator ctc = new CryptoTaxCalculator(transactions);
                    ctc.writeToCsv(outputFileName);
                    ctc.outputAssets();
                } catch (IOException e) {

                }
            }
        } else if (numFiles == 0) {
            System.out.println("No files were provided");
        }

    }

    /**
     * processFile  - Takes in the type of file and the file path to create
     *                the corresponding TransactionFile class
     *
     * @param filePath      - The path to the file
     * @param exchange      - The exchange the file came from
     * @throws FileNotFoundException    - if file was not found
     * @throws IllegalArgumentException - unrecognized flag passed
     * @throws UnsupportedEncodingException - error opening file
     * @return TransactionFile
     */
    private static TransactionFile processFile(String filePath, Exchange exchange)
            throws FileNotFoundException, IllegalArgumentException, UnsupportedEncodingException {

        try {
            CSVReader csvReader = new CSVReader( new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            if (exchange == Exchange.BINANCE) {
                return new BinanceTransactionFile(csvReader);
            } else if (exchange == Exchange.BITFINEX) {
                return new BitfinexTransactionFile(csvReader);
            } else if (exchange == Exchange.COINBASE) {
                return new CoinbaseTransactionFile(csvReader);
            } else if (exchange == Exchange.KRAKEN) {
                return new KrakenTransactionFile(csvReader);
            } else if (exchange == Exchange.QUADRIGA) {
                return new QuadrigaTransactionFile(csvReader);
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


    private static String [] inputByFile(String fileName) {
        ArrayList<String> al = new ArrayList<String>();
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader("./ignore/" + fileName));
            while((line = br.readLine()) != null) {
                al.add(line);
            }
            br.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

        return al.toArray(new String [0]);
    }

}
