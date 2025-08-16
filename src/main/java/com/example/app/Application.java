package com.example.app;

import com.github.t1.htmljava.Attribute;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

import static com.github.t1.bulmajava.basic.Color.LINK;
import static com.github.t1.bulmajava.basic.Style.LIGHT;
import static com.github.t1.bulmajava.columns.Columns.columns;
import static com.github.t1.bulmajava.elements.Button.button;
import static com.github.t1.bulmajava.elements.Button.buttons;
import static com.github.t1.bulmajava.form.Field.field;
import static com.github.t1.bulmajava.form.Form.form;
import static com.github.t1.bulmajava.form.Input.input;
import static com.github.t1.bulmajava.form.InputType.EMAIL;
import static com.github.t1.bulmajava.form.InputType.NUMBER;
import static com.github.t1.bulmajava.form.InputType.TEXT;
import static com.github.t1.htmljava.HtmlBasics.div;

@Path("/")
public class Application {

    @Inject Page page;

    @GET
    public Response index() {return Response.seeOther(URI.create(SearchPage.PATH)).build();}

    @GET @Path("/validation")
    public Page validationPage() {
        return page.title("Validation Page").content(columns()
                .column(2, div())
                .column(form()
                        .attr("hx-post", "/form-submit")
                        .content(
                                field("Name").id("name")
                                        .content(input(TEXT).placeholder("Text input")
                                                .attr("hx-ext", "debug")
                                                .attr(validate("value !== ''", "valid name", "required"))),
                                field("Age").id("age")
                                        .content(input(NUMBER).placeholder("42")
                                                .attr("hx-ext", "debug")),
                                field("Username").id("username")
                                        .content(input(TEXT).placeholder("Text input")
                                                .attr(validate("value.length >= 3", "valid username", "len >= 3")))
                                        .iconLeft("user")
                                        .iconRight("check")
                                        .help("This username is available"),
                                field("Email").id("email")
                                        .content(input(EMAIL).placeholder("Email input")
                                                .attr(validate("value !== ''",
                                                        "We will send a verification message to this address", "This email is invalid")))
                                        .iconLeft("envelope")
                                        .iconRight("exclamation-triangle")
                                        .help("We will send a verification message to this address"),
                                field()
                                        .content(buttons().content(
                                                button("Submit").is(LINK),
                                                button("Cancel").is(LINK, LIGHT)))))
                .column(2, div()));
    }

    private Attribute validate(String validExpression, String validHelp, String invalidHelp) {
        return Attribute.of("onBlur", "validate(this, () => " + validExpression + ", '" + validHelp + "', '" + invalidHelp + "')");
    }
}
