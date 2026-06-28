package ir.moke.microfox.api.http;

import java.util.regex.Pattern;

public class FilterInfo {

    private final String path;
    private final int order;
    private final Filter filter;
    private final Pattern pattern;
    private String name;
    private String category;

    public FilterInfo(String path, int order, Filter filter, String name, String category) {
        this.path = path;
        this.order = order;
        this.filter = filter;
        this.name = name;
        this.category = category;
        pattern = HttpUtils.compilePattern(path);
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

    public Filter getFilter() {
        return filter;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}