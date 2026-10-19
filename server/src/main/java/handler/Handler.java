package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import server.InvalidRequestException;
import service.UserService;

public class Handler {
    UserService userService;

    public Handler(UserService userService) {
        this.userService = userService;
    }

//   protected boolean authorized(Context ctx) {
//        String authToken = ctx.header("Authorization");
//        if (!validTokens.contains(authToken)) {
//            ctx.contentType("application/json");
//            ctx.status(401);
//            ctx.result(new Gson().toJson(Map.of("msg", "invalid authorization")));
//            return false;
//        }
//        return true;
//    }

    protected static <T> T getBodyObject(Context context, Class<T> clazz) {
        var bodyObject = new Gson().fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new InvalidRequestException("Missing required body.");
        }

        return bodyObject;
    }
}
