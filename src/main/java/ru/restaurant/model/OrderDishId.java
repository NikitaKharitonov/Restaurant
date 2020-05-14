package ru.restaurant.model;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class OrderDishId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "name")
    private Dish dish;

    public OrderDishId(Dish dish, Order order) {
        this.dish = dish;
        this.order = order;
    }

    public OrderDishId() {

    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }
}
