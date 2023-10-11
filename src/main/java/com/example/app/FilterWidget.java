package com.example.app;

import com.example.domain.Filter;
import com.example.domain.Product;
import com.github.t1.bulmajava.basic.AbstractElement;
import com.github.t1.bulmajava.basic.Element;
import com.github.t1.bulmajava.basic.Renderable;
import com.github.t1.bulmajava.elements.Button;
import com.github.t1.bulmajava.elements.Title;

import java.util.*;

import static com.github.t1.bulmajava.basic.Basic.div;
import static com.github.t1.bulmajava.basic.Color.INFO;
import static com.github.t1.bulmajava.basic.State.ACTIVE;
import static com.github.t1.bulmajava.elements.Button.button;
import static com.github.t1.bulmajava.elements.Title.title;

class FilterWidget extends AbstractElement<FilterWidget> {
    static Collection<AbstractElement<?>> filtersFor(List<Product> products, Set<String> activeFilters) {
        Map<Class<? extends Product>, AbstractElement<?>> filterWidgets = new LinkedHashMap<>();
        for (Product product : products) {
            filterWidgets.compute(product.getClass(), (unused, existing) -> {
                if (existing == null)
                    existing = div().classes(product.getTypeSlug() + "-filters")
                            .content(title(5, product.getType()).classes("mb-1"));
                for (Filter filter : product.filters())
                    filterWidget(existing, activeFilters, filter);
                return existing;
            });
        }
        return stripSingleElementFilters(filterWidgets.values());
    }

    private static void filterWidget(AbstractElement<?> existing, Set<String> activeFilters, Filter filter) {
        existing.getOrCreate(filter.slug() + "-filter-group", () -> new FilterWidget(filter))
                .option(filter, activeFilters);
    }

    private final AbstractElement<?> addon;

    private FilterWidget(Filter filter) {
        super("div");
        getOrCreate(filter.slug() + "-title", () -> filterTitle(filter.title()));
        this.addon = getOrCreate(filter.slug() + "-filter", Button::buttonsAddon);
    }

    private Element filterTitle(String text) {return Title.title(6, text).classes("ml-1 mb-1 mt-2");}

    private void option(Filter filter, Set<String> activeFilters) {
        addon.getOrCreate(filter.expression(), () -> optionButton(filter, activeFilters));
    }

    private Button optionButton(Filter filter, Set<String> activeFilters) {
        var button = button(filter.value())
                .attr("hx-get", SearchPage.PATH + "?filter=" + calculate(filter, activeFilters))
                .attr("hx-include", "*")
                .attr("hx-swap", "none") // we swap oob
                .attr("hx-push-url", "true");
        if (activeFilters.contains(filter.expression())) button.is(ACTIVE, INFO);
        return button;
    }

    private String calculate(Filter filter, Set<String> activeFilters) {
        var allFilters = new LinkedHashSet<>(activeFilters);
        if (activeFilters.contains(filter.expression())) {
            allFilters.remove(filter.expression());
        } else {
            allFilters.removeIf(s -> s.startsWith(filter.slug() + ":"));
            allFilters.add(filter.expression());
        }
        return String.join(",", allFilters);
    }

    private long optionCount() {return addon.contentStream().count();}

    private static Collection<AbstractElement<?>> stripSingleElementFilters(Collection<AbstractElement<?>> categories) {
        for (var category : new ArrayList<>(categories)) {
            var categoryContent = category.contentAs(Renderable.ConcatenatedRenderable.class).renderables();
            int groupCount = 0;
            for (var i : new ArrayList<>(categoryContent)) {
                if (i instanceof FilterWidget group) {
                    if (group.optionCount() == 1) {
                        categoryContent.remove(group);
                    } else {
                        groupCount++;
                    }
                }
            }
            if (groupCount == 0) {
                categories.remove(category);
            }
        }
        return categories;
    }
}
