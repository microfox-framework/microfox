package ir.moke.http;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Set;

@WebServlet("/*")
public class BaseServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        Set<RouteInfo> list = ResourceHolder.instance.list();
        String requestURI = req.getRequestURI();
        list.stream()
                .filter(item -> item.getPath().equals(requestURI))
                .findFirst()
                .ifPresent(routeInfo -> routeInfo.getRoute().handle(new Request(req), resp));
    }
}
