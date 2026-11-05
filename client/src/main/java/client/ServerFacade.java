package client;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseURL;

    private HttpRequest.BodyPublisher requestBodyPublisher(String body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(body);
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private URI buildURI(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return URI.create(baseURL).resolve(path);
    }

    private <T> T sendRequest(String method, String path, String body, String[] headers, Class<T> clazz) throws IOException, InterruptedException {
        HttpRequest.Builder requestDraft = HttpRequest.newBuilder()
                .uri(buildURI(path))
                .method(method, requestBodyPublisher(body));

        if (headers != null) {
            requestDraft.headers(headers);
        }

        HttpRequest request = requestDraft.build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        int statusCode = response.statusCode();

        if (statusCode < 200 || statusCode >= 300) {
            throw new GenericHttpErrorException(statusCode);
        }

        return new Gson().fromJson(response.body(), clazz);
    }

    public ServerFacade(String baseURL) {
        if (!baseURL.endsWith("/")) {
            baseURL += "/";
        }
        this.baseURL = baseURL;
    }
}
