package com.example.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static java.util.Locale.ROOT;

@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true) @SuperBuilder
public class Furniture extends Product {
    FurnitureCategory category;
    String description;

    @Override public String details() {return "best used in: " + category;}

    public List<Filter> filters() {
        return List.of(new Filter("Room", categoryId(), category.toString()));
    }

    private String categoryId() {return category.name().toLowerCase(ROOT).replace('_', '-');}
}
