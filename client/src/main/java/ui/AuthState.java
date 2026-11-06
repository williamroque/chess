package ui;

import model.AuthData;

public class AuthState {
    AuthData currentAuth;

    public AuthState() {}

    public AuthData getAuth() {
        return currentAuth;
    }

    public void setAuth(AuthData auth) {
        currentAuth = auth;
    }

    public void clearAuth() {
        currentAuth = null;
    }
}
