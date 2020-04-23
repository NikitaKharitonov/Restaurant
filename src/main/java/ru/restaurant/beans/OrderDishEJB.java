package ru.restaurant.beans;

import ru.restaurant.Database;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.sql.SQLException;
import java.util.List;

@Remote
@Stateless
public class OrderDishEJB {

    public void addOrderDish(int orderId, String dishName, int quantity) throws SQLException {
        if(Database.orderDishExists(dishName, orderId))
            Database.updateOrderDishQuantity(orderId, dishName, Database.getOrderDishQuantity(orderId, dishName) + quantity);
        else
            Database.addOrderDish(orderId, dishName, quantity);

    }

    public List<String> getDishNameList() throws SQLException {
        return Database.getDishNameList();
    }

}
