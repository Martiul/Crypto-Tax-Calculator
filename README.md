# Crypto Tax Calculator

__DISCLAIMER:__ I am not an accountant. Use at your own risk and if in doubt, always consult a professional.

This is an easy-to-use Java program that calculates the capital gains made from trading cryptocurrency using the
transaction history provided by supported exchanges. 

Simply provide the files to the program as command line arguments and the program will automatically detect which exchange the
file came from. After confirmation from the user, the calculator will begin processing all transactions, determining profits and losses using the FIFO method.

__Sample Input:__
```
"./samples/Binance.csv" "./samples/Quadriga.csv" "./samples/Bitfinex.csv" -output "./samples/output.csv"
```

__User confirmation:__
```
./samples/Binance.csv - BINANCE
./samples/Quadriga.csv - QUADRIGA
./samples/Bitfinex.csv - BITFINEX
Output file: ./samples/output.csv
Is this information correct (Y/N)? 
```

__Console output:__
```
Number of files processed: 3
Number of transactions: 4

=== REMAINING ASSETS ===
Date Bought:  Wed Nov 01 12:00:00 EDT 2017
Currency:     LTC
Amount:       5.62
Rate:         73.17
```

Too see the input and output files, jump to [Sample Scenario](#sample-scenario)

## The Difficulties with Calculating Profit/Loss
With stocks and options, it is generally easy to calculate profit and loss. 
Assuming there are no transaction fees, buying a stock of ABC at $90 and selling it at $100 means $10 profit.
Even with more complicated transactions, being able track assets with whole numbered quantities makes
calculations tangible and easier to make sense of.

However, this convenience does not exist with cryptocurrency. Cryptocurrency is more similar to forex trading, which is
trickier to calculate profits and losses from. But unlike brokerages, cryptocurrency exchanges do not do any of the
calculating for you, and they never will because they can't. 

Here is a list of difficulties faced when trying to manually calculate profit/loss

*   Decimal quantities 
    * example: Buy 0.02 BTC with a 0.001 BTC transaction fee
    * example: You bought 0.4245 ETH at rate A and 0.312 ETH at rate B. Later you sell 0.6777 ETH at rate C

*   Intercurrency transactions
    * example: Buy 10 LTC at a BTC/LTC rate of 14. In most cases, this transaction is taxable
    * image if stocks could be swapped in a stock market

*   No Indication of Value in Fiat
    * example: Calculate the gains made from the transaction in the previous point given the time it was executed
    * example: 
        * 0.40 LTC bought at LTC/CAD: 100
        * 0.60 LTC bought at BTC/LTC: 15
        * 0.55 LTC sold at LTC/EUR: 125. 

*   Determining Exchange Rates
    * the profit/loss in CAD for the second example of the previous point can only be calculate if the BTC/CAD rate is known
    and either the LTC/CAD or EUR/CAD rate are known
    * Exchange rates for cryptocurrency flucuates every hour and must be found for every transaction
    
### How the Program Overcomes These Difficulties

*   Decimal quantities
    * Computers are good at arithmetic
    * Use of Java's `BigDecimal` class ensures accuracy

*   Intercurrency transactions
    * A list of assets is kept track of during runtime with the amount bought and rate stored in a native currency (e.g. CAD)
    * Profit/loss of a transaction between cryptocurrency is calculated by selling the currency used at the current rate in a native currency

*   No Indication of Value in Fiat
    * API requests made to obtain the value in fiat

*   Determining Exchange Rates
    * API requests made to obtain exchange rates
 
 ## Sample Scenario
 Joe is looking to buy some cryptocurrency. In September he opens an account on Quadriga and decides to buy about 1 ETH 
 with Canadian dollars, paying a bit more to cover for trading fees.
 
 Later in October, Joe has transferred all his ETH to Binance and gets interested in another cryptocurrency called 
 Litecoin (LTC) which has been rising lately. In the spur of the moment he exchanges
 all his ETH for LTC.
 
 Again, Joe moves his LTC to another exchange called Bitfinex. Happy with his earlier purchase, he decides
 to buy even more LTC.
 
 Finally, near the end of the year, Joe transfers all his assets to Quadriga and sells a large chunk of his LTC.
 
 Joe downloads the `csv` files provided by the exchanges, which are as follows:
 
 _Quadriga:_

| type | major | minor | amount | rate | value   | fee   | total | timestamp  | datetime        | 
|------|-------|-------|--------|------|---------|-------|-------|------------|-----------------| 
| buy  | eth   | cad   | 1.006  | 500  | 503.0   | 0.005 | 1     | 1504267200 | 9/1/2017 12:00  | 
| sell | ltc   | cad   | 10.05  | 365  | 3668.25 | 18.25 | 3650  | 1514267200 | 12/26/2017 5:46 | 


_Binance:_

| Date            | Market | Type | Price | Amount | Total | Fee   | Fee Coin | 
|-----------------|--------|------|-------|--------|-------|-------|----------| 
| 10/1/2017 12:00 | ETHLTC | SELL | 5.52  | 1      | 5.52  | 0.001 | ETH      | 


_Bitfinex:_

| #   | Pair    | Amount | Price | Fee  | FeeCurrency | Date            | 
|-----|---------|--------|-------|------|-------------|-----------------| 
| XXX | LTC/USD | 10.2   | 54.62 | 0.05 | LTC         | 11/1/2017 12:00 | 



Feeding the files into this program, he gets the following standardized `csv` as output

| Exchange | Date                         | Trade Type | Major | Minor | Amount | Local Rate | Major Rate (CAD) | Minor Rate (CAD) | Value (CAD) | Fee Currency | Fee Amount | Fee Rate (CAD) | Fee (CAD)       | Gain/Loss (CAD) | 
|----------|------------------------------|------------|-------|-------|--------|------------|------------------|------------------|-------------|--------------|------------|----------------|-----------------|-----------------| 
| Quadriga | Fri Sep 01 12:00:00 EDT 2017 | BUY        | ETH   | CAD   | 1.006  | 500        | 500.00           | 1.00             | 503.00      | ETH          | 0.005      | 500.00         | 2.50            | 0.00            | 
| Binance  | Sun Oct 01 12:00:00 EDT 2017 | SELL       | ETH   | LTC   | 1      | 5.52       | 377.57           | 68.50            | 377.57      | ETH          | 0.001      | 377.57         | 0.38            | -122.55         | 
| Bitfinex | Wed Nov 01 12:00:00 EDT 2017 | BUY        | LTC   | USD   | 10.2   | 54.62      | 73.17            | 1.25             | 746.33      | LTC          | 0.05       | 73.17          | 3.66            | 0.23            | 
| Quadriga | Tue Dec 26 05:46:00 EST 2017 | SELL       | LTC   | CAD   | 10.05  | 365        | 365.00           | 1.00             | 3668.25     | CAD          | 18.25      | 1.00           | 18.25           | 2958.44         | 
|          |                              |            |       |       |        |            |                  |                  |             |              |            |                |                 |                 | 
|          |                              |            |       |       |        |            |                  |                  |             |              |            |                | 24.78607        | 2836.12         | 
|          |                              |            |       |       |        |            |                  |                  |             |              |            |                | Capital Gains:  | 2811.33         | 

From the output file, he made a few observations: the transaction he made to exchange
all his ETH for LTC was taxable, calculated as if he had first sold all his ETH before buying
LTC. Secondly, a trading fee once payed in LTC was also taxable. Finally, he found
that the program sold the LTC he bought in October before selling the LTC he bought
in November, following the FIFO method as expected.

 ## Design
 
UML(ish) Diagram
 
 ![UML](https://github.com/Martiul/Crypto-Tax-Calculator/raw/master/UML/CTC_UML.png "UML Diagram")

 
 In short,
 * All `TransactionFile`s have `Transaction`s and can be written to a `.csv` file
 * Exchange-specific `TransactionFile`s match external information to the standardized `Transaction` model
 * `CryptoTaxCalculator` takes in `Transaction`s and puts them into a `CalculatedTransactionFile`

Additionally, the `Transaction` class follows the Builder design pattern, avoiding the
need for many getter/setters or a constructor consuming a dozen arguments.
  
## Extensibility

Support for additional currencies can be done by adding them to the `Currency` enumeration.

Making a new `TransactionFile` to support another exchange is as simple as copying boilerplate code and then
matching `csv` headers to their corresponding field in the `Transaction` model.

 Sample code from `BinanceTransactionFile`
 
 ```
 HashMap <String, String> hm = createHashMap(header, csvLine);
 Transaction t = new Transaction("Binance")
         .date(hm.get("Date"))
         .type(hm.get("Type"))
         .major(hm.get("Market").substring(0,3))
         .minor(hm.get("Market").substring(3,6))
         .localRate(hm.get("Price"))
         .amount(hm.get("Amount"))
         .feeCurrency(hm.get("Fee Coin"))
         .feeAmount(hm.get("Fee"))
         .build();
 ```
 
 ## Other Considerations
 * Used Java's BigDecimal class to maintain accuracy when dealing with decimals
 
 * Some cryptocurrencies have multiple symbols. For example, BTC and XBT are both
 symbols for Bitcoin. However, the API used in this calculator only accepts BTC, 
 so the `Currency` enumeration has a `synonymFilter` function which returns the most common symbol of a currency
 
 * The program outputs a list of remaining assets and the rates at which
 they were purchased. This is useful for carrying assets forward to the next tax year
 
 