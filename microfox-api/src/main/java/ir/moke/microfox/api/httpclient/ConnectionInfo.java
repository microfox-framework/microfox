package ir.moke.microfox.api.httpclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

public record ConnectionInfo(URI uri,
                             Duration tcpTimeout,
                             Duration requestTimeout,
                             HttpClient.Version version) {
}
