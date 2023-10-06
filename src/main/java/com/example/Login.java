package com.example;

import com.github.t1.bulmajava.elements.Button;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;
import java.util.UUID;

import static com.github.t1.bulmajava.basic.Color.LINK;
import static com.github.t1.bulmajava.basic.Color.PRIMARY;
import static com.github.t1.bulmajava.basic.Size.SMALL;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
@SuppressWarnings("QsUndeclaredPathMimeTypesInspection")
public class Login implements Serializable {
    private static final String COOKIE_NAME = "session";

    @Inject Sessions sessions;

    @Inject HttpHeaders headers;

    @POST @Path("/login")
    @Consumes(APPLICATION_JSON)
    public Response login() {
        var response = Response.ok(button(true));
        if (!isLoggedIn()) response.cookie(newSessionCookie());
        return response.build();
    }

    @POST @Path("/logout")
    @Consumes(APPLICATION_JSON)
    public Response logout() {
        if (isLoggedIn()) sessions.remove(sessionUuid());
        return Response.ok(button(false)).cookie(removeSessionCookie()).build();
    }

    private boolean isLoggedIn() {return sessions.isActive(sessionUuid());}

    private UUID sessionUuid() {
        var sessionCookie = headers.getCookies().get(COOKIE_NAME);
        return sessionCookie == null ? null : UUID.fromString(sessionCookie.getValue());
    }

    private NewCookie newSessionCookie() {
        return new NewCookie.Builder(COOKIE_NAME).value(sessions.create().toString()).build();
    }

    private NewCookie removeSessionCookie() {
        return new NewCookie.Builder(COOKIE_NAME).maxAge(0).build();
    }

    public Button button() {return button(isLoggedIn());}

    private Button button(boolean loggedIn) {
        return Button.button(loggedIn ? "Logout" : "Login")
                .is(SMALL, loggedIn ? LINK : PRIMARY)
                .attr("hx-post", loggedIn ? "/logout" : "/login")
                .attr("hx-swap", "outerHTML");
    }
}
