package handler;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.InvalidCredentialsException;
import io.javalin.http.Context;
import model.UserData;
import server.InvalidRequestException;
import server.Server;
import service.UserService;
import service.results.LoginResult;

public class UserHandler extends Handler {
    public UserHandler(UserService userService) {
        super(userService);
    }

    public void register(Context ctx) {
        UserData userData;

        try {
            userData = getBodyObject(ctx, UserData.class);

            if (userData.username() == null || userData.password() == null || userData.email() == null) {
                throw new InvalidRequestException("Bad request.");
            }
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

    public void login(Context ctx) {
        UserData userData;

        try {
            userData = getBodyObject(ctx, UserData.class);

            if (userData.username() == null || userData.password() == null) {
                throw new InvalidRequestException("Bad request.");
            }
        } catch (InvalidRequestException _) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: bad request\" }");

            return;
        }

        try {
            LoginResult result = userService.login(userData);
            ctx.status(200);
            ctx.json(result);
        } catch(InvalidCredentialsException _) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: unauthorized\" }");
        } catch (Exception e) {
            System.err.println(e);
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: server error\" }");
        }
    }

    public void logout(Context ctx) {
        String authToken = ctx.header("authorization");

        try {
            userService.logout(authToken);
            ctx.status(200);
            ctx.json("{}");
        } catch(AuthTokenNotFoundException _) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: unauthorized\" }");
        } catch (Exception e) {
            System.err.println(e);
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: server error\" }");
        }
    }
}
