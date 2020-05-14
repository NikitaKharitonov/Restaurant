package ru.restaurant.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "zakaz")
@XmlRootElement(name = "order")
@XmlAccessorType(XmlAccessType.FIELD)
@org.hibernate.annotations.NamedQueries({
        @org.hibernate.annotations.NamedQuery(name = "GetOrderListByCustomerId", query = "from Order where customer_id = :customerId"),
        @org.hibernate.annotations.NamedQuery(name = "DeleteOrderByOrderId", query = "delete from Order where order_id = :orderId")
})
public class Order {
    private int orderId;
    @XmlElementWrapper(name="orderDishList")
    @XmlElement(name = "orderDish")
    private List<OrderDish> orderDishList = new ArrayList<>();

    public Order(int orderId, OrderDish... orderDishes) {
        this.orderId = orderId;
        this.orderDishList.addAll(Arrays.asList(orderDishes));
    }

    public Order() {

    }

    @Column(name = "order_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }



    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    public List<OrderDish> getOrderDishList() {
        return orderDishList;
    }

    public void setOrderDishList(List<OrderDish> orderDishList) {
        this.orderDishList = orderDishList;
    }

    public void setOrderDishList(ArrayList<OrderDish> orderDishList) {
        this.orderDishList = orderDishList;
    }

    public OrderDish getOrderDish(int index) {
        return orderDishList.get(index);
    }

    public void addOrderDish(OrderDish orderDish) {
        orderDishList.add(orderDish);
    }

    public void removeOrderDish(int index) {
        orderDishList.remove(index);
    }

    public int orderDishesSize() {
        return orderDishList.size();
    }

    public void sort(Comparator<OrderDish> comparator) {
        orderDishList.sort(comparator);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderDishes=" + orderDishList +
                '}';
    }

    public static void write(Order order, Writer writer) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(order, writer);
    }

    public static Order read(Reader reader) {
        Gson gson = new Gson();
        return gson.fromJson(reader, Order.class);
    }
}
