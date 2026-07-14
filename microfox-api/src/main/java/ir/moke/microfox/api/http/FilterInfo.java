package ir.moke.microfox.api.http;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.regex.Pattern;

public class FilterInfo {

    private final String path;
    private final Filter filter;
    private final Pattern pattern;
    private String description;
    private String category;
    private String hash;
    private int order;

    public FilterInfo(String path, int order, Filter filter, String description, String category) {
        this.path = path;
        this.order = order;
        this.filter = filter;
        this.description = description;
        this.category = category;
        pattern = HttpUtils.compilePattern(path);
        this.hash = DigestUtils.md5Hex(path + description + category);
    }

    public FilterInfo(String path, int order, Filter filter) {
        this.path = path;
        this.order = order;
        this.filter = filter;
        pattern = HttpUtils.compilePattern(path);
    }

    public String getPath() {
        return path;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Filter getFilter() {
        return filter;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHash() {
        return hash;
    }
}