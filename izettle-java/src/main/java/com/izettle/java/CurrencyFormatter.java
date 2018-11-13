package com.izettle.java;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

public class CurrencyFormatter {

    protected static final char NBSP = '\u00a0';
    protected static final char SPACE = ' ';
    protected static final char MINUS_SIGN = '\u2212';
    protected static final char HYPHEN = '-';

    /**
     * Will return the amount represented in the currency's minimal value, eg fractionized amount.
     * Ex. "12" will be returned as 1200
     *
     * Method intended to be used whenever raw user numerical input needs to be interpreted into a franctionized amount.
     *
     * @param currencyId The id of the currency
     * @param amount The amount to convert - no grouping allowed, and dot for decimal separator
     * @return the fractionized amount
     */
    public static long parse(CurrencyId currencyId, String amount) {
        Currency currency = Currency.getInstance(currencyId.name());
        Number numericValue = Double.valueOf(amount);
        return Math.round(numericValue.doubleValue() * StrictMath.pow(10, currency.getDefaultFractionDigits()));
    }

    /**
     * Will return the amount represented in the currency's minimal value, eg fractionized amount.
     * Ex. "SEK12" will be returned as 1200
     *
     * Method intended to be used whenever complete GUI strings needs to be interpreted into a fractionized amount.
     *
     * @param currencyId The id of the entered currency
     * @param locale the locale of the input - needed to make sure decimal separators and grouping characters are
     *        interpreted correctly
     * @param amount The amount to convert
     * @return the fractionized amount
     * @throws ParseException If the string is not parseable as a number
     */
    public static long parse(
        final CurrencyId currencyId,
        final Locale locale,
        final String amount
    ) throws ParseException {
        final Currency currency = Currency.getInstance(currencyId.name());
        final DecimalFormat currencyFormatter = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        currencyFormatter.setCurrency(currency);
        final char groupingSeparator = currencyFormatter.getDecimalFormatSymbols().getGroupingSeparator();
        final char minusSign = currencyFormatter.getDecimalFormatSymbols().getMinusSign();
        final String currencySymbol = currencyFormatter.getDecimalFormatSymbols().getCurrencySymbol();
        String toParse = amount;
        if (HYPHEN == minusSign) {
            toParse = toParse.replace(MINUS_SIGN, HYPHEN);
        }
        if (MINUS_SIGN == minusSign) {
            toParse = toParse.replace(HYPHEN, MINUS_SIGN);
        }
        if (SPACE == groupingSeparator && currencySymbol.indexOf(NBSP) < 0) {
            toParse = toParse.replace(NBSP, SPACE);
        }
        if (NBSP == groupingSeparator && currencySymbol.indexOf(SPACE) < 0) {
            toParse = toParse.replace(SPACE, NBSP);
        }
        final Number numericValue = currencyFormatter.parse(toParse);
        return new BigDecimal(numericValue.doubleValue())
            .movePointRight(currency.getDefaultFractionDigits())
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact();
    }

    /**
     * Will return a string representation of the provided fractionizedAmount. This string can be used for displaying
     * a monetary value to a user.
     *
     * Example: 45564 will return "455,64 kr" for SEK and Swedish locale.
     *
     * @param currencyId the id of the currency, ex SEK
     * @param locale the locale of the user to present the string to
     * @param fractionizedAmount the amount to format
     * @return Formatted string, eg. "10.000,34 SEK" for SEK and Swedish locale.
     */
    public static String format(CurrencyId currencyId, Locale locale, long fractionizedAmount) {
        Currency currency = Currency.getInstance(currencyId.name());
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        currencyFormatter.setCurrency(currency);
        currencyFormatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
        double amount = fractionizedAmount * StrictMath.pow(10, -currency.getDefaultFractionDigits());
        return currencyFormatter.format(amount);
    }
}
