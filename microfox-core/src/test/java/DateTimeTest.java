import ir.moke.microfox.utils.date.CalendarType;
import ir.moke.microfox.utils.date.DatePattern;
import ir.moke.microfox.utils.date.DateTimeUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

public class DateTimeTest {

    @Test
    public void checkConvertZonedDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        String enOutput = DateTimeUtils.toString(zonedDateTime, Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.ISO_8601);
        String faOutput = DateTimeUtils.toString(zonedDateTime, Locale.of("fa_IR"), CalendarType.PERSIAN, DatePattern.ISO_8601);
        String faOutputIslamic = DateTimeUtils.toString(zonedDateTime, Locale.of("fa_IR"), CalendarType.ISLAMIC, DatePattern.ISO_8601);
        String arOutputIslamic = DateTimeUtils.toString(zonedDateTime, Locale.of("ar_SA"), CalendarType.ISLAMIC, DatePattern.ISO_8601);
        System.out.println(enOutput);
        System.out.println(faOutput);
        System.out.println(faOutputIslamic);
        System.out.println(arOutputIslamic);
    }

    @Test
    public void checkStringToInstant() {
        // TimeZone : Asia/Tehran
        String en = "1404-05-12T23:16:22.386Z";
        String fa = "۱۴۰۴-۰۵-۱۲T۲۳:۱۶:۲۲.۳۸۶Z";
        String faIslamic = "۱۴۴۷-۰۲-۱۰T۲۳:۱۶:۲۲.۳۸۶Z";
        String arIslamic = "١٤٤٧-٠٢-١٠T٢٣:١٦:٢٢.٣٨٦Z";

        ZonedDateTime enOutput = DateTimeUtils.fromString(en, ZoneId.of("Asia/Tehran"), Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.ISO_8601, ZonedDateTime.class);
        ZonedDateTime faOutput = DateTimeUtils.fromString(fa, ZoneId.of("Asia/Tehran"), Locale.of("fa_IR"), CalendarType.PERSIAN, DatePattern.ISO_8601, ZonedDateTime.class);
        ZonedDateTime faIslamicOutput = DateTimeUtils.fromString(faIslamic, ZoneId.of("Asia/Tehran"), Locale.of("fa_IR"), CalendarType.ISLAMIC, DatePattern.ISO_8601, ZonedDateTime.class);
        ZonedDateTime arIslamicOutput = DateTimeUtils.fromString(arIslamic, ZoneId.of("Asia/Tehran"), Locale.of("ar_SA"), CalendarType.ISLAMIC, DatePattern.ISO_8601, ZonedDateTime.class);
        System.out.println(enOutput);
        System.out.println(faOutput);
        System.out.println(faIslamicOutput);
        System.out.println(arIslamicOutput);

        // Migrate TimeZone : UTC
        ZonedDateTime utc = enOutput.withZoneSameInstant(ZoneId.of("UTC"));
        System.out.println(utc);
    }

    @Test
    public void checkStringToLocalTime() {
        // TimeZone : Asia/Tehran
        String fullDateTimeZoneString = "1404-05-12T23:16:22.386Z";
        LocalTime output1 = DateTimeUtils.fromString(fullDateTimeZoneString, ZoneId.of("Asia/Tehran"), Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.ISO_8601, LocalTime.class);
        System.out.println(output1);

        String timeString = "23:16:22";
        LocalTime output2 = DateTimeUtils.fromString(timeString, ZoneId.of("Asia/Tehran"), Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.FULL_TIME_PATTERN, LocalTime.class);
        System.out.println(output2);
    }
}
