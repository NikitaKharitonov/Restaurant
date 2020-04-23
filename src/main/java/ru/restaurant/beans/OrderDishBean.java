package ru.restaurant.beans;

import ru.restaurant.model.OrderDish;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.validator.ValidatorException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@ManagedBean
@RequestScoped
public class OrderDishBean {
    @EJB
    private final OrderDishEJB orderDishEJB = new OrderDishEJB();
    @ManagedProperty("#{param.orderId}")
    private Integer orderId;
    private String dishName;
    private String quantity;
    private OrderDish orderDish;
    private List<String> dishNameList;
    private String message;

    public OrderDishBean() {

        try {
            dishNameList = orderDishEJB.getDishNameList();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public OrderDish getOrderDish() {
        return orderDish;
    }

    public void setOrderDish(OrderDish orderDish) {
        this.orderDish = orderDish;
    }

    public List<String> getDishNameList() {
        return dishNameList;
    }

    public void setDishNameList(List<String> dishNameList) {
        this.dishNameList = dishNameList;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void insert() {
        try {
            Flash fScope = FacesContext.getCurrentInstance().getExternalContext().getFlash();
            fScope.put("orderId", orderId);
            FacesContext.getCurrentInstance().getExternalContext().redirect("insert.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String addOrderDish(int orderId, CustomerBean customerBean) {
        try {
            orderDishEJB.addOrderDish(orderId, dishName, Integer.parseInt(quantity));
            customerBean.updateCustomer();
            return this.message = "success";
        } catch (SQLException exception) {
            return this.message = "sqlexception";
        }
    }


    public void validateQuantity(FacesContext facesContext, UIComponent uiComponent, Object o)
            throws ValidatorException {
        int quantity;
        try {
            quantity = Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            FacesMessage facesMessage = new FacesMessage("Введите целое число");
            throw new ValidatorException(facesMessage);
        }
        if (quantity < 1) {
            FacesMessage facesMessage = new FacesMessage("Количество должно быть больше 0");
            throw new ValidatorException(facesMessage);
        }
        if (quantity > 100) {
            FacesMessage facesMessage = new FacesMessage("Количество должно быть меньше 101");
            throw new ValidatorException(facesMessage);
        }
    }
}
