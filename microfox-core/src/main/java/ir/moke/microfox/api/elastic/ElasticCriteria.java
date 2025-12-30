package ir.moke.microfox.api.elastic;

import ir.moke.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ElasticCriteria {
    private final List<Map<String, Object>> must = new ArrayList<>();
    private final List<Map<String, Object>> should = new ArrayList<>();
    private final List<Map<String, Object>> mustNot = new ArrayList<>();
    private final List<Map<String, Object>> filter = new ArrayList<>();
    private Integer from;
    private Integer size;
    private final List<Map<String, Object>> sort = new ArrayList<>();

    public static Builder builder() {
        return new Builder(new ElasticCriteria());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> root = new LinkedHashMap<>();
        if (from != null) root.put("from", from);
        if (size != null) root.put("size", size);
        if (!sort.isEmpty()) root.put("sort", sort);

        Map<String, Object> bool = new LinkedHashMap<>();
        if (!must.isEmpty()) bool.put("must", must);
        if (!should.isEmpty()) bool.put("should", should);
        if (!mustNot.isEmpty()) bool.put("must_not", mustNot);
        if (!filter.isEmpty()) bool.put("filter", filter);
        root.put("query", Collections.singletonMap("bool", bool));
        return root;
    }

    public String toJson() {
        return JsonUtils.toJson(toMap());
    }

    public static class Builder {
        private final ElasticCriteria c;
        private NestedBuilder currentNested;
        private final Deque<NestedBuilder> stack = new ArrayDeque<>();
        private final Logger logger = LoggerFactory.getLogger(Builder.class);

        private Builder(ElasticCriteria c) {
            this.c = c;
        }

        public Builder match(String field, Object value) {
            return addQuery("match", normalizeField(field), value, c.must);
        }

        public Builder term(String field, Object value) {
            return addQuery("term", normalizeField(field), value, c.must);
        }

        public Builder prefix(String field, String value) {
            return addQuery("prefix", normalizeField(field), value, c.must);
        }

        public Builder wildcard(String field, String value) {
            return addQuery("wildcard", normalizeField(field), value, c.must);
        }

        public Builder exists(String field) {
            c.must.add(Collections.singletonMap("exists", Collections.singletonMap("field", normalizeField(field))));
            return this;
        }

        public RangeBuilder range(String field) {
            return new RangeBuilder(this, normalizeField(field), c.must);
        }

        public Builder mustMatch(String field, Object value) {
            return addQuery("match", normalizeField(field), value, sectionList("must"));
        }

        public Builder shouldMatch(String field, Object value) {
            return addQuery("match", normalizeField(field), value, sectionList("should"));
        }

        public Builder mustNotMatch(String field, Object value) {
            return addQuery("match", normalizeField(field), value, sectionList("must_not"));
        }

        public NestedBuilder nested(String path, String boolClause) {
            if (path == null || path.isEmpty()) {
                logger.error("Nested path must not be null or empty");
                throw new IllegalArgumentException("Nested path must not be null or empty");
            }
            NestedBuilder nb = new NestedBuilder(this, path, boolClause);
            if (currentNested != null) stack.push(currentNested);
            currentNested = nb;
            return nb;
        }

        public Builder from(int from) {
            if (from < 0) {
                logger.error("From value must be non-negative: {}", from);
                throw new IllegalArgumentException("From value must be non-negative");
            }
            c.from = from;
            return this;
        }

        public Builder size(int size) {
            if (size < 0) {
                logger.error("Size value must be non-negative: {}", size);
                throw new IllegalArgumentException("Size value must be non-negative");
            }
            c.size = size;
            return this;
        }

        public Builder sort(String field, String order) {
            if (!List.of("asc", "desc").contains(order.toLowerCase())) {
                logger.error("Invalid sort order: {}", order);
                throw new IllegalArgumentException("Sort order must be 'asc' or 'desc'");
            }
            c.sort.add(Collections.singletonMap(normalizeField(field), Collections.singletonMap("order", order)));
            return this;
        }

        public ElasticCriteria build() {
            if (currentNested != null) {
                logger.error("Unfinished nested query – call endNested() before build()");
                throw new IllegalStateException("Unfinished nested query – call endNested() before build()");
            }
            return c;
        }

        private String normalizeField(String field) {
            if (field.endsWith(".keyword")) {
                return field.substring(0, field.length() - 8);
            }
            return field;
        }

        private List<Map<String, Object>> sectionList(String section) {
            return switch (section) {
                case "must" -> c.must;
                case "should" -> c.should;
                case "must_not" -> c.mustNot;
                case "filter" -> c.filter;
                default -> {
                    logger.error("Unknown bool section: {}", section);
                    throw new IllegalArgumentException("Unknown bool section " + section);
                }
            };
        }

        private Builder addQuery(String type, String field, Object value, List<Map<String, Object>> target) {
            if (field == null || field.isEmpty()) {
                logger.error("Field must not be null or empty for {} query", type);
                throw new IllegalArgumentException("Field must not be null or empty");
            }
            if (value == null) {
                logger.error("Value must not be null for {} query on field {}", type, field);
                throw new IllegalArgumentException("Value must not be null");
            }
            target.add(Collections.singletonMap(type, Collections.singletonMap(field, value)));
            return this;
        }
    }

    public static class RangeBuilder {
        private final Builder parent;
        private final String field;
        private final Map<String, Object> range = new LinkedHashMap<>();
        private final List<Map<String, Object>> target;
        private final Logger logger = LoggerFactory.getLogger(RangeBuilder.class);

        private RangeBuilder(Builder parent, String field, List<Map<String, Object>> target) {
            if (field == null || field.isEmpty()) {
                logger.error("Field must not be null or empty for range query");
                throw new IllegalArgumentException("Field must not be null or empty");
            }
            this.parent = parent;
            this.field = field;
            this.target = target;
        }

        public RangeBuilder gte(Object v) {
            range.put("gte", v);
            return this;
        }

        public RangeBuilder lte(Object v) {
            range.put("lte", v);
            return this;
        }

        public RangeBuilder gt(Object v) {
            range.put("gt", v);
            return this;
        }

        public RangeBuilder lt(Object v) {
            range.put("lt", v);
            return this;
        }

        public Builder end() {
            if (range.isEmpty()) {
                logger.error("Range query for field {} is empty", field);
                throw new IllegalStateException("Range query must specify at least one bound");
            }
            target.add(Collections.singletonMap("range", Collections.singletonMap(field, range)));
            return parent;
        }
    }

    public static class NestedBuilder {
        private final Builder parent;
        private final String path;
        private final String boolClause;
        private final List<Map<String, Object>> must = new ArrayList<>();
        private final List<Map<String, Object>> should = new ArrayList<>();
        private final List<Map<String, Object>> mustNot = new ArrayList<>();
        private final Map<String, Object> innerHits = new LinkedHashMap<>();
        private final Logger logger = LoggerFactory.getLogger(NestedBuilder.class);

        private NestedBuilder(Builder parent, String path, String boolClause) {
            this.parent = parent;
            this.path = path;
            this.boolClause = boolClause;
        }

        public NestedBuilder mustMatch(String field, Object value) {
            must.add(Collections.singletonMap("match", Collections.singletonMap(parent.normalizeField(field), value)));
            return this;
        }

        public NestedBuilder shouldMatch(String field, Object value) {
            should.add(Collections.singletonMap("match", Collections.singletonMap(parent.normalizeField(field), value)));
            return this;
        }

        public NestedBuilder range(String field, Object gte, Object lte) {
            Map<String, Object> r = new LinkedHashMap<>();
            if (gte != null) r.put("gte", gte);
            if (lte != null) r.put("lte", lte);
            if (r.isEmpty()) {
                logger.error("Range query for field {} is empty", field);
                throw new IllegalStateException("Range query must specify at least one bound");
            }
            must.add(Collections.singletonMap("range", Collections.singletonMap(parent.normalizeField(field), r)));
            return this;
        }

        public NestedBuilder withInnerHits(String name) {
            innerHits.put("name", name);
            return this;
        }

        public Builder endNested() {
            Map<String, Object> bool = new LinkedHashMap<>();
            if (!must.isEmpty()) bool.put("must", must);
            if (!should.isEmpty()) bool.put("should", should);
            if (!mustNot.isEmpty()) bool.put("must_not", mustNot);

            Map<String, Object> nestedQuery = new LinkedHashMap<>();
            nestedQuery.put("path", path);
            nestedQuery.put("query", Collections.singletonMap("bool", bool));
            if (!innerHits.isEmpty()) nestedQuery.put("inner_hits", innerHits);

            parent.sectionList(boolClause).add(Collections.singletonMap("nested", nestedQuery));

            if (!parent.stack.isEmpty()) {
                parent.currentNested = parent.stack.pop();
            } else {
                parent.currentNested = null;
            }
            return parent;
        }
    }
}
