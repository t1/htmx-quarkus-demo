package com.example;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/")
@SuppressWarnings("QsUndeclaredPathMimeTypesInspection")
public class Application {

    @Inject Page page;

    @GET
    public Response index() {return Response.seeOther(URI.create(SearchPage.PATH)).build();}

    @GET @Path("/other")
    public Page otherPage() {return page.title("Other Page");}
}
