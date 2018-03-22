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
    * example: Buy 10 LTC at a BTC/LTC rate of 14. This transaction is in fact, taxable
    * image if stocks could be swapped in a stock market (think bartering)
*   No Indication of Value in Fiat
    * example: Calculate the gains made from the transaction in the previous point given the time it was executed and no other information
    * example: 
        * 0.40 LTC bought at LTC/CAD: 100
        * 0.60 LTC bought at BTC/LTC: 15
        * 0.55 LTC sold at LTC/EUR: 125. 
*   Determining Exchange Rates
    * the profit/loss in CAD for the second example of the previous point can only be calculate if the BTC/CAD rate is known
    and either the LTC/CAD or EUR/CAD rate are known
    * Exchange rates for cryptocurrency flucuates every hour and must be found for every transaction
    
## How the Program Overcomes The Difficulties
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
 
 ## How It's Built and Extendability