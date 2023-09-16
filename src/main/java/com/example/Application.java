package com.example;

import com.github.t1.bulmajava.basic.Color;
import com.github.t1.bulmajava.basic.Element;
import com.github.t1.bulmajava.elements.Button;
import com.github.t1.bulmajava.form.Field;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.Data;

import java.net.URI;

import static com.github.t1.bulmajava.basic.Basic.p;
import static com.github.t1.bulmajava.basic.Color.*;
import static com.github.t1.bulmajava.basic.Size.SMALL;
import static com.github.t1.bulmajava.basic.Style.LIGHT;
import static com.github.t1.bulmajava.elements.Button.button;
import static com.github.t1.bulmajava.form.Field.field;
import static com.github.t1.bulmajava.form.Input.input;
import static com.github.t1.bulmajava.form.InputType.TEXT;
import static com.github.t1.bulmajava.helpers.ColorsHelper.dark;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
@SuppressWarnings("QsUndeclaredPathMimeTypesInspection")
public class Application {

    @Inject Page page;

    @GET
    public Response goToSearch() {
        return Response.seeOther(URI.create("/search")).build();
    }

    @GET @Path("/search")
    public Page searchPage() {
        return page.title("Hello Bulma-Java & HTMX").content(searchField());
    }

    @GET @Path("/other")
    public Page otherPage() {
        return page.title("Other Page");
    }

    private static Field searchField() {
        return field().label("Search")
                .iconRight("search")
                .control(input(TEXT).name("searchString")
                        .attr("hx-post", "/search")
                        .attr("hx-trigger", "keyup changed delay:500ms")
                        .attr("hx-target", ".help")
                        .attr("hx-swap", "outerHTML")
                        .placeholder("Search..."))
                .help("nothing searched, yet...", dark(LIGHT));
    }

    @POST @Path("/search")
    @Consumes(APPLICATION_JSON)
    public Element search(@Valid SearchForm form) {
        return form.searchString.contains("xxx") ?
                getHelp("not found: " + form.searchString, DANGER) :
                getHelp("found: " + form.searchString, SUCCESS);
    }

    @RequestScoped @Data
    public static class SearchForm {
        @NotNull String searchString;
    }

    private static Element getHelp(String text, Color color) {
        return p(text).classes("help").is(color);
    }

    @POST @Path("/login")
    @Consumes(APPLICATION_JSON)
    public Button login() {
        return button("Rüdiger").is(SMALL, LINK);
    }
}
