package ir.moke.microfox.discovery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterDTO(@JsonProperty("Id") String id,
                          @JsonProperty("Name") String name,
                          @JsonProperty("Address") String address,
                          @JsonProperty("Port") int port,
                          @JsonProperty("Check") CheckDTO check) {
}
