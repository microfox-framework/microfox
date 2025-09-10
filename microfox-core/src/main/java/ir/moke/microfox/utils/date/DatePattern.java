package ir.moke.microfox.utils.date;

public enum DatePattern {
    YEAR_DATE_PATTERN("yyyy"),                                  // 2023
    SMALLEST_DATE_PATTERN("yyMMdd"),                            // 081215
    MINIMIZED_DATE_PATTERN("yyyyMMdd"),                         // 20231020
    DATE_PATTERN("yyyy/MM/dd"),                                 // 2023/10/20
    DATE_BY_HYPHEN_PATTERN("yyyy-MM-dd"),                       // 2023-08-16
    DATE_TIME_PATTERN("yyyy/MM/dd HH:mm"),                      // 2023/10/20 16:15
    DATE_TIME_BY_HYPHEN_PATTERN("yyyy-MM-dd HH:mm"),            // 2023-08-16 16:15
    FULL_DATE_TIME_PATTERN("yyyy/MM/dd HH:mm:ss"),              // 2023/10/20 16:15:30
    DAY_MONTH_YEAR_TIME_PATTERN("dd/MM/yyyy HH:mm"),            // 2023/10/20 16:15:30
    FULL_DATE_TIME_BY_HYPHEN_PATTERN("yyyy-MM-dd HH:mm:ss"),    // 2023-08-16 16:15:30
    FULL_DATE_TIME_STRING_PATTERN("EEE, d MMM yyyy HH:mm:ss"),  // Wed, 4 Oct 2023 16:15:30
    TIMESTAMP_DATE_TIME_PATTERN("yyyyMMddHHmmss"),              // 20231020233030
    TIME_PATTERN("HH:mm"),                                      // 16:15
    FULL_TIME_PATTERN("HH:mm:ss"),                              // 16:15:20
    FULL_TIME_WITH_OFFSET_PATTERN("HH:mm:ss.SSSX"),                 // 12:30:00.000+0330
    MINIMIZED_TIME_PATTERN("HHmmss"),                           // 103045
    ISO_TRANSMISSION_DATE_PATTERN("MMddHHmmss"),                // 1020233030
    ISO_LOCAL_TRANSACTION_DATE_TIME_PATTERN("yyMMddHHmmss"),    // 081215233030
    ISO_CAPTURE_DATE_PATTERN("MMdd"),                           // 1020
    ISO_SETTLEMENT_DATE_PATTERN("ddMMyy"),                      // 202310
    ISO_SETTLEMENT_FULL_DATE_PATTERN("ddMMyyyy"),               // 20232017
    ISO_EXPIRY_DATE_PATTERN("yyMM"),                            // 1010
    DATE_TIME_SPACE_PATTERN("yyyyMMdd HHmmss"),                 // 20231020 103030
    ISO_8601("yyyy-MM-dd'T'HH:mm:ss.SSSX"),                   // 2015-06-27T13:16:37.363Z
    FULL_DATE_TIME_BY_HYPHEN_PATTERN_WITH_T_SEPERATOR("yyyy-MM-dd'T'HH:mm:ss");    // 2023-08-16T16:15:30

    private final String pattern;

    DatePattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return pattern;
    }
}