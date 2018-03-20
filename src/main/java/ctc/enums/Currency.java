package ctc.enums;

/**
 * Currency:
 * Enumerates the cryptocurrencies and fiat currencies used
 */

public enum Currency {
    CAD, USD, BNB, BTC, BTG, ETH, LTC, XRP, BCC, BCH, TRX, XVG, XBT, XLM;

    /**
     * isFiat   - Returns if a Currency is fiat or not
     * @return boolean
     */
    public boolean isFiat() {
        return (this == CAD || this == USD);
    }

    /**
     * synonymFilter    - Converts less commonly used crypto symbols
     *                    to more common ones used by the API
     * @param toFilter  - The Currency to convert
     * @return Currency
     */
    public static Currency synonymFilter(Currency toFilter) {
        if (toFilter == Currency.XBT) return Currency.BTC;
        else if (toFilter == Currency.BCC) return Currency.BCH;
        return toFilter;
    }
}
