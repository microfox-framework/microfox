<p align="center">
  <img src="microfox.png" alt="JOS" width="300"/>
</p>

# Microfox

Microfox is a lightweight, developer-friendly toolkit designed to simplify the setup and management of
microservice-based projects.    
With a focus on ease of use and minimal configuration, Microfox helps teams get started quickly, offering a smooth path
from development to deployment.    
Whether you're building a small service or scaling up a complex system, Microfox keeps things simple and efficient.

dependency:

```xml

<dependency>
    <groupId>ir.moke.microfox</groupId>
    <artifactId>microfox</artifactId>
    <version>0.1</version>
</dependency>
```

Usage:

```java
import ir.moke.kafir.annotation.GET;
import org.junit.jupiter.api.Test;
import ir.moke.microfox.MicroFoxServer;

import java.net.http.HttpResponse;

import static ir.moke.microfox.MicroFox.*;

public class MicroFoxTest {

    private interface BookService {
        @GET("/book/find")
        HttpResponse<String> findBooks();
    }

    public static void main(String[] args) {

        /* Easy Implement Rest API */
        filter("/book/add", ((request, response) -> {/*...*/}));
        post("/book/add", (req, resp) -> {/*...*/});
        get("/book/find", (request, response) -> {/*...*/});
        delete("/book/remove", (request, response) -> {/*...*/});
        get("/api/:name/:age", (request, response) -> {/*...*/});
        delete("/redirect", (request, response) -> response.redirect("/book/find"));

        /* Easy call rest api */
        BookService bookService = restCall("http://w.x.y.z:8080/book/find", BookService.class);
        bookService.findBooks();

        MicroFoxServer.start();
    }
}
```