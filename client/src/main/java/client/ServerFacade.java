package client;

import client.exceptions.FacadeException;
import client.exceptions.GenericHttpErrorException;
import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

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

    public void routeHttpError(int statusCode, Map<Integer, String> messages) throws FacadeException {
        if (messages.containsKey(statusCode)) {
            throw new FacadeException(messages.get(statusCode), statusCode);
        }
        throw new FacadeException("There was an issue communicating with the server.", statusCode);
    }

    private <T> T sendRequest(
            String method,
            String path,
            String body,
            String[] headers,
            Class<T> clazz,
            Map<Integer, String> errorMessages
    ) throws FacadeException {
        HttpRequest.Builder requestDraft = HttpRequest.newBuilder()
                .uri(buildURI(path))
                .method(method, requestBodyPublisher(body));

        if (headers != null) {
            requestDraft.headers(headers);
        }

        HttpRequest request = requestDraft.build();
        HttpResponse<String> response;

        try {
            response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (Exception e) {
            throw new FacadeException("There was an issue connecting with the server.", 500);
        }

        int statusCode = response.statusCode();

        if (statusCode < 200 || statusCode >= 300) {
            routeHttpError(statusCode, errorMessages);
        }

        return clazz == null ? null : new Gson().fromJson(response.body(), clazz);
    }

    public void clear() throws FacadeException {
        sendRequest(
                "DELETE",
                "/db",
                null,
                null,
                null,
                Map.of(
                        500, "There was an internal server error while trying to clear the database."
                )
        );
    }

    public AuthData register(UserData data) throws FacadeException {
        return sendRequest(
                "POST",
                "/user",
                new Gson().toJson(data),
                null,
                AuthData.class,
                Map.of(
                        400, "Invalid registration details.",
                        403, "This username is already taken!",
                        500, "There was an internal server error while trying to register."
                )
        );
    }

    public ServerFacade(String baseURL) {
        if (!baseURL.endsWith("/")) {
            baseURL += "/";
        }
        this.baseURL = baseURL;
    }
}
