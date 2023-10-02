package com.example;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Stream;

import static com.example.FurnitureCategory.KITCHEN;
import static com.example.FurnitureCategory.LIVING_ROOM;

@ApplicationScoped
public class Products {
    private final List<Product> all = List.of(
            Book.builder().id(1001).author("J.R.R. Tolkien").name("The Lord Of The Rings").build(),
            Book.builder().id(1002).author("J.R.R. Tolkien").name("The Hobbit").build(),
            Book.builder().id(1003).author("George R.R. Martin").name("Game Of Thrones").build(),
            Shoe.builder().id(2001).brand("First Brand").name("Da Lil Shoe").size(10).build(),
            Shoe.builder().id(2002).brand("Ota Brand").name("Da Ota Shoe").size(11).build(),
            Shoe.builder().id(2003).brand("First Brand").name("Da Shoe").size(12).build(),
            Furniture.builder().id(3001).name("Table").description("One leg").category(LIVING_ROOM).build(),
            Furniture.builder().id(3002).name("Chair").description("Throne like").category(LIVING_ROOM).build(),
            Furniture.builder().id(3101).name("Sink").category(KITCHEN).build());

    public Stream<Product> search(String searchString) {
        return "*".equals(searchString) ? all.stream() : all.stream().filter(p -> p.matches(searchString));
    }
}
