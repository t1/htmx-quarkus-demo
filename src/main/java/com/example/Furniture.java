package com.example;

import com.github.t1.bulmajava.basic.Renderable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static com.github.t1.bulmajava.basic.Basic.br;
import static com.github.t1.bulmajava.basic.Renderable.ConcatenatedRenderable.concat;
import static com.github.t1.bulmajava.basic.Renderable.RenderableString.string;
import static java.util.Locale.ROOT;

@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true) @SuperBuilder
public class Furniture extends Product {
    FurnitureCategory category;
    String description;

    @Override public Renderable details() {
        return concat(
                string("best used in: " + category),
                br(),
                string((description == null) ? "" : description));
    }

    public List<Filter> filters() {
        return List.of(new Filter("Room", categoryId(), category.toString()));
    }

    private String categoryId() {return category.name().toLowerCase(ROOT).replace('_', '-');}
}
