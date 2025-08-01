package ir.moke.microfox.utils;

public enum CalendarType {
    BUDDHIST("buddhist"),
    CHINESE("chinese"),
    COPTIC("coptic"),
    ETHIOPIC("ethiopic"),
    GREGORIAN("gregorian"),
    HEBREW("hebrew"),
    ISLAMIC("islamic"),
    ISLAMIC_CIVIL("islamic-civil"),
    JAPANESE("japanese"),
    ROC("roc"),
    PERSIAN("persian");
    private final String value;

    CalendarType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
