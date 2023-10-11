package com.example.app;

import com.example.domain.Filter;
import com.example.domain.Product;
import com.example.domain.Products;
import com.github.t1.bulmajava.basic.AbstractElement;
import com.github.t1.bulmajava.basic.Element;
import com.github.t1.bulmajava.basic.Renderable;
import com.github.t1.bulmajava.form.Field;
import com.github.t1.bulmajava.form.Input;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

import static com.github.t1.bulmajava.basic.Basic.*;
import static com.github.t1.bulmajava.basic.Renderable.ConcatenatedRenderable.concat;
import static com.github.t1.bulmajava.columns.Columns.columns;
import static com.github.t1.bulmajava.elements.Image.*;
import static com.github.t1.bulmajava.elements.ImageSize._64x64;
import static com.github.t1.bulmajava.elements.Title.title;
import static com.github.t1.bulmajava.form.Field.field;
import static com.github.t1.bulmajava.form.Input.input;
import static com.github.t1.bulmajava.form.InputType.TEXT;
import static com.github.t1.bulmajava.layout.Media.media;

@Path("/")
@SuppressWarnings("QsUndeclaredPathMimeTypesInspection")
public class SearchPage {
    public static final String PATH = "/search";

    @Inject Page page;
    @Inject Products products;
    @Context UriInfo uriInfo;
    @HeaderParam("Referer") URI referer;

    @GET @Path(PATH)
    public Renderable searchPage(
            @QueryParam("search") String search,
            @QueryParam("filter") String filterString) {
        List<Product> found = (search == null) ? List.of() : products.search(search).toList();
        var filterStrings = filterString == null ? new String[0] : filterString.split(",");
        var teasers = teasers(found, search, filterStrings);
        var filters = filtersFor(found, Set.of(filterStrings));

        if (fullPage())
            return page.title("Product Search").content(columns()
                    .column(3, filters(filters.stream()))
                    .column(searchField(search), resultList(teasers)));
        return concat(
                oob("filters").content(filters.stream()),
                oob("result-list").content(teasers.stream()),
                oob("search-input").content(searchInput(search)));
    }

    private List<Renderable> teasers(List<Product> found, String search, String... filterStrings) {
        var teasers = found.stream()
                .filter(product -> product.filters().stream()
                        .allMatch(filter -> Stream.of(filterStrings).allMatch(filter::matches)))
                .map(this::productTeaser)
                .toList();
        return teasers.isEmpty()
                ? List.of(title(4, search == null ? "Nothing searched, yet" : "No matches"))
                : teasers;
    }

    private Renderable productTeaser(Product product) {
        return media()
                .left(figure().content(imageP(_64x64).content(
                        img("128x128.png", "image"))))
                .content(
                        div().content(strong(product.getName())),
                        details(product));
    }

    private Renderable details(Product product) {
        return p(product.details());
    }

    private Collection<AbstractElement<?>> filtersFor(List<Product> products, Set<String> activeFilters) {
        Map<Class<? extends Product>, AbstractElement<?>> filterWidgets = new LinkedHashMap<>();
        for (Product product : products) {
            filterWidgets.compute(product.getClass(), (unused, existing) -> {
                if (existing == null)
                    existing = div().classes(product.getTypeSlug() + "-filters")
                            .content(title(5, product.getType()).classes("mb-1"));
                for (Filter filter : product.filters())
                    FilterWidget.filterWidget(existing, activeFilters, filter);
                return existing;
            });
        }
        return FilterWidget.stripSingleElementFilters(filterWidgets.values());
    }

    private boolean fullPage() {
        return referer == null || uriInfo.getRequestUri().equals(referer);
    }

    private Renderable filters(Stream<? extends Renderable> filters) {
        return div().content(
                title(6, "Filters"),
                div().id("filters").content(filters));
    }

    private static Field searchField(String search) {
        return field().label("Search")
                .iconRight("search")
                .control(searchInput(search));
    }

    private static Input searchInput(String search) {
        var input = input(TEXT).id("search-input").name("search")
                .attr("hx-get", PATH)
                .attr("hx-include", "*")
                .attr("hx-push-url", "true")
                .attr("hx-trigger", "keyup changed delay:500ms")
                .attr("hx-swap", "none") // we swap oob
                .placeholder("Search...");
        if (search != null) input.value(search);
        return input;
    }

    private static Element resultList(List<Renderable> teasers) {
        return div().id("result-list").content(teasers.stream());
    }

    private static Element oob(String id) {
        return div().id(id).attr("hx-swap-oob", "true");
    }
}
