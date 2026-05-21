package ir.moke.microfox.api.http;

public enum CORSHeader {
    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),
    ACCESS_CONTROL_ALLOW_METHODS("Access-Control-Allow-Methods"),
    ACCESS_CONTROL_ALLOW_HEADERS("Access-Control-Allow-Headers"),
    ACCESS_CONTROL_ALLOW_CREDENTIALS("Access-Control-Allow-Credentials"),
    ACCESS_CONTROL_MAX_AGE("Access-Control-Max-Age");

    private final String value;

    CORSHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
