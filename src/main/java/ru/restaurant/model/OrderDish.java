package ru.restaurant.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Reader;
import java.io.Writer;

@XmlRootElement(name = "orderDish")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderDish {
    private Dish dish;
    private int quantity;

    public OrderDish(Dish dish, int quantity) {
        this.dish = dish;
        this.quantity = quantity;
    }

    public OrderDish() {

    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getCost() {
        return dish.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return "org.example.model.OrderDish{" +
                "dish=" + dish +
                ", quantity=" + quantity +
                '}';
    }

    public static void write(OrderDish orderDish, Writer writer) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(orderDish, writer);
    }

    public static OrderDish read(Reader reader) {
        Gson gson = new Gson();
        return gson.fromJson(reader, OrderDish.class);
    }
}
