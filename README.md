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
import org.quartz.Job;

import java.net.http.HttpResponse;

import static ir.moke.microfox.MicroFox.*;

public class MicroFoxTest {

    private interface BookService {
        @GET("/book/find")
        HttpResponse<String> findBooks();
    }

    private class EchoJob implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("Job executed");
        }
    }

    public static void main(String[] args) {

        /* Easy Implement Rest API */
        filter("/book/add", ((request, response) -> {/*...*/}));
        post("/book/add", (req, resp) -> {/*...*/});
        get("/book/findAll", (request, response) -> {/*...*/});
        delete("/book/remove?id=12", (request, response) -> {/*...*/});
        get("/api/:name/:age", (request, response) -> {/*...*/});
        delete("/redirect", (request, response) -> response.redirect("/book/find"));

        /* Easy call rest api */
        BookService bookService = restCall("http://w.x.y.z:8080/book/find", BookService.class);
        bookService.findBooks();

        /* Easy setup job */
        job(EchoJob.class,"*/3 * * * * ? *");

        MicroFoxServer.start();
    }
}
```