package ru.restaurant.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@org.hibernate.annotations.NamedQueries({
        @org.hibernate.annotations.NamedQuery(name="GetCustomerByUsername", query = "from Customer where username = :username"),
        @org.hibernate.annotations.NamedQuery(name="GetCustomerByUsernameAndPassword", query = "FROM Customer WHERE username = :username AND password = :password"),

})
@Table(name = "customer")
@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {
    private int customerId;
    private String name;
    private String username;
    @XmlTransient
    private String password;
    @XmlElementWrapper(name="orderList")
    @XmlElement(name = "order")
    private List<Order> orderList = new ArrayList<>();

    public Customer(int customerId, String name, String username, String password, Order... orders) {
        this.customerId = customerId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.orderList.addAll(Arrays.asList(orders));
    }

    public Customer() {

    }

    @Column(name = "customer_id")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public Order getOrder(int index) {
        return orderList.get(index);
    }

    public void addOrder(Order order) {
        orderList.add(order);
    }

    public void removeOrder(int index) {
        orderList.remove(index);
    }

    public int orderSize() {
        return orderList.size();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", orderList=" + orderList +
                '}';
    }

    public static void write(Customer customer, Writer writer) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(customer, writer);
    }

    public static Customer read(Reader reader) {
        Gson gson = new Gson();
        return gson.fromJson(reader, Customer.class);
    }
}
