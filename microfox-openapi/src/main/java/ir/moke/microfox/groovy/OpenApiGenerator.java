package ir.moke.microfox.groovy;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariables;
import ir.moke.microfox.MicroFoxEnvironment;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.http.ResourceHolder;
import ir.moke.microfox.http.RouteInfo;
import ir.moke.utils.JsonUtils;

import java.lang.reflect.Method;
import java.util.*;

public class OpenApiGenerator {

    static {
        ModelConverters.getInstance().addConverter(new ModelResolver(JsonUtils.getObjectMapper()));
    }

    public static OpenAPI generate() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.info(new Info()
                .title(MicroFoxEnvironment.getEnv("microfox.open.api.title"))
                .description(MicroFoxEnvironment.getEnv("microfox.open.api.description")));
        Paths paths = new Paths();


        for (RouteInfo routeInfo : ResourceHolder.listRoutes()) {
            PathItem item = paths.computeIfAbsent(routeInfo.path(), k -> new PathItem());

            Operation op = extractSwaggerAnnotation(routeInfo.route());

            switch (routeInfo.method()) {
                case GET -> item.get(op);
                case POST -> item.post(op);
                case PUT -> item.put(op);
                case DELETE -> item.delete(op);
                case PATCH -> item.patch(op);
                case OPTIONS -> item.options(op);
                case HEAD -> item.head(op);
                case TRACE -> item.trace(op);
            }

            paths.addPathItem(routeInfo.path(), item);
        }

        openAPI.setPaths(paths);
        return openAPI;
    }

    private static Operation extractSwaggerAnnotation(Route route) {
        try {
            Method handle = route.getClass().getDeclaredMethod("handle", Request.class, Response.class);
            Operation operation = new Operation();
            if (handle.isAnnotationPresent(io.swagger.v3.oas.annotations.Operation.class)) {
                io.swagger.v3.oas.annotations.Operation operationAnnotation = handle.getDeclaredAnnotation(io.swagger.v3.oas.annotations.Operation.class);
                operation.setSummary(operationAnnotation.summary());
                operation.setOperationId(operationAnnotation.operationId());
                operation.setDeprecated(operationAnnotation.deprecated());
                operation.setDescription(operationAnnotation.description());
                operation.setExtensions(convertExtensions(operationAnnotation.extensions()));
                operation.setExternalDocs(convertExternalDocs(operationAnnotation.externalDocs()));
                operation.setRequestBody(convertRequestBody(operationAnnotation.requestBody()));
                operation.setParameters(convertParameters(operationAnnotation.parameters()));
                operation.setSecurity(convertSecurityRequirement(operationAnnotation.security()));
                operation.setTags(convertTags(operationAnnotation.tags()));
                operation.setServers(convertServers(operationAnnotation.servers()));
                operation.setResponses(convertApiResponses(operationAnnotation.responses()));
            }
            return operation;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static ApiResponses convertApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse[] responses) {
        ApiResponses apiResponses = new ApiResponses();
        if (responses == null) return apiResponses;

        for (io.swagger.v3.oas.annotations.responses.ApiResponse response : responses) {
            ApiResponse resp = new ApiResponse();
            resp.setDescription(response.description());

            // Convert headers
            if (response.headers() != null && response.headers().length > 0) {
                Map<String, Header> headersMap = getStringHeaderMap(response);
                resp.setHeaders(headersMap);
            }

            // Convert content
            if (response.content() != null && response.content().length > 0) {
                Content content = getContent(response.content());
                resp.setContent(content);
            }

            apiResponses.addApiResponse(response.responseCode(), resp);
        }


        return apiResponses;
    }

    private static Map<String, Header> getStringHeaderMap(io.swagger.v3.oas.annotations.responses.ApiResponse response) {
        Map<String, Header> headersMap = new LinkedHashMap<>();
        for (io.swagger.v3.oas.annotations.headers.Header header : response.headers()) {
            Header h = new Header();
            h.setDescription(header.description());
            if (!header.schema().implementation().equals(Void.class)) {
                io.swagger.v3.oas.models.media.Schema<?> schema = new io.swagger.v3.oas.models.media.Schema<>();
                schema.setType(header.schema().type());
                schema.setFormat(header.schema().format());
                h.setSchema(schema);
            }
            headersMap.put(header.name(), h);
        }
        return headersMap;
    }

    private static Content getContent(io.swagger.v3.oas.annotations.media.Content[] contents) {
        Content content = new Content();
        for (io.swagger.v3.oas.annotations.media.Content c : contents) {
            MediaType mediaType = new MediaType();
            io.swagger.v3.oas.models.media.Schema<?> schema;
            Class<?> implClass = c.schema().implementation();

            if (implClass != Void.class) {
                ResolvedSchema resolvedSchema = ModelConverters.getInstance().readAllAsResolvedSchema(implClass);
                schema = resolvedSchema.schema;
                mediaType.setSchema(schema);
            }

            // Handle examples: either set a simple example or multiple named examples
            if (c.examples().length == 1) {
                mediaType.setExample(c.examples()[0].value());
            } else if (c.examples().length > 1) {
                Map<String, Example> examplesMap = new LinkedHashMap<>();
                for (ExampleObject ex : c.examples()) {
                    Example example = new Example();
                    example.setSummary(ex.summary());
                    example.setDescription(ex.description());
                    example.setValue(ex.value());
                    examplesMap.put(ex.name().isEmpty() ? UUID.randomUUID().toString() : ex.name(), example);
                }
                mediaType.setExamples(examplesMap);
            }

            content.addMediaType(c.mediaType(), mediaType);
        }
        return content;
    }

    private static List<Server> convertServers(io.swagger.v3.oas.annotations.servers.Server[] serverAnnotations) {
        List<Server> servers = new ArrayList<>();
        for (io.swagger.v3.oas.annotations.servers.Server serverAnnotation : serverAnnotations) {
            Server server = new Server();
            server.setDescription(serverAnnotation.description());
            server.setUrl(serverAnnotation.url());

            Map<String, Object> extentionsMap = new HashMap<>();
            for (Extension extension : serverAnnotation.extensions()) {
                extentionsMap.put(extension.name(), extension.properties());
            }
            server.setExtensions(extentionsMap);

            ServerVariables variables = new ServerVariables();
            for (ServerVariable variable : serverAnnotation.variables()) {
                io.swagger.v3.oas.models.servers.ServerVariable serverVariable = new io.swagger.v3.oas.models.servers.ServerVariable();
                serverVariable.setDescription(variable.description());
                serverVariable.setExtensions(convertExtensions(variable.extensions()));
                variables.addServerVariable(variable.name(), serverVariable);
            }
            server.setVariables(variables);

            servers.add(server);
        }

        return servers;
    }

    private static List<String> convertTags(String[] tags) {
        return Arrays.stream(tags).toList();
    }

    private static List<SecurityRequirement> convertSecurityRequirement(io.swagger.v3.oas.annotations.security.SecurityRequirement[] security) {
        List<SecurityRequirement> result = new ArrayList<>();

        for (io.swagger.v3.oas.annotations.security.SecurityRequirement annotation : security) {
            SecurityRequirement model = new SecurityRequirement();
            model.addList(annotation.name(), Arrays.asList(annotation.scopes()));
            result.add(model);
        }

        return result;
    }


    private static Map<String, Object> convertExtensions(Extension[] extensions) {
        Map<String, Object> result = new HashMap<>();
        for (Extension ext : extensions) {
            String name = ext.name();
            Map<String, String> properties = new HashMap<>();
            for (ExtensionProperty prop : ext.properties()) {
                properties.put(prop.name(), prop.value());
            }
            result.put(name, properties);
        }
        return result;
    }

    private static ExternalDocumentation convertExternalDocs(io.swagger.v3.oas.annotations.ExternalDocumentation annotation) {
        if (annotation == null || (annotation.description().isEmpty() && annotation.url().isEmpty())) {
            return null;
        }

        ExternalDocumentation model = new ExternalDocumentation();
        model.setDescription(annotation.description());
        model.setUrl(annotation.url());
        return model;
    }

    private static RequestBody convertRequestBody(io.swagger.v3.oas.annotations.parameters.RequestBody requestBodyAnnotation) {
        if (requestBodyAnnotation == null || (requestBodyAnnotation.content().length == 0 && requestBodyAnnotation.description().isEmpty())) {
            return null;
        }

        RequestBody model = new RequestBody();
        model.setDescription(requestBodyAnnotation.description());
        model.setRequired(requestBodyAnnotation.required());

        Content content = getContent(requestBodyAnnotation.content());
        model.setContent(content);
        return model;
    }

    private static List<Parameter> convertParameters(io.swagger.v3.oas.annotations.Parameter[] parameterAnnotations) {
        List<Parameter> parameters = new ArrayList<>();

        for (io.swagger.v3.oas.annotations.Parameter annotation : parameterAnnotations) {
            Parameter model = new Parameter();

            model.setName(annotation.name());
            model.setIn(annotation.in().toString().toLowerCase()); // query, header, path, cookie
            model.setDescription(annotation.description());
            model.setRequired(annotation.required());
            model.setDeprecated(annotation.deprecated());
            model.setAllowEmptyValue(annotation.allowEmptyValue());

            Schema schema = annotation.schema();
            if (!schema.type().isEmpty()) {
                io.swagger.v3.oas.models.media.Schema<?> swaggerSchema = new StringSchema();
                swaggerSchema = switch (schema.type()) {
                    case "string" -> new StringSchema();
                    case "integer" -> new IntegerSchema();
                    case "number" -> new NumberSchema();
                    case "boolean" -> new BooleanSchema();
                    default -> swaggerSchema;
                };
                model.setSchema(swaggerSchema);
            }

            parameters.add(model);
        }

        return parameters;
    }
}
