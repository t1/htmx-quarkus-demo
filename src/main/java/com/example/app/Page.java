package com.example.app;

import com.github.t1.bulmajava.components.Navbar;
import com.github.t1.bulmajava.elements.Title;
import com.github.t1.bulmajava.layout.Section;
import com.github.t1.htmljava.Element;
import com.github.t1.htmljava.Html;
import com.github.t1.htmljava.Renderable;
import com.github.t1.htmljava.Renderer;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static com.github.t1.bulmajava.basic.BulmaElement.TextModifier.text;
import static com.github.t1.bulmajava.basic.Color.SUCCESS;
import static com.github.t1.bulmajava.basic.Size.SMALL;
import static com.github.t1.bulmajava.basic.State.ACTIVE;
import static com.github.t1.bulmajava.basic.Style.WHITE;
import static com.github.t1.bulmajava.components.Navbar.NAVBAR_FIXED_TOP;
import static com.github.t1.bulmajava.components.Tabs.tabs;
import static com.github.t1.bulmajava.elements.Button.button;
import static com.github.t1.bulmajava.elements.Button.buttons;
import static com.github.t1.bulmajava.elements.Icon.icon;
import static com.github.t1.bulmajava.helpers.ColorsHelper.dark;
import static com.github.t1.bulmajava.layout.Container.container;
import static com.github.t1.bulmajava.layout.Section.section;
import static com.github.t1.htmljava.Anchor.a;
import static com.github.t1.htmljava.Body.body;
import static com.github.t1.htmljava.Html.html;
import static com.github.t1.htmljava.HtmlBasics.div;
import static com.github.t1.htmljava.HtmlBasics.element;
import static com.github.t1.htmljava.HtmlBasics.li;
import static com.github.t1.htmljava.HtmlBasics.span;
import static com.github.t1.htmljava.Renderable.RenderableString.string;
import static com.github.t1.htmljava.Renderable.UnsafeString.unsafeString;

@RequestScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Page implements Renderable {
    private final HttpSession session;
    private final UriInfo uriInfo;
    private final Login login;

    @ConfigProperty(name = "htmx.debug", defaultValue = "false") boolean debug;

    private Html html;
    private Section section;

    public Page title(String title) {
        //noinspection CommaExpressionJS,JSUnresolvedReference
        this.html = html(title)
                .stylesheet("/webjars/fortawesome__fontawesome-free/css/all.css")
                .stylesheet("/webjars/bulma/css/bulma.css")
                .stylesheet("validation.css")
                .script("/webjars/htmx.org/dist/htmx.js")
                .script("/webjars/htmx-ext-json-enc/json-enc.js")
                .script("/webjars/htmx-ext-ws/ws.js")
                .script("/webjars/htmx-ext-debug/debug.js")
                .script("validation.js")
                .content(body().has(NAVBAR_FIXED_TOP).content(
                        container().content(
                                this.section = section().classes("mt-6")
                                        .attr("hx-ext", "ws,json-enc" + (debug ? ",debug" : ""))
                                        .attr("ws-connect", "/connect/" + session.getId())
                                        .content(
                                                navbar(),
                                                Title.title(title)))));
        var body = this.html.findElement(e -> e.hasTagName("body")).orElseThrow();
        body.content(element("script").content(unsafeString("""
                document.body.addEventListener("reload-page", function(){
                    window.location.reload();
                })
                """)));
        return this;
    }

    private Navbar navbar() {
        return Navbar.navbar("the-navbar").classes("is-fixed-top", "px-5", "has-shadow")
                .hasBackground(dark(SUCCESS))
                .start(tabs().content(
                        tab("Search", SearchPage.PATH, "search"),
                        tab("Validation", "/validation", "wrench"),
                        tab("Static", "/static/index.html", "file-alt")))
                .end(div().content(buttons().content(
                        button().is(SMALL, WHITE).content(
                                div().id("ticker").content(string("?"))),
                        login.button())));
    }

    private Element tab(String text, String href, String icon) {
        var a = a().has(text(WHITE));
        if (icon != null) a = a.content(icon(icon).is(SMALL).ariaHidden(true));
        var item = li().content(a.content(span(text)).href(href));
        if (href.equals(uriInfo.getPath())) item = item.is(ACTIVE);
        return item;
    }

    public Page content(Renderable... content) {
        this.section.content(content);
        return this;
    }


    @Override public void render(Renderer renderer) {html.render(renderer);}
}
