<p align="center">
  <img src="assets/microfox.png" alt="JOS" width="300"/>
</p>

# MicroFox

MicroFox is a lightweight, developer-friendly **framework** designed to simplify the setup and management of
microservice-based projects.    
With a focus on ease of use and minimal configuration, MicroFox helps teams get started quickly, offering a smooth path
from development to deployment.    
Whether you're building a small service or scaling up a complex system, MicroFox keeps things simple and efficient.

### Dependency :

```xml

<dependency>
    <groupId>ir.moke</groupId>
    <artifactId>microfox</artifactId>
    <version>0.2</version>
</dependency>
```

### Usage :

```java
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

    private interface BookMapper {
        @InsertProvider(type = BookProvider.class, method = "insert")
        @SelectKey(statement = "SELECT book_seq.NEXTVAL FROM dual", keyProperty = "id", before = true, resultType = Long.class)
        void save(Book book);

        @Select("select * from book")
        List<Address> findAll();
    }

    public static void main(String[] args) {

        /* Easy Implement Rest API */
        httpFilter("/book/add", ((request, response) -> {/*...*/}));
        httpPost("/book/add", (req, resp) -> {/*...*/});
        httpGet("/book/findAll", (request, response) -> {/*...*/});
        httpDelete("/book/remove?id=12", (request, response) -> {/*...*/});
        httpGet("/api/:name/:age", (request, response) -> {/*...*/});
        httpDelete("/redirect", (request, response) -> response.redirect("/book/find"));

        /* Easy call rest api */
        httpCall("http://w.x.y.z:8080/book", BookService.class, bookService -> {/*...*/});

        /* Easy setup job */
        job(EchoJob.class, "*/3 * * * * ? *");
    }
}
```

### Implementations :

| **Feature**      | **Technology**                        |
|------------------|---------------------------------------|
| Rest API         | Apache Tomcat                         |
| Job Scheduler    | Quartz                                |
| SQL Framework    | MyBatis                               |
| FTP Client       | Apache Commons-net                    |
| RestClient       | Kafir Project (Pure Java Http Client) |
| OpenAPI / Web UI | Swagger annotations / RapiDoc         |    

### Example Project: 
https://github.com/microfox-framework/Microfox-Example 

<p align="center">
  <img src="assets/RapiDoc.png" alt="JOS" width="800"/>
</p>