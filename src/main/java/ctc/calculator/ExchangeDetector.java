package ctc.calculator;

import ctc.enums.Exchange;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ExchangeDetector {

    public HashMap<Exchange, String []> signatures;

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
    }

    public Exchange determineExchange(String [] signature) {
        for (Map.Entry<Exchange, String []> entry : signatures.entrySet()) {
            if (Arrays.equals(signature, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void main (String[] args) {
        String [] s = new String[] {"Date","Market","Type","Price","Amount","Total","Fee","Fee Coin"};

        ExchangeDetector ed = new ExchangeDetector();
        System.out.println(ed.determineExchange(s));
    }
}
