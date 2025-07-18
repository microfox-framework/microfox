package ir.moke.microfox.api.elastic;

public record BulkOperation<T>(BulkActionType action, String id, T document) {

    public static <T> BulkOperation<T> index(String id, T doc) {
        return new BulkOperation<>(BulkActionType.INDEX, id, doc);
    }

    public static <T> BulkOperation<T> save(T doc) {
        return new BulkOperation<>(BulkActionType.SAVE, null, doc);
    }

    public static <T> BulkOperation<T> update(String id, T doc) {
        return new BulkOperation<>(BulkActionType.UPDATE, id, doc);
    }

    public static <T> BulkOperation<T> delete(String id) {
        return new BulkOperation<>(BulkActionType.DELETE, id, null);
    }
}

