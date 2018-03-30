package ctc.calculator;

import ctc.enums.Exchange;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * ExchangeDetector:
 * Given the first line of a csv file as a String [],
 * determines the exchange associated with the file
 */

public class ExchangeDetector {

    private HashMap<Exchange, String []> signatures;

    public ExchangeDetector(){
        signatures = new HashMap<Exchange, String[]>();
        signatures.put(Exchange.BINANCE, new String [] {
                "Date","Market","Type","Price","Amount","Total","Fee","Fee Coin"
        });
        signatures.put(Exchange.BITFINEX, new String [] {
                "#","Pair","Amount","Price","Fee","FeeCurrency","Date"
        });
        signatures.put(Exchange.COINBASE, new String [] {
                "Transactions","","","","","","","","","","","","","","","","","","","","",""
        });
        signatures.put(Exchange.KRAKEN, new String [] {
                "txid","ordertxid","pair","time","type","ordertype","price","cost","fee","vol","margin","misc","ledgers"
        });
        signatures.put(Exchange.QUADRIGA, new String [] {
                "type","major","minor","amount","rate","value","fee","total","timestamp","datetime"
        });
        signatures.put(Exchange.CTC, new String [] {
                "Exchange","Date","Trade Type","Major","Minor","Amount","Local Rate","Major Rate (CAD)",
                "Minor Rate (CAD)","Value (CAD)","Fee Currency","Fee Amount","Fee Rate (CAD)","Fee (CAD)","Gain/Loss (CAD)"
        });

    }

    /**
     * determineExchange    - Takes in the first line of a csv file (it's 'signature')
     *                        and returns the associated exchange or throws an exception
     *
     * @param signature                 - the first line of the csv
     * @throws IllegalArgumentException - if the signature does not match any of the supported exchanges
     * @return Exchange                 - The associated exchange
     */
    public Exchange determineExchange(String [] signature) {

        for (Map.Entry<Exchange, String []> entry : signatures.entrySet()) {
            if (Arrays.equals(signature, entry.getValue())) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("CSV signature does not match any supported exchanges");
    }

    public static void main (String[] args) {
        String [] s = new String[] {"Date","Market","Type","Price","Amount","Total","Fee","Fee Coin"};

        ExchangeDetector ed = new ExchangeDetector();
    }
}
