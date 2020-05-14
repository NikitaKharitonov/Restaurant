package ru.restaurant.controller;

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

@ManagedBean
@SessionScoped
public class CustomerBean {
    @EJB
    private final CustomerEJB customerEJB = new CustomerEJB();
    private String name;
    private String username;
    private String password;
    private Customer customer;
    private String message;

    public CustomerBean () {
    }

    public CustomerEJB getCustomerEJB() {
        return customerEJB;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        this.customer = customerEJB.validateUserLogin(username, password);
        if (customer != null)
            return this.message = "success";
        else
            return this.message = "failed";
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
        customerEJB.deleteOrderDish(dishName, orderId);
        updateCustomer();
    }

    public void updateCustomer() {
        this.customer = customerEJB.getCustomer(username);
    }

    public void addOrder() {
        this.customerEJB.addOrder(customer);
        updateCustomer();
    }

    public void deleteOrder(int orderId) {
        this.customerEJB.deleteOrder(orderId);
        updateCustomer();
    }

    public String createCustomer() {
        customer = customerEJB.createCustomer(name, username, password);
        if (customer != null)
            return this.message = "success";
        else
            return this.message = "failed";
    }


}

