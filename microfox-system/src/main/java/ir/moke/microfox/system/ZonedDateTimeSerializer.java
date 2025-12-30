package ir.moke.microfox.system;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ir.moke.utils.date.CalendarType;
import ir.moke.utils.date.DatePattern;
import ir.moke.utils.date.DateTimeUtils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Locale;

public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {
    @Override
    public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String str = DateTimeUtils.toString(zonedDateTime, Locale.ENGLISH, CalendarType.GREGORIAN, DatePattern.ISO_8601);
        jsonGenerator.writeString(str);
    }
}
