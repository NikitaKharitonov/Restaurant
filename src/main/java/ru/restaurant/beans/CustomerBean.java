package ru.restaurant.beans;

import ru.restaurant.CustomerNotFoundException;
import ru.restaurant.model.Customer;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.sql.SQLException;

@ManagedBean
@SessionScoped
public class CustomerBean {
    @EJB
    private final CustomerEJB customerEJB = new CustomerEJB();
    private String username;
    private String password;
    private Customer customer;
    private String message;

    public CustomerBean () {
    }

    public CustomerEJB getCustomerEJB() {
        return customerEJB;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String validateUserLogin() {
        try {
            this.customer = customerEJB.validateUserLogin(username, password);
            return this.message = "success";
        } catch (SQLException exception) {
            return this.message = "sqlexception";
        } catch (CustomerNotFoundException exception) {
            return this.message = "notfound";
        }
    }

    public void downloadXml() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();

            response.setContentType("text/xml");
            response.setHeader("Content-Disposition", "attachment;filename=file.xml");

            jaxbMarshaller.marshal(customer, response.getWriter());

            ctx.responseComplete();
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrderDish(String dishName, int orderId) {
        try {
            customerEJB.deleteOrderDish(dishName, orderId);
            updateCustomer();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void updateCustomer() throws SQLException {
        this.customer = customerEJB.getCustomer(username);
    }


}

