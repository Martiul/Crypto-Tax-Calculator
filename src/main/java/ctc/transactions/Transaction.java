package ctc.transactions;

import ctc.enums.Currency;
import ctc.enums.Exchange;
import ctc.enums.TradeType;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Transaction:
 * A standardized class for every Transaction that allows
 * for gains and losses to be calculated
 * Implemented using Builder design pattern and BigDecimal for accuracy
 */
public class Transaction implements Serializable, Comparable<Transaction> {
    private Exchange exchange;
    private Date date;
    private TradeType type;
    private Currency major;
    private Currency minor;
    private BigDecimal amount;          // Amount of major
    private BigDecimal localRate;       // Major-Minor rate
    private BigDecimal majorRate;       // Major-NATIVE rate
    private BigDecimal minorRate;       // Minor-NATIVE rate
    private BigDecimal value;
    private Currency feeCurrency;
    private BigDecimal feeAmount;
    private BigDecimal feeRate;
    private BigDecimal fee;
    private final Currency NATIVE = Currency.CAD;
    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public Transaction() {}

    public Transaction(Transaction other) {
        this.exchange = other.exchange;
        this.date = other.date;
        this.type = other.type;
        this.major = other.major;
        this.minor = other.minor;
        this.amount = other.amount;
        this.localRate = other.localRate;
        this.majorRate = other.majorRate;
        this.minorRate = other.minorRate;
        this.value = other.value;
        this.feeCurrency = other.feeCurrency;
        this.feeAmount = other.feeAmount;
        this.feeRate = other.feeRate;
        this.fee = other.fee;
    }

    public Transaction(String [] fromFile) {
        this.exchange = Exchange.valueOf(fromFile[0]);
        this.date = new Date(fromFile[1]);
        this.type = TradeType.valueOf(fromFile[2]);
        this.major = Currency.valueOf(fromFile[3]);
        this.minor = Currency.valueOf(fromFile[4]);
        this.amount = new BigDecimal(fromFile[5]);
        this.localRate = new BigDecimal(fromFile[6]);
        this.majorRate = new BigDecimal(fromFile[7]);
        this.minorRate = new BigDecimal(fromFile[8]);
        this.value = new BigDecimal(fromFile[9]);
        this.feeCurrency = Currency.valueOf(fromFile[10]);
        this.feeAmount = new BigDecimal(fromFile[11]);
        this.feeRate = new BigDecimal(fromFile[12]);
        this.fee = new BigDecimal(fromFile[13]);
    }

    // Exchange
    public Transaction (String exchange) {
        this.exchange = Exchange.valueOf(exchange.toUpperCase());
    }

    public Exchange getExchange() {
        return exchange;
    }


    // Date
    public Transaction date (String date) {
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse date of " + exchange + " file");
        }
        this.date = parsedDate;
        return this;
    }

    public Date getDate() {
        return date;
    }


    // Type
    public Transaction type (String type) {
        this.type = TradeType.valueOf(type.toUpperCase());
        return this;
    }

    public Transaction type (TradeType type) {
        this.type = type;
        return this;
    }

    public TradeType getType() {
        return type;
    }


    // Major
    public Transaction major (String major) {
        this.major = Currency.synonymFilter(Currency.valueOf(major.toUpperCase()));
        return this;
    }

    public Transaction major (Currency major) {
        this.major = Currency.synonymFilter(major);
        return this;
    }

    public Currency getMajor() {
        return major;
    }


    // Minor
    public Transaction minor (String minor) {
        this.minor = Currency.synonymFilter(Currency.valueOf(minor.toUpperCase()));;
        return this;
    }

    public Transaction minor (Currency minor) {
        this.minor = Currency.synonymFilter(minor);
        return this;
    }

    public Currency getMinor() {
        return minor;
    }


    // LocalRate
    public Transaction localRate (String localRate) {
        this.localRate = new BigDecimal(localRate);
        return this;
    }

    public Transaction localRate (double localRate) {
        this.localRate = BigDecimal.valueOf(localRate);
        return this;
    }

    public BigDecimal getLocalRate() {
        return localRate;
    }


    // Amount
    public Transaction amount (String amount) {
        this.amount = new BigDecimal(amount);
        return this;
    }

    public Transaction amount (double amount) {
        assert(amount > 0);
        this.amount = BigDecimal.valueOf(amount);
        return this;
    }

    public Transaction amount (BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }


    // FeeCurrency
    public Transaction feeCurrency (String feeCurrency) {
        this.feeCurrency = Currency.valueOf(feeCurrency.toUpperCase());
        return this;
    }

    public Transaction feeCurrency (Currency feeCurrency) {
        this.feeCurrency = feeCurrency;
        return this;
    }

    public Currency getFeeCurrency() {
        return feeCurrency;
    }


    // FeeAmount
    public Transaction feeAmount (String feeAmount) {
        this.feeAmount = new BigDecimal(feeAmount);
        return this;
    }
    public Transaction feeAmount (double feeAmount) {
        this.feeAmount = BigDecimal.valueOf(feeAmount);
        return this;
    }
    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    // FeeRate
    public BigDecimal getFeeRate() {
        return feeRate;
    }

    // Calculated fields
    public BigDecimal getMajorRate() {
        return majorRate;
    }

    public BigDecimal getMinorRate() {
        return minorRate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getFee() {
        return fee;
    }

    /**
     * build    - Builds the remaining fields after all the other
     *            components have been set.
     *            Calculates major/minor rate, value and fee
     * @return Transaction
     * @throws IOException  - if cannot get exchange rate
     */
    // Sets majorRate, minorRate, value  and fee
    public Transaction build() throws IOException {

        // Special Cases for sending major/minor rate:
        // Case NATIVE/ABC: MajorRate is 1,
        //                  MinorRate is reciprocal of localRate
        // Case ABC/NATIVE: MajorRate is LocalRate
        //                  MinorRate is 1
        if (major == NATIVE) {
            majorRate = BigDecimal.ONE;
            minorRate = BigDecimal.ONE.divide(localRate,6, RoundingMode.HALF_UP);
        } else if (minor == NATIVE) {
            majorRate = localRate;
            minorRate = BigDecimal.ONE;
        } else {
            majorRate = getExchangeRate(major, NATIVE);
            minorRate = getExchangeRate(minor, NATIVE);
        }
        value = majorRate.multiply(amount);

        BigDecimal feeRate;
        if (feeCurrency == major) {
            feeRate = majorRate;
        } else if (feeCurrency == minor) {
            feeRate = minorRate;
        } else {
            feeRate = getExchangeRate(feeCurrency, NATIVE);
        }
        fee = feeRate.multiply(feeAmount);
        this.feeRate = feeRate;

        return this;
    }

    // Transactions compared based on Date
    public int compareTo(Transaction that) {
        if (that == null) {
            throw new IllegalArgumentException("Cannot compare null Transaction");
        }
        return this.date.compareTo(that.date);
    }

    public Currency getNative() {
        return NATIVE;
    }

    public String [] getHeader() {
        return new String[]{
                "Exchange",
                "Date",
                "Trade Type",
                "Major",
                "Minor",
                "Amount",
                "Local Rate",
                "Major Rate (" + NATIVE + ")",
                "Minor Rate (" + NATIVE + ")",
                "Value (" + NATIVE + ")",
                "Fee Currency",
                "Fee Amount",
                "Fee Rate (" + NATIVE + ")",
                "Fee (" + NATIVE + ")",
        };
    }

    public String [] toCsv() {
        return new String[]{
                exchange.toString(),
                date.toString(),
                type.toString(),
                major.toString(),
                minor.toString(),
                amount.toString(),
                localRate.toString(),
                String.format("%.2f", majorRate),
                String.format("%.2f", minorRate),
                String.format("%.2f", value),
                feeCurrency.toString(),
                feeAmount.toString(),
                String.format("%.2f", feeRate),
                String.format("%.2f", fee)
        };
    }

    public String toString() {
        return    "Exchange:     " + exchange +
                "\nDate:         " + date.toString() +
                "\nType:         " + type +
                "\nMajor:        " + major +
                "\nMinor:        " + minor +
                "\nAmount:       " + String.format("%.6f", amount) +
                "\nLocalRate:    " + String.format("%.6f", localRate) +
                "\nMajorRate:    " + String.format("%.6f", majorRate) +
                "\nMinorRate:    " + String.format("%.6f", minorRate) +
                "\nValue:        " + String.format("%.2f", value) + " " + NATIVE +
                "\nFeeCurrency:  " + feeCurrency +
                "\nFeeAmount:    " + String.format("%.6f", feeAmount) +
                "\nFee Rate:     " + String.format("%.2f",feeRate) + " " + NATIVE +
                "\nFee:          " + String.format("%.2f",fee) + " " + NATIVE;

    }


    /**
     * getExchangeRate  - Finds the exchange rate for @major/@minor
     *                    using the cryptocompare free API
     * @param major - the major currency
     * @param minor - the minor currency
     * @return BigDecimal
     * @throws IOException  - if desired response data was not provided
     * @throws RuntimeException - if cannot connect to API
     */
    private BigDecimal getExchangeRate (Currency major, Currency minor)
            throws IOException, RuntimeException {

        // First filter out less-used synonyms
        major = Currency.synonymFilter(major);
        minor = Currency.synonymFilter(minor);
        if (major == minor) {
            return BigDecimal.ONE;
        }

        // API host has bad data for XRP. Use USD as intermediate
        if ((major == Currency.XRP && minor == Currency.CAD) ||
                (major == Currency.CAD && minor == Currency.XRP)) {

            BigDecimal xrp_usd = getExchangeRate(Currency.XRP, Currency.USD);
            BigDecimal usd_cad = getExchangeRate(Currency.USD, Currency.CAD);

            if (major == Currency.XRP) {
                return xrp_usd.multiply(usd_cad);
            } else {
                return BigDecimal.ONE.divide(usd_cad,6, RoundingMode.HALF_UP).multiply(
                        BigDecimal.ONE.divide(xrp_usd,6, RoundingMode.HALF_UP)
                );
            }
        }

        // Set up URL and call API
        String price = null;
        long timestamp = date.getTime()/1000;
        String url = "https://min-api.cryptocompare.com/data/histohour?fsym=" + major
                + "&tsym=" + minor + "&limit=1&toTs=" + timestamp;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");


        int responseCode = con.getResponseCode();
//        System.out.println("Response code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {

            String line;
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while ((line = in.readLine()) != null) {
                JSONObject parser = new JSONObject(line);
                // System.out.println(parser.toString());        // Output json
                price = parser.getJSONArray("Data").getJSONObject(0).get("close").toString();
//                System.out.println(date + ": " + major + "/" + minor + " - " + price);
            }
            in.close();
        } else {
            throw new RuntimeException("Unable to connect to API");
        }
        return new BigDecimal(price);
    }

}
