package ir.moke.microfox.utils;

public enum DatePattern {
    YEAR_DATE_PATTERN("yyyy"),                                  // 2010
    SMALLEST_DATE_PATTERN("yyMMdd"),                            // 101020
    MINIMIZED_DATE_PATTERN("yyyyMMdd"),                         // 20101020
    DATE_PATTERN("yyyy/MM/dd"),                                 // 2010/10/20
    DATE_BY_HYPHEN_PATTERN("yyyy-MM-dd"),                       // 2010-10-20
    DATE_TIME_PATTERN("yyyy/MM/dd HH:mm"),                      // 2010/10/20 10:30
    DATE_TIME_BY_HYPHEN_PATTERN("yyyy-MM-dd HH:mm"),            // 2010-10-20 10:30
    FULL_DATE_TIME_PATTERN("yyyy/MM/dd HH:mm:ss"),              // 2010/10/20 10:30:30
    DAY_MONTH_YEAR_TIME_PATTERN("dd/MM/yyyy HH:mm"),            // 2010/10/20 10:30:30
    FULL_DATE_TIME_BY_HYPHEN_PATTERN("yyyy-MM-dd HH:mm:ss"),    // 2010-10-20 10:30:30
    FULL_DATE_TIME_STRING_PATTERN("EEE, d MMM yyyy HH:mm:ss"),  // Wed, 4 Oct 2010 10:30:30
    TIMESTAMP_DATE_TIME_PATTERN("yyyyMMddHHmmss"),              // 20101020103030
    TIME_PATTERN("HH:mm"),                                      // 10:30
    FULL_TIME_PATTERN("HH:mm:ss"),                              // 10:30:20
    MINIMIZED_TIME_PATTERN("HHmmss"),                           // 103045
    ISO_TRANSMISSION_DATE_PATTERN("MMddHHmmss"),                // 1020103030
    ISO_LOCAL_TRANSACTION_DATE_TIME_PATTERN("yyMMddHHmmss"),    // 101020103030
    ISO_CAPTURE_DATE_PATTERN("MMdd"),                           // 1020
    ISO_SETTLEMENT_DATE_PATTERN("ddMMyy"),                      // 201010
    ISO_SETTLEMENT_FULL_DATE_PATTERN("ddMMyyyy"),               // 20102017
    ISO_EXPIRY_DATE_PATTERN("yyMM"),                            // 1010
    DATE_TIME_SPACE_PATTERN("yyyyMMdd HHmmss"),                 // 20101020 103030
    ISO_8601("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),                   // 2015-06-27T13:16:37.363Z
    FULL_DATE_TIME_BY_HYPHEN_PATTERN_WITH_T_SEPERATOR("yyyy-MM-dd'T'HH:mm:ss");    // 2010-10-20T10:30:30

    private final String pattern;

    DatePattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return pattern;
    }
}