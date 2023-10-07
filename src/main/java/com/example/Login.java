package com.example;

import com.github.t1.bulmajava.elements.Button;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;

import static com.github.t1.bulmajava.basic.Color.LINK;
import static com.github.t1.bulmajava.basic.Color.PRIMARY;
import static com.github.t1.bulmajava.basic.Size.SMALL;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
@Slf4j
@SuppressWarnings("QsUndeclaredPathMimeTypesInspection")
public class Login {
    @Inject User user;
    @Inject HttpSession session;
    @Inject Connections connections;
    @Inject UriInfo uriInfo;

    @POST @Path("/login/{userId}")
    @Consumes(APPLICATION_JSON)
    public Response login(@PathParam("userId") String userId) {
        user.login(userId);
        log.info("login {}", user);
        var button = button(true);
        return broadcast(button);
    }

    @POST @Path("/logout")
    @Consumes(APPLICATION_JSON)
    public Response logout() {
        log.info("logout {}", user);
        user.logout();
        var button = button(false);
        return broadcast(button);
    }

    public Button button() {
        return button(user.isLoggedIn());
    }

    private Button button(boolean loggedIn) {
        return Button.button(loggedIn ? user.getName() : "Login")
                .id("login")
                .is(SMALL, loggedIn ? LINK : PRIMARY)
                .attr("hx-post", loggedIn ? "/logout" : "/login/jane")
                .attr("hx-swap", "outerHTML");
    }

    private Response broadcast(Button button) {
        var found = connections.broadcast(session.getId(), button);
        if (found) return Response.noContent().build();
        return Response.ok().header("HX-Trigger", "reload-page").build();
    }
}
