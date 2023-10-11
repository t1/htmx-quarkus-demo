package com.example.app;

import com.github.t1.bulmajava.basic.Attribute;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

import static com.github.t1.bulmajava.basic.Attribute.StringAttribute.stringAttribute;
import static com.github.t1.bulmajava.basic.Basic.div;
import static com.github.t1.bulmajava.basic.Basic.form;
import static com.github.t1.bulmajava.basic.Color.LINK;
import static com.github.t1.bulmajava.basic.Style.LIGHT;
import static com.github.t1.bulmajava.columns.Columns.columns;
import static com.github.t1.bulmajava.elements.Button.button;
import static com.github.t1.bulmajava.elements.Button.buttons;
import static com.github.t1.bulmajava.form.Field.field;
import static com.github.t1.bulmajava.form.Input.input;
import static com.github.t1.bulmajava.form.InputType.EMAIL;
import static com.github.t1.bulmajava.form.InputType.TEXT;

@Path("/")
@SuppressWarnings("QsUndeclaredPathMimeTypesInspection")
public class Application {

    @Inject Page page;

    @GET
    public Response index() {return Response.seeOther(URI.create(SearchPage.PATH)).build();}

    @GET @Path("/other")
    public Page otherPage() {
        return page.title("Other Page").content(columns()
                .column(2, div())
                .column(form()
                        .attr("hx-post", "/form-submit")
                        .content(
                                field().id("name").label("Name")
                                        .control(input(TEXT).placeholder("Text input")
                                                .attr("hx-ext", "debug")
                                                .attr(validate("value !== ''", "valid Help", "required"))),
                                field().id("username").label("Username")
                                        .control(input(TEXT).placeholder("Text input")
                                                .attr(validate("value.length >= 3", "valid Help", "len >= 3")))
                                        .iconLeft("user")
                                        .iconRight("check")
                                        .help("This username is available"),
                                field().id("email").label("Email")
                                        .control(input(EMAIL).placeholder("Email input")
                                                .attr(validate("value !== ''",
                                                        "We will send a verification message to this address", "This email is invalid")))
                                        .iconLeft("envelope")
                                        .iconRight("exclamation-triangle")
                                        .help("We will send a verification message to this address"),
                                field()
                                        .control(buttons().content(
                                                button("Submit").is(LINK),
                                                button("Cancel").is(LINK, LIGHT)))))
                .column(2, div()));
    }

    private Attribute validate(String validExpression, String validHelp, String invalidHelp) {
        return stringAttribute("onBlur", "validate(this, () => " + validExpression + ", '" + validHelp + "', '" + invalidHelp + "')");
    }
}
