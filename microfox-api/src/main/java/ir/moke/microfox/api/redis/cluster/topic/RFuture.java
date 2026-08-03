package ir.moke.microfox.api.redis.cluster.topic;

import java.util.concurrent.CompletionStage;

public interface RFuture<V> extends java.util.concurrent.Future<V>, CompletionStage<V> {

}
