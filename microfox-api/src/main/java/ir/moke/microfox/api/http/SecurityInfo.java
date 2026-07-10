package ir.moke.microfox.api.http;

import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.utils.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

public class SecurityInfo {

    private final String path;
    private final SecurityStrategy strategy;
    private final Pattern pattern;
    private String name;
    private String category;
    private String hash;
    private int order;

    public SecurityInfo(String path, SecurityStrategy strategy, int order, String name, String category) {
        this.path = path;
        this.strategy = strategy;
        this.name = name;
        this.category = category;
        pattern = HttpUtils.compilePattern(path);
        this.hash = StringUtils.randomHash();
        this.order = order;
    }

    public SecurityInfo(String path, SecurityStrategy strategy, int order) {
        this.path = path;
        this.strategy = strategy;
        this.order = order;
        pattern = HttpUtils.compilePattern(path);
        this.hash = StringUtils.randomHash();
    }

    public String getPath() {
        return path;
    }

    public SecurityStrategy getStrategy() {
        return strategy;
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

    public String getHash() {
        return hash;
    }

    public int getOrder() {
        return order;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SecurityInfo routeInfo = (SecurityInfo) o;
        return Objects.equals(path, routeInfo.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}