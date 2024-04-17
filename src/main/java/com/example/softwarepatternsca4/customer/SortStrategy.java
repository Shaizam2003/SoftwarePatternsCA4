package com.example.softwarepatternsca4.customer;

import com.example.softwarepatternsca4.models.Product;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public interface SortStrategy {
    void sort(List<Product> products);
}

// Concrete strategy classes which carry out sorting functionality as applicable
class TitleDescendingSortStrategy implements SortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparing(Product::getCustomTitle));
        Collections.reverse(products);
    }
}

class TitleAscendingSortStrategy implements SortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparing(Product::getCustomTitle));
    }
}

class PriceDescendingSortStrategy implements SortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparingDouble(p -> Double.parseDouble(p.getCustomPrice())));
        Collections.reverse(products);
    }
}

class PriceAscendingSortStrategy implements SortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparingDouble(p -> Double.parseDouble(p.getCustomPrice())));
    }
}
