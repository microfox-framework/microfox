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
    TEXT_CSS("text/css"),
    TEXT_CSV("text/csv"),
    TEXT_EVENT_STREAM("text/event-stream"),
    TEXT_HTML("text/html"),
    TEXT_JAVASCRIPT("text/javascript"),
    TEXT_PLAIN("text/plain"),
    TEXT_XML("text/xml"),
    FONT_WOFF2("font/woff2"),
    IMAGE_APNG("image/apng"),
    IMAGE_AVIF("image/avif"),
    IMAGE_GIF("image/gif"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_SVG_XML("image/svg+xml"),
    IMAGE_WEBP("image/webp");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
