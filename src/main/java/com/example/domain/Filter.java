package com.example.domain;

import static java.util.Locale.ROOT;

public record Filter(String title, String slug, String id, String value) {
    public Filter(String title, String id, String value) {
        this(title, title.toLowerCase(ROOT), id, value);
    }

    public String expression() {return slug + ":" + id;}

    public boolean matches(String filter) {
        if (filter != null && filter.startsWith(slug + ":"))
            return id.equals(filter.substring(slug.length() + 1));
        return true;
    }
}
