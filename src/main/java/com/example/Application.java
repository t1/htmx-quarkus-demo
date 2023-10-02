package com.example;

import com.github.t1.bulmajava.elements.Button;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

import static com.github.t1.bulmajava.basic.Color.LINK;
import static com.github.t1.bulmajava.basic.Size.SMALL;
import static com.github.t1.bulmajava.elements.Button.button;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
@SuppressWarnings("QsUndeclaredPathMimeTypesInspection")
public class Application {

    @Inject Page page;

    @GET
    public Response index() {return Response.seeOther(URI.create(SearchPage.PATH)).build();}

    @GET @Path("/other")
    public Page otherPage() {return page.title("Other Page");}

    @POST @Path("/login")
    @Consumes(APPLICATION_JSON)
    public Button login() {return button("Logout").is(SMALL, LINK);}
}
