import java.util.ArrayList;

public class CryptoTaxCalculator {

    private final CalculatedTransactionFile ctf;

    // Takes an ArrayList of transactions and calculates CG
    public CryptoTaxCalculator(ArrayList<Transaction> transactions) {
        ctf = new CalculatedTransactionFile(transactions);
    }

    public void writeToCsv (String fileName) {
        ctf.writeToCsv(fileName);
    }

    public void outputAssets () {
        ctf.outputAssets();
    }
}
