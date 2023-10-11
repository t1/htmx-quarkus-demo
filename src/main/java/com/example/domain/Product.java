package com.example.domain;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static java.util.Locale.ROOT;

@Data @SuperBuilder
public abstract class Product {
    Integer id;
    String name;

    public abstract String details();

    public boolean matches(String searchString) {
        return toString().toLowerCase(ROOT).contains(searchString.toLowerCase(ROOT));
    }

    public String getTypeSlug() {return getType().toLowerCase(ROOT);}

    public String getType() {return getClass().getSimpleName();}

    public abstract List<Filter> filters();
}
