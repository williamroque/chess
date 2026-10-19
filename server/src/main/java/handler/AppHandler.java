package handler;

import io.javalin.http.Context;
import service.AppService;
import service.UserService;

public class AppHandler extends Handler {
    AppService appService;

    public AppHandler(AppService appService, UserService userService) {
        super(userService);
        this.appService = appService;
    }

    public void clear(Context ctx) {
        try {
            appService.clear();
            ctx.status(200);
            ctx.json("{}");
        } catch (Exception e) {
            System.err.println(e);
            ctx.status(500);
            ctx.json("{ \"message\": \"An error occurred in the server.\" }");
        }
    }
}
