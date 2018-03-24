# Crypto Tax Calculator

DISCLAIMER: I am not an accountant. Use at your own risk and if in doubt, always consult a professional.

This is an easy-to-use Java program that calculates the capital gains made from trading cryptocurrency using the
transaction history provided by supported exchanges. 

Simply provide the files to the program as command line arguments and the program will automatically detect which exchange the
file came from and then begin processing all transactions, calculating profits and losses using the FIFO method.

```
Sample usage
```

## The Difficulties with Calculating Profit/Loss
With stocks and options, it is generally easy to calculate profit and loss. 
Assuming there are no transaction fees, Buying a stock of ABC at $90 and selling it at $100 means $10 profit.
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
    * example: Buy 10 LTC at a BTC/LTC rate of 14. This transaction is taxable for most cases
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
 Joe is looking to buy some cryptocurrency. In September he opens an account on Quadriga and decides to buy about 1 ETH, 
 with Canadian dollars, paying a bit more to cover for trading fees.
 
 Later in October, Joe has transferred all his ETH to Binance and gets interested in another cryptocurrency called 
 Litecoin (LTC), which has been rising while ETH has been falling. In the spur of the moment he exchanges
 all his ETH for LTC.
 
 Again, Joe moves his LTC to another exchange called Bitfinex. Happy with his earlier purchase, he decides
 to buy even more LTC.
 
 Finally, near the end of the year, Joe transfers all his assets to Quadriga and sells a large chunk of his LTC.
 
 Joe downloads the `csv` files provided by the exchanges, which are as follows:
 
 Quadriga:

| type | major | minor | amount | rate | value   | fee   | total | timestamp  | datetime        | 
|------|-------|-------|--------|------|---------|-------|-------|------------|-----------------| 
| buy  | eth   | cad   | 1.006  | 500  | 502.5   | 0.005 | 1     | 1504267200 | 9/1/2017 12:00  | 
| sell | ltc   | cad   | 10.05  | 365  | 3668.25 | 18.25 | 3650  | 1514267200 | 12/26/2017 5:46 | 


Binance:
| Date            | Market | Type | Price | Amount | Total | Fee   | Fee Coin | 
|-----------------|--------|------|-------|--------|-------|-------|----------| 
| 10/1/2017 12:00 | ETHLTC | SELL | 5.52  | 1      | 5.52  | 0.001 | ETH      | 


Bitfinex:
| #   | Pair    | Amount | Price | Fee  | FeeCurrency | Date            | 
|-----|---------|--------|-------|------|-------------|-----------------| 
| XXX | LTC/USD | 10.2   | 54.62 | 0.05 | LTC         | 11/1/2017 12:00 | 



Feeding the files into this program, he can get the follow `csv` as output
| Exchange | Date                         | Trade Type | Major | Minor | Amount | Local Rate | Major Rate (CAD) | Minor Rate (CAD) | Value (CAD) | Fee Currency | Fee Amount | Fee Rate (CAD) | Fee (CAD)       | Gain/Loss (CAD) | 
|----------|------------------------------|------------|-------|-------|--------|------------|------------------|------------------|-------------|--------------|------------|----------------|-----------------|-----------------| 
| Quadriga | Fri Sep 01 12:00:00 EDT 2017 | BUY        | ETH   | CAD   | 1.006  | 500        | 500.00           | 1.00             | 503.00      | ETH          | 0.005      | 500.00         | 2.50            | 0.00            | 
| Binance  | Sun Oct 01 12:00:00 EDT 2017 | SELL       | ETH   | LTC   | 1      | 5.52       | 377.57           | 68.50            | 377.57      | ETH          | 0.001      | 377.57         | 0.38            | -122.55         | 
| Bitfinex | Wed Nov 01 12:00:00 EDT 2017 | BUY        | LTC   | USD   | 10.2   | 54.62      | 73.17            | 1.25             | 746.33      | LTC          | 0.05       | 73.17          | 3.66            | 0.23            | 
| Quadriga | Tue Dec 26 05:46:00 EST 2017 | SELL       | LTC   | CAD   | 10.05  | 365        | 365.00           | 1.00             | 3668.25     | CAD          | 18.25      | 1.00           | 18.25           | 2958.44         | 
|          |                              |            |       |       |        |            |                  |                  |             |              |            |                |                 |                 | 
|          |                              |            |       |       |        |            |                  |                  |             |              |            |                | 24.78607        | 2836.11747      | 
|          |                              |            |       |       |        |            |                  |                  |             |              |            |                | Capital Gains:  | 2811.33140      | 

From the output file, he made a few observations: the transaction he made to exchange
all his ETH for LTC was taxable, calculated as if he had first sold all his ETH before buying
LTC. Secondly, a trading fee he had to once pair in LTC was also taxable. Finally, he found
that the program sold the LTC he bought in October before selling the LTC he bought
in November, following the FIFO method.

 ## How It's Built and Extendability