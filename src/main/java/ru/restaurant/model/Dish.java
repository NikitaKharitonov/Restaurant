package ru.restaurant.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Reader;
import java.io.Writer;

@Entity
@org.hibernate.annotations.NamedQueries({
        @org.hibernate.annotations.NamedQuery(name = "GetDishList", query = "from Dish")
})
@Table(name = "dish")
@XmlRootElement(name = "dish")
@XmlAccessorType(XmlAccessType.FIELD)
public class Dish {
    private String name;
    private double price;

    public Dish(String name, double price) {
        if (price <= 0)
            throw new IllegalArgumentException("price must be > 0");
        this.name = name;
        this.price = Math.round(price * 100) / 100.0;
    }

    public Dish() {

    }

    @Column(name = "name")
    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "price")
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }

    public static void write(Dish dish, Writer writer) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(dish, writer);
    }

    public static Dish read(Reader reader) {
        Gson gson = new Gson();
        return gson.fromJson(reader, Dish.class);
    }
}
