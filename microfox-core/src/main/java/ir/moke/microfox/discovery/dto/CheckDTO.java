package ir.moke.microfox.discovery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CheckDTO(@JsonProperty("HTTP") String http,
                       @JsonProperty("Interval") String interval) {
}
