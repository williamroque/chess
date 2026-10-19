package handler;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.InvalidCredentialsException;
import io.javalin.http.Context;
import model.UserData;
import server.InvalidRequestException;
import server.Server;
import service.UserService;
import service.results.LoginResult;

public class UserHandler {
    UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        UserData userData;

        try {
            userData = Server.getBodyObject(ctx, UserData.class);
        } catch (InvalidRequestException _) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: bad request\" }");

            return;
        }

        try {
            LoginResult result = userService.register(userData);
            ctx.status(200);
            ctx.json(result);
        } catch(InvalidCredentialsException _) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: bad request\" }");
        } catch (AlreadyTakenException _) {
            ctx.status(403);
            ctx.json("{ \"message\": \"Error: already taken\" }");
        } catch (Exception e) {
            System.err.println(e);
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: server error\" }");
        }
    }
}
