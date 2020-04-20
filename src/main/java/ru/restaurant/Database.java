package ru.restaurant;

import com.google.gson.Gson;
import ru.restaurant.model.Customer;
import ru.restaurant.model.Dish;
import ru.restaurant.model.Order;
import ru.restaurant.model.OrderDish;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Has methods for access to the database specified by the given {@code URL}
 */
public class Database {

    // Used for connection to a database
    private static final String URL = "jdbc:postgresql://localhost:5432/restaurant";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    /**
     * Creates the {@code customer}, {@code zakaz}, {@code order_dish} and {@code dish} relations,
     * reads all data from the {@code customer1.json}, ..., {@code customer5.json} files
     * and adds the data to the newly created relations,
     * @throws SQLException if an error occurred when creating relations
     * or adding the data to the database.
     */
    public static void createAndFillTables() throws SQLException {
        // create the relations
        createCustomerTable();
        createOrderTable();
        createDishTable();
        createOrderDishTable();

        // read the customer and order data from the files
        ArrayList<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 5; ++i) {
            try (FileReader customerReader = new FileReader("src\\main\\resources\\customer" + i + ".json")) {
                Customer customer = Customer.read(customerReader);
                customers.add(customer);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        // read the dish data form the file
        ArrayList<Dish> dishes = new ArrayList<>();
        Gson gson = new Gson();
        try (FileReader fileReader = new FileReader("src/main/resources/dishes.json")) {
            Dish[] dishArray = gson.fromJson(fileReader, Dish[].class);
            dishes.addAll(Arrays.asList(dishArray));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // add the dish data to the relation
        for (Dish dish: dishes)
            addDish(dish.getName(), dish.getPrice());

        // add the customer and order data to the relations
        for (Customer customer : customers) {
            addCustomer(customer.getCustomerId(), customer.getName(), customer.getUsername(), customer.getPassword());
            for (int j = 0; j < customer.orderSize(); ++j) {
                Order order = customer.getOrder(j);
                addOrder(customer.getCustomerId(), order.getOrderId(), order);
            }
        }
    }

    /**
     * Creates the {@code customer} relation in the database
     */
    public static void createCustomerTable() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE customer (\n" +
                            "    customer_id INTEGER PRIMARY KEY,\n" +
                            "    name VARCHAR(100),\n" +
                            "    username VARCHAR(100) UNIQUE,\n" +
                            "    password VARCHAR(100)\n" +
                            "    );"
            );
            preparedStatement.execute();
        }
    }

    /**
     * Creates the {@code zakaz} relation in the database
     */
    public static void createOrderTable() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE zakaz (\n" +
                            "    order_id INTEGER PRIMARY KEY,\n" +
                            "    customer_id INTEGER REFERENCES customer(customer_id)\n" +
                            "    );"
            );
            preparedStatement.execute();
        }
    }

    /**
     * Creates the {@code dish} relation in the database
     */
    public static void createDishTable() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE dish (\n" +
                            "    name VARCHAR(100) PRIMARY KEY,\n" +
                            "    price NUMERIC(5, 2)\n" +
                            "    );"
            );
            preparedStatement.execute();
        }
    }

    /**
     * Creates the {@code order_dish} relation in the database
     */
    public static void createOrderDishTable() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE order_dish (\n" +
                            "    order_id INTEGER REFERENCES zakaz(order_id),\n" +
                            "    name VARCHAR(100) REFERENCES dish(name),\n" +
                            "    quantity INTEGER,\n" +
                            "    PRIMARY KEY (order_id, name));"
            );
            preparedStatement.execute();
        }
    }

    /**
     * Adds the new tuple with the given attribute values
     * to the {@code customer} relation
     *
     * @param customerId the {@code customer_id} attribute value
     * @param name the {@code name} attribute value
     * @param username the {@code username} attribute value
     * @param password the {@code password} attribute value
     */
    public static void addCustomer(int customerId, String name, String username, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO customer (customer_id, name, username, password) VALUES (?, ?, ?, ?)"
            );
            preparedStatement.setInt(1, customerId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, password);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Adds the new tuple with the given attribute values
     * to the {@code zakaz} relation
     *
     * @param customerId the {@code customer_id} attribute value
     * @param orderId the {@code order_id} attribute value
     */
    public static void addOrder(int customerId, int orderId, Order order) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO zakaz (order_id, customer_id) VALUES (?, ?)"
            );
            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, customerId);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(
                    "INSERT INTO order_dish (order_id, name, quantity) VALUES (?, ?, ?)"
            );
            for (int i = 0; i < order.orderDishesSize(); ++i) {
                preparedStatement.setInt(1, orderId);
                preparedStatement.setString(2, order.getOrderDish(i).getDish().getName());
                preparedStatement.setInt(3, order.getOrderDish(i).getQuantity());
                preparedStatement.executeUpdate();
            }
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    /**
     * Adds the new tuple with the given attribute values
     * to the {@code order_dish} relation
     *
     * @param orderId the {@code order_id} attribute value
     * @param name the {@code name} attribute value
     * @param quantity the {@code quantity} attribute value
     */
    public static void addOrderDish(int orderId, String name, int quantity) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO order_dish (order_id, name, quantity) VALUES (?, ?, ?)"
            );
            preparedStatement.setInt(1, orderId);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, quantity);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Adds the new tuple with the given attribute values
     * to the {@code dish} relation
     *
     * @param name the {@code name} attribute value
     * @param price the {@code price} attribute value
     */
    public static void addDish(String name, double price) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO dish (name, price) VALUES (?, ?)"
            );
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, price);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Deletes the tuple from the {@code order_dish} relation
     * by the given {@code name} attribute value
     *
     * @param dishName the {@code name} attribute value of the tuple to be deleted
     */
    public static void deleteOrderDish(String dishName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM order_dish WHERE name = ?"
            );
            preparedStatement.setString(1, dishName);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Deletes the tuple from the {@code zakaz} relation
     * by the given {@code order_id} attribute value,
     * also deletes the referring tuples from the {@code order_dish} relation,
     *
     * @param id the {@code order_id} attribute value of the tuple to be deleted
     */
    public static void deleteOrder(int id) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM order_dish WHERE order_id = ?"
            );
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(
                    "DELETE FROM zakaz WHERE order_id = ?"
            );
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        }
    }

    /**
     * Checks if the tuple with the given attribute values exists
     * in the {@code customer} relation
     *
     * @param username the {@code username} attribute value
     * @param password the {@code password} attribute value
     * @return {@code true} if the tuple exists
     */
    public static boolean customerExists(String username, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM customer WHERE username = ? AND password = ?"
            );
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    /**
     * Queries the tuples from the {@code zakaz} relation
     * by the given {@code customer_id} attribute value
     *
     * @param customerId the {@code customer_id} attribute value
     * @return {@code ArrayList<org.example.model.Order>} composed of the found tuples, {@code null} if no tuples found
     */
    public static ArrayList<Order> getOrdersByCustomerId(int customerId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM zakaz WHERE customer_id = ?"
            );

            preparedStatement.setInt(1, customerId);
            ResultSet set = preparedStatement.executeQuery();
            ArrayList<Order> orders = new ArrayList<>();

            while (set.next()) {
                Order order = getOrderByOrderId(set.getInt("order_id"));
                orders.add(order);
            }

            return orders;
        }
    }

    /**
     * Queries the tuple from the {@code order_dish} relation
     * by the given {@code order_id} attribute value
     *
     * @param orderId the {@code order_id} attribute value
     * @return {@code org.example.model.Order} created by the found tuple,
     * {@code null} if no tuples found
     */
    public static Order getOrderByOrderId(int orderId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM order_dish WHERE order_id = ?"
                );

            preparedStatement.setInt(1, orderId);
            ResultSet orderDishSet = preparedStatement.executeQuery();
            Order order = new Order(orderId);

            while (orderDishSet.next()) {

                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM dish WHERE name = ?"
                );

                preparedStatement.setString(1, orderDishSet.getString("name"));
                ResultSet dishSet = preparedStatement.executeQuery();

                if (dishSet.next()) {
                    OrderDish orderDish = new OrderDish(new Dish(dishSet.getString("name"), dishSet.getInt("price")),
                            orderDishSet.getInt("quantity"));
                    order.addOrderDish(orderDish);
                }
            }

            return order;
        }
    }

    /**
     * Queries the tuple from the {@code customer} relation
     * by the given {@code customer_id} attribute value
     *
     * @param customerId the {@code order_id} attribute value
     * @return {@code org.example.model.Customer} created by the found tuple,
     * {@code null} if no tuples found
     */
    public static Customer getCustomerByCustomerId(int customerId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM customer WHERE customer_id = ?"
            );

            preparedStatement.setInt(1, customerId);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {
                Customer customer = new Customer(resultSet.getInt("customer_id"),
                        resultSet.getString("name"),
                        resultSet.getString("username"),
                        resultSet.getString("password"));

                List<Order> orderList = getOrdersByCustomerId(customer.getCustomerId());
                if (orderList != null)
                    for (Order order: orderList)
                        customer.addOrder(order);
                return customer;
            }
            else return null;
        }
    }

    /**
     * Queries the tuple from the {@code customer} relation
     * by the given {@code username} attribute value
     *
     * @param username the {@code order_id} attribute value
     * @return {@code org.example.model.Customer} created by the found tuple,
     * {@code null} if no tuples found
     */
    public static Customer getCustomerByUsername(String username) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM customer WHERE username = ?"
            );

            preparedStatement.setString(1, username);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getInt("customer_id"),
                        resultSet.getString("name"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );

                List<Order> orderList = getOrdersByCustomerId(customer.getCustomerId());
                if (orderList != null)
                    for (Order order: orderList)
                        customer.addOrder(order);
                return customer;
            }
            else return null;
        }
    }

    /**
     * Queries the tuples from the {@code customer} relation having at least one referring tuple
     * with the given {@code order_dish.name} attribute value in the {@code order_dish} relation
     *
     * @param orderDishName the {@code order_dish.name} attribute value
     * @return {@code ArrayList<org.example.model.Customer>} composed of the found tuples, {@code null} if no tuples found
     */
    public static ArrayList<Customer> getCustomersByOrderDishName(String orderDishName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM customer WHERE customer_id = " +
                            "ANY(SELECT customer_id FROM zakaz WHERE order_id = " +
                            "ANY(SELECT order_id FROM order_dish WHERE order_dish.name = ?))"
            );

            preparedStatement.setString(1, orderDishName);
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Customer> customers = new ArrayList<>();

            while (resultSet.next()) {
                Customer customer = new Customer(resultSet.getInt("customer_id"),
                        resultSet.getString("name"),
                        resultSet.getString("username"),
                        resultSet.getString("password"));

                List<Order> orderList = getOrdersByCustomerId(customer.getCustomerId());
                if (orderList != null)
                    for (Order order: orderList)
                        customer.addOrder(order);

                customers.add(customer);
            }

            return customers;
        }
    }

    /**
     * Queries the tuples from the {@code customer} relation by the given number
     * of referring tuples in the {@code zakaz} relation
     *
     * @param orderCount the number of tuples in the {@code zakaz} relation
     * @return {@code ArrayList<org.example.model.Customer>} composed of the found tuples,
     * {@code null} if no tuples found
     */
    public static ArrayList<Customer> getCustomersByOrderCount(int orderCount) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM customer WHERE customer_id = " +
                            "ANY(SELECT customer_id FROM zakaz GROUP BY customer_id HAVING COUNT(*) = ?)"
            );

            preparedStatement.setInt(1, orderCount);
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Customer> customers = new ArrayList<>();

            while (resultSet.next()) {
                Customer customer = new Customer(resultSet.getInt("customer_id"),
                        resultSet.getString("name"),
                        resultSet.getString("username"),
                        resultSet.getString("password"));

                List<Order> orderList = getOrdersByCustomerId(customer.getCustomerId());
                if (orderList != null)
                    for (Order order: orderList)
                        customer.addOrder(order);

                customers.add(customer);
            }

            return customers;
        }
    }
}
