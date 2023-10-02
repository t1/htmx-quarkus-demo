package com.example;

import com.github.t1.bulmajava.basic.AbstractElement;
import com.github.t1.bulmajava.basic.Element;
import com.github.t1.bulmajava.basic.Renderable;
import com.github.t1.bulmajava.elements.Button;
import com.github.t1.bulmajava.elements.Title;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.github.t1.bulmajava.basic.Color.INFO;
import static com.github.t1.bulmajava.basic.State.ACTIVE;
import static com.github.t1.bulmajava.elements.Button.button;
import static java.util.Locale.ROOT;

public record Filter(String title, String slug, String id, String value) {
    public Filter(String title, String id, String value) {
        this(title, title.toLowerCase(ROOT), id, value);
    }

    public void addTo(AbstractElement<?> existing, Set<String> activeFilters) {
        existing.getOrCreate(slug + "-filter-group", this::groupElement)
                .option(this, activeFilters);
    }

    private String expression() {return slug + ":" + id;}

    public boolean matches(String filter) {
        if (filter != null && filter.startsWith(slug + ":"))
            return id.equals(filter.substring(slug.length() + 1));
        return true;
    }


    private GroupElement groupElement() {return new GroupElement(this);}

    @EqualsAndHashCode(callSuper = true)
    private static class GroupElement extends AbstractElement<GroupElement> {
        private final Element title;
        private final AbstractElement<?> addon;

        public GroupElement(Filter filter) {
            super("div");
            this.title = getOrCreate(filter.slug + "-title", () -> filterTitle(filter.title));
            this.addon = getOrCreate(filter.slug + "-filter", Button::buttonsAddon);
        }

        private Element filterTitle(String text) {return Title.title(6, text).classes("mb-1 mt-2");}

        public void option(Filter filter, Set<String> activeFilters) {
            addon.getOrCreate(filter.expression(), () -> optionButton(filter, activeFilters));
        }

        private Button optionButton(Filter filter, Set<String> activeFilters) {
            var button = button(filter.value)
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
                allFilters.removeIf(s -> s.startsWith(filter.slug + ":"));
                allFilters.add(filter.expression());
            }
            return String.join(",", allFilters);
        }

        public long optionCount() {return addon.contentStream().count();}
    }

    public static Collection<AbstractElement<?>> stripSingleElementFilters(Collection<AbstractElement<?>> categories) {
        for (var category : new ArrayList<>(categories)) {
            var categoryContent = category.contentAs(Renderable.ConcatenatedRenderable.class).renderables();
            int groupCount = 0;
            for (var i : new ArrayList<>(categoryContent)) {
                if (i instanceof GroupElement group) {
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
