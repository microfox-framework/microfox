<p align="center">
  <img src="microfox.png" alt="JOS" width="300"/>
</p>

# Microfox
Microfox is a lightweight, developer-friendly toolkit designed to simplify the setup and management of microservice-based projects. 
With a focus on ease of use and minimal configuration, Microfox helps teams get started quickly, offering a smooth path from development to deployment.
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
public class MicroFoxTest {
    public static void main(String[] args) {
        filter("/book/add", ((request, response) -> System.out.println("I'm Filter")));
        post("/book/add", (req, resp) -> {
            Book body = req.body(Book.class);
            BookService.instance.add(body);
            resp.body(body);
        });

        get("/book/find", (request, response) -> response.body(BookService.instance.find()));
        delete("/book/remove", (request, response) -> BookService.instance.removeAll());

        MicroFoxServer.start();
    }
}
```