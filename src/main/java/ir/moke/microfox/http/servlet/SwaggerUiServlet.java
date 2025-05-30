package ir.moke.microfox.http.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/docs")
public class SwaggerUiServlet extends HttpServlet {
    private static final String HTML = """
            <!DOCTYPE html>
            <html>
              <head>
                <title>RapiDoc</title>
                <meta charset="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <!-- Load RapiDoc web component -->
                <script type="module" src="https://unpkg.com/rapidoc/dist/rapidoc-min.js"></script>
                <style>
                  body {
                    margin: 0;
                    padding: 0;
                    font-family: 'Roboto', 'Montserrat', sans-serif;
                  }
                  rapi-doc::part(section-servers) { /* <<< targets the server div */
                    background: #6b5b95;
                    color:#d1c2e4;
                    margin:0 24px 0 24px;
                    border-radius: 5px;
                  }
                  rapi-doc::part(label-selected-server) { /* <<< targets selected server label */
                    color: #fff;
                  }
                </style>
              </head>
              <body>
                <rapi-doc
                  spec-url="/openapi.json"
                  render-style="read"
                  allow-try="true"
                  show-header="true"
                  show-code-sample="true"
                  code-sample-language="curl"
                  nav-bg-color = "#423368"
                  primary-color ="#df75c4"
                  bg-color = "#fae4f5"
                ></rapi-doc>
              </body>
            </html>
            """;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().write(HTML);
    }
}
