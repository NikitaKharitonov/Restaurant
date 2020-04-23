package ru.restaurant.beans;

import ru.restaurant.CustomerNotFoundException;
import ru.restaurant.Database;
import ru.restaurant.model.Customer;

import javax.ejb.*;
import java.sql.SQLException;

@Remote
@Stateless
public class CustomerEJB {

    public Customer validateUserLogin(String username, String password) throws CustomerNotFoundException, SQLException {

        if (Database.customerExists(username, password)) {
            return Database.getCustomerByUsername(username);
        } else
            throw new CustomerNotFoundException();

    }

    public void deleteOrderDish(String dishName, int orderId) throws SQLException {
        Database.deleteOrderDish(dishName, orderId);
    }

    public Customer getCustomer(String username) throws SQLException {
        return Database.getCustomerByUsername(username);
    }
}
