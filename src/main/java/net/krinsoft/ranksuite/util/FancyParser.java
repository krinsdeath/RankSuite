package net.krinsoft.ranksuite.util;

/**
 * @author krinsdeath
 */
public class FancyParser {

    /**
     * Creates a fancy formatted time string from the specified input minutes
     * @param minutes The amount of minutes to format into a fancy time string
     * @return The formatted fancy time string
     */
    public static String toFancyTime(int minutes) {
        if (minutes <= 0) {
            return "0 minutes";
        }
        StringBuilder time = new StringBuilder();
        int days = minutes / 60 / 24;
        if (days > 0) {
            time.append(days).append(" day").append(days > 1 ? "s" : "");
        }
        int hours = minutes / 60 % 24;
        if (hours > 0) {
            if (days > 0) {
                time.append(", ");
            }
            time.append(hours).append(" hour").append(hours > 1 ? "s" : "");
        }
        minutes = minutes % 60;
        if (minutes > 0) {
            if (hours > 0) {
                time.append(", ");
            }
            time.append(minutes).append(" minute").append(minutes > 1 ? "s": "");
        }
        return time.toString();
    }

    private static final String[] ONES = {
            "",
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine",
            "ten",
            "eleven",
            "twelve",
            "thirteen",
            "fourteen",
            "fifteen",
            "sixteen",
            "seventeen",
            "eighteen",
            "nineteen"
    };

    private static final String[] TENS = {
            "",
            "ten",
            "twenty",
            "thirty",
            "forty",
            "fifty",
            "sixty",
            "seventy",
            "eighty",
            "ninety"
    };

    private static final String[] TENTHS = {
            "",
            "",
            "twentieth",
            "thirtieth",
            "fortieth",
            "fiftieth",
            "sixtieth",
            "seventieth",
            "eightieth",
            "ninetieth"
    };

    private static final String[] ORDINALS = {
            "",
            "first",
            "second",
            "third",
            "fourth",
            "fifth",
            "sixth",
            "seventh",
            "eighth",
            "ninth",
            "tenth",
            "eleventh",
            "twelfth",
            "thirteenth",
            "fourteenth",
            "fifteenth",
            "sixteenth",
            "seventeenth",
            "eighteenth",
            "nineteenth"
    };



    /**
     * Converts a number to a fancy ordinal version<br />
     * 1 -> first<br />
     * 53 -> fifty third<br />
     * @param num The number to be converted
     * @return The fancy ordinal string
     */
    public static String toFancyOrdinal(int num) {
        StringBuilder ordinal = new StringBuilder();
        if (num < 20) {
            ordinal.append(ORDINALS[num]);
        } else {
            if (num / 1000 > 0) {
                ordinal.append(toFancyThousands(num / 1000));
                num %= 1000;
                if (num == 0) {
                    return ordinal.append("th").toString();
                }
                ordinal.append(" ");
            }
            if (num / 100 > 0) {
                ordinal.append(toFancyHundreds(num / 100));
                num %= 100;
                if (num == 0) {
                    return ordinal.append("th").toString();
                }
                ordinal.append(" ");
            }
            if (num / 10 > 0) {
                ordinal.append(ORDINALS[num]);
            }
        }
        return ordinal.toString();
    }

    private static String toFancyThousands(int num) {
        StringBuilder ordinal = new StringBuilder();
        if (num < 20) {
            return ONES[num] + " thousand";
        } else {
            if (num / 100 > 0) {
                ordinal.append(ONES[num / 100]).append(" hundred");
                num %= 100;
            }
            if (num / 10 > 0) {
                ordinal.append(" ");
                if (num % 10 == 0) {
                    ordinal.append(TENTHS[num / 10]);
                } else {
                    ordinal.append(TENS[num / 10]);
                }
                num %= 10;
            }
            if (num > 0) {
                ordinal.append(" ").append(ONES[num]);
            }
            return ordinal.append(" thousand").toString();
        }
    }

    private static String toFancyHundreds(int num) {
        StringBuilder ordinal = new StringBuilder();
        if (num < 20) {
            return ONES[num] + " hundred";
        } else {
            if (num / 100 > 0) {
                ordinal.append(ONES[num / 100]).append(" hundred");
                num %= 100;
            }
            if (num / 10 > 0) {
                ordinal.append(" ");
                if (num % 10 == 0) {
                    ordinal.append(TENTHS[num / 10]);
                } else {
                    ordinal.append(TENS[num / 10]);
                }
                num %= 10;
            }
            if (num > 0) {
                ordinal.append(" ").append(ONES[num]);
            }
            return ordinal.append(" hundred").toString();
        }
    }

}
