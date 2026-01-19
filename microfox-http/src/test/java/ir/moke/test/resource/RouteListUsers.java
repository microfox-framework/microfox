package ir.moke.test.resource;

import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.Route;
import ir.moke.test.UserDTO;

import java.util.List;

public class RouteListUsers implements Route {
    @Override
    public void handle(Request request, Response response) {
        UserDTO u1 = new UserDTO(1, "Mahdi");
        UserDTO u2 = new UserDTO(2, "ali");
        UserDTO u3 = new UserDTO(3, "sin");
        List<UserDTO> users = List.of(u1, u2, u3);
        response.body(users);
    }
}
