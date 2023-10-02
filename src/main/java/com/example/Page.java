package com.example;

import com.github.t1.bulmajava.basic.*;
import com.github.t1.bulmajava.components.Navbar;
import com.github.t1.bulmajava.elements.Title;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriInfo;

import static com.github.t1.bulmajava.basic.Anchor.a;
import static com.github.t1.bulmajava.basic.Basic.*;
import static com.github.t1.bulmajava.basic.Body.body;
import static com.github.t1.bulmajava.basic.Color.SUCCESS;
import static com.github.t1.bulmajava.basic.Color.WARNING;
import static com.github.t1.bulmajava.basic.Html.html;
import static com.github.t1.bulmajava.basic.Size.SMALL;
import static com.github.t1.bulmajava.basic.State.ACTIVE;
import static com.github.t1.bulmajava.basic.Style.WHITE;
import static com.github.t1.bulmajava.components.Tabs.tabs;
import static com.github.t1.bulmajava.elements.Button.button;
import static com.github.t1.bulmajava.elements.Button.buttons;
import static com.github.t1.bulmajava.elements.Icon.icon;
import static com.github.t1.bulmajava.helpers.ColorsHelper.dark;
import static com.github.t1.bulmajava.layout.Container.container;
import static com.github.t1.bulmajava.layout.Section.section;

@RequestScoped
public class Page implements Renderable {
    @Inject UriInfo uriInfo;

    private Html html;

    public Page title(String title) {
        this.html = html(title)
                .stylesheet("/webjars/fortawesome__fontawesome-free/css/all.css")
                .stylesheet("/webjars/bulma/css/bulma.css")
                .script("/webjars/htmx.org/dist/htmx.min.js")
                .script("json-enc.js")
                .content(body().hasNavbarFixedTop().content(
                        container().attr("hx-ext", "json-enc").content(section().classes("mt-6")
                                .content(navbar(), Title.title(title)))));
        return this;
    }

    private Navbar navbar() {
        return Navbar.navbar("the-navbar").classes("is-fixed-top", "px-5", "has-shadow")
                .hasBackground(dark(SUCCESS))
                .start(tabs().content(
                        tab("Search", SearchPage.PATH, "search"),
                        tab("Other", "/other", "wrench")))
                .end(div().content(buttons().content(
                        button("Log in").is(SMALL, WARNING)
                                .attr("hx-trigger", "load delay:1s")
                                .attr("hx-post", "/login")
                                .attr("hx-swap", "outerHTML"))));
    }

    private Element tab(String text, String href, String icon) {
        var a = a().hasText(WHITE);
        if (icon != null) a = a.content(icon(icon).is(SMALL).ariaHidden(true));
        var item = li().content(a.content(span(text)).href(href));
        if (href.equals(uriInfo.getPath())) item = item.is(ACTIVE);
        return item;
    }

    public Page content(Renderable... content) {
        this.html = html.body((AbstractElement<?> body) ->
                body.content("container", container ->
                        container.content("section", section ->
                                section.content(content))));
        return this;
    }


    @Override public void render(Renderer renderer) {html.render(renderer);}
}
