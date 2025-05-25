package ir.moke.microfox.http;

public enum ContentType {
    APPLICATION_JAVA_ARCHIVE("application/java-archive"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_XHTML_XML("application/xhtml+xml"),
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    APPLICATION_ZIP("application/zip"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    text_css("text/css"),
    text_csv("text/csv"),
    text_event_stream("text/event-stream"),
    text_html("text/html"),
    text_javascript("text/javascript"),
    text_plain("text/plain"),
    text_xml("text/xml");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
