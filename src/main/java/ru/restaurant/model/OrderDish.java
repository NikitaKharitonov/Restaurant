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
        @org.hibernate.annotations.NamedQuery(name = "GetOrderDishByDishNameAndOrderId", query = "from OrderDish where name = :name and order_id = :orderId"),
        @org.hibernate.annotations.NamedQuery(name = "UpdateOrderDishQuantity", query = "update OrderDish set quantity=:quantity where name = :name and order_id = :orderId"),
        @org.hibernate.annotations.NamedQuery(name = "DeleteOrderDishByOrderIdAndName", query = "delete from OrderDish where name = :name and order_id = :orderId"),
        @org.hibernate.annotations.NamedQuery(name = "DeleteOrderDishByOrderId", query = "delete from OrderDish where order_id = :orderId")

})
@Table(name = "order_dish")
@XmlRootElement(name = "orderDish")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderDish {
    private int quantity;

    @EmbeddedId
    private OrderDishId orderDishId;

    public OrderDish() {

    }

    public OrderDish(Dish dish, Order order, int quantity) {
        this.orderDishId = new OrderDishId(dish, order);
        this.quantity = quantity;
    }

    public Dish getDish() {
        return orderDishId.getDish();
    }

    public void setDish(Dish dish) {
        this.orderDishId.setDish(dish);
    }

    @Column(name = "quantity")
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderDishId getOrderDishId() {
        return orderDishId;
    }

    public void setOrderDishId(OrderDishId orderDishId) {
        this.orderDishId = orderDishId;
    }

    public double getCost() {
        return orderDishId.getDish().getPrice() * quantity;
    }


    @Override
    public String toString() {
        return "OrderDish{" +
                "quantity=" + quantity +
                ", dish=" + getDish().toString() +
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
