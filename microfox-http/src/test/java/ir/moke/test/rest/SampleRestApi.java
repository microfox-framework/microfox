package ir.moke.test.rest;

import ir.moke.microfox.api.http.annotation.*;
import ir.moke.test.UserDTO;

@Path("/api/v1")
public class SampleRestApi {

    @Path("/hello")
    @GET
    public static void sayHello(@QueryParam("name") String name) {
        System.out.println(name);
    }

    @Path("/person/{test}")
    @POST
    public static void person(@PostBody UserDTO dto, @PathParam("test") String test) {
        System.out.println(dto);
    }
}
