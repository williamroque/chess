package ui;

import model.AuthData;

public class AuthState {
    private AuthData currentAuth;

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
