package com.example.softwarepatternsca4.customer;

import com.example.softwarepatternsca4.models.Product;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public interface SortProductStrategy {
    void sort(List<Product> products);
}

// Concrete strategy classes which carry out sorting functionality as applicable
class ProductTitleDescendingSortStrategy implements SortProductStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparing(Product::getCustomTitle));
        Collections.reverse(products);
    }
}

class ProductTitleAscendingSortStrategy implements SortProductStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparing(Product::getCustomTitle));
    }
}

class ProductPriceDescendingSortStrategy implements SortProductStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparingDouble(p -> Double.parseDouble(p.getCustomPrice())));
        Collections.reverse(products);
    }
}

class ProductPriceAscendingSortStrategy implements SortProductStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparingDouble(p -> Double.parseDouble(p.getCustomPrice())));
    }
}
