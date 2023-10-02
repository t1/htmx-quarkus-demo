package com.example;

import com.github.t1.bulmajava.basic.Renderable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static com.github.t1.bulmajava.basic.Basic.p;
import static java.util.Locale.ROOT;

@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true) @SuperBuilder
public class Shoe extends Product {
    int size;
    String brand;

    @Override public Renderable details() {return p("by: " + brand + " (size: " + size + ")");}

    public List<Filter> filters() {
        return List.of(
                new Filter("Size", sizeString(), sizeString()),
                new Filter("Brand", brandId(), brand));
    }

    private String sizeString() {return Integer.toString(size);}

    private String brandId() {return brand.toLowerCase(ROOT).replace(' ', '-');}
}
