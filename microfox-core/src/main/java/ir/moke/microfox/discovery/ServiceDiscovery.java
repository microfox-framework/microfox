package ir.moke.microfox.discovery;

import ir.moke.kafir.annotation.GET;
import ir.moke.kafir.annotation.PUT;
import ir.moke.kafir.annotation.PathParameter;
import ir.moke.microfox.discovery.dto.RegisterDTO;

import java.net.http.HttpResponse;

public interface ServiceDiscovery {

    @PUT("/v1/agent/service/register")
    HttpResponse<String> register(RegisterDTO register);

    @PUT("/v1/agent/service/deregister/{id}")
    HttpResponse<String> unregister(@PathParameter("id") String id);

    @GET("/v1/catalog/service/{name}")
    HttpResponse<String> discover(@PathParameter("name") String name);
}
