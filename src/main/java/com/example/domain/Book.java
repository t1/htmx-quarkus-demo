package com.example.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static java.util.Locale.ROOT;

@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true) @SuperBuilder
public class Book extends Product {
    @NonNull String author;

    @Override public String details() {return "by: " + author;}

    public List<Filter> filters() {
        return List.of(new Filter("Author", authorId(), lastName()));
    }

    private String authorId() {return lastName().toLowerCase(ROOT);}

    private String lastName() {return author.substring(author.lastIndexOf(' ') + 1);}
}
