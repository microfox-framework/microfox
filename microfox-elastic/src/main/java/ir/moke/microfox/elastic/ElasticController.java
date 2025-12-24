package ir.moke.microfox.elastic;

import ir.moke.microfox.api.elastic.BulkOperation;
import ir.moke.microfox.api.elastic.ElasticCriteria;
import ir.moke.microfox.api.elastic.Index;
import ir.moke.microfox.api.elastic.MappingBuilder;
import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElasticController {
    private static final Logger logger = LoggerFactory.getLogger(ElasticController.class);

    public static <T> void createIndex(String identity, String index, Class<T> clazz) {
        boolean annotationPresent = clazz.isAnnotationPresent(Index.class);
        if (!annotationPresent)
            throw new MicrofoxException("Class " + clazz.getName() + " must be annotated with @Index");

        Map<String, Object> map = MappingBuilder
                .forEntity(clazz)
                .shards(clazz.getDeclaredAnnotation(Index.class).shard())
                .replicas(clazz.getDeclaredAnnotation(Index.class).replica())
                .build();
        String mappingJson = JsonUtils.toJson(map);
        var response = ElasticHttpClient.put(identity, "/" + index, mappingJson);
        logger.trace("Created index {}: [{}]", index, response.body());
        checkResponse(response);
    }

    public static void deleteIndex(String identity, String index) {
        var response = ElasticHttpClient.delete(identity, "/" + index);
        logger.trace("Deleted index {}: [{}]", index, response.body());
    }

    public static void reindex(String identity, String srcIndex, String dstIndex) {
        String json = JsonUtils.toJson(Map.of("source", Map.of("index", srcIndex), "dest", Map.of("index", dstIndex)));
        var response = ElasticHttpClient.post(identity, "/_reindex", json);
        logger.trace("Reindex [{}]", response.body());
        checkResponse(response);
    }

    public static void refreshIndex(String identity, String index) {
        var response = ElasticHttpClient.post(identity, "/" + index + "/_refresh", "");
        logger.trace("refresh index {}: [{}]", index, response.body());
        checkResponse(response);
    }

    public static <T> void updateMapping(String identity, String index, Class<T> clazz) {
        boolean annotationPresent = clazz.isAnnotationPresent(Index.class);
        if (!annotationPresent)
            throw new MicrofoxException("Class " + clazz.getName() + " must be annotated with @Index");

        Map<String, Object> map = MappingBuilder
                .forEntity(clazz)
                .shards(clazz.getDeclaredAnnotation(Index.class).shard())
                .replicas(clazz.getDeclaredAnnotation(Index.class).replica())
                .build();
        String mappingJson = JsonUtils.toJson(map);
        var response = ElasticHttpClient.put(identity, "/" + index + "/_mapping", mappingJson);
        logger.trace("updateMapping index {}: [{}]", index, response.body());
        checkResponse(response);
    }

    public static <T> void save(String identity, String index, String id, T document) {
        String json = JsonUtils.toJson(document);
        HttpResponse<String> response = ElasticHttpClient.put(identity, "/" + index + "/_doc/" + id, json);
        logger.trace("save index:{} document:[{}] response:[{}]", index, json, response.body());
        checkResponse(response);
    }

    public static <T> T get(String identity, String index, String id, Class<T> clazz) {
        var response = ElasticHttpClient.get(identity, "/" + index + "/_doc/" + id);
        logger.trace("get index:{} id:{} response:[{}]", index, id, response.body());
        checkResponse(response);
        var node = JsonUtils.readTree(response.body()).path("_source");
        return JsonUtils.treeToValue(node, clazz);
    }

    public static void delete(String identity, String index, String id) {
        HttpResponse<String> response = ElasticHttpClient.delete(identity, "/" + index + "/_doc/" + id);
        logger.trace("delete index:{} id:{} response:[{}]", index, id, response.body());
        checkResponse(response);
    }

    public static void deleteByQuery(String identity, String index, ElasticCriteria criteria) {
        HttpResponse<String> response = ElasticHttpClient.post(identity, "/" + index + "/_delete_by_query", criteria.toJson());
        logger.trace("deleteByQuery index:{} response:[{}]", index, response.body());
        checkResponse(response);
    }

    public static <T> List<T> search(String identity, String index, ElasticCriteria criteria, Class<T> clazz) {
        var response = ElasticHttpClient.post(identity, "/" + index + "/_search", criteria.toJson());
        logger.trace("search index:{} response:[{}]", index, response.body());
        checkResponse(response);
        List<T> results = new ArrayList<>();
        var root = JsonUtils.readTree(response.body());
        for (var hit : root.path("hits").path("hits")) {
            var source = hit.path("_source");
            results.add(JsonUtils.treeToValue(source, clazz));
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public static <T> void update(String identity, String index, String id, T document) {
        Map<String, Object> docMap = JsonUtils.convert(document, Map.class);
        String json = JsonUtils.toJson(Map.of("doc", docMap));
        var response = ElasticHttpClient.post(identity, "/" + index + "/_update/" + id, json);
        logger.trace("update index:{} response:[{}]", index, response.body());
        checkResponse(response);
    }

    public static <T> void bulk(String identity, String index, List<BulkOperation<T>> operations) {
        List<String> actions = new ArrayList<>();

        for (BulkOperation<T> op : operations) {
            switch (op.action()) {
                case INDEX -> {
                    actions.add("{\"index\":{\"_index\":\"" + index + "\",\"_id\":\"" + op.id() + "\"}}");
                    actions.add(JsonUtils.toJson(op.document()));
                }
                case SAVE -> {
                    actions.add("{\"index\":{\"_index\":\"" + index + "\"}}");
                    actions.add(JsonUtils.toJson(op.document()));
                }
                case UPDATE -> {
                    actions.add("{\"update\":{\"_index\":\"" + index + "\",\"_id\":\"" + op.id() + "\"}}");
                    actions.add(JsonUtils.toJson(Map.of("doc", JsonUtils.convert(op.document(), Map.class))));
                }
                case DELETE -> actions.add("{\"delete\":{\"_index\":\"" + index + "\",\"_id\":\"" + op.id() + "\"}}");
            }
        }

        String body = String.join("\n", actions) + "\n";
        var response = ElasticHttpClient.post(identity, "/_bulk", body);
        logger.trace("bulk index:{} response:[{}]", index, response.body());
        checkResponse(response);
    }

    private static void checkResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode > 300) {
            throw new MicrofoxException("Elastic operation failed, %s".formatted(response.body()));
        }
    }
}
