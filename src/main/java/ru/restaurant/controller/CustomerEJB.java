package ru.restaurant.controller;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.restaurant.model.*;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

@Remote
@Stateless
public class CustomerEJB {

    public static EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("Restaurant");

    public Customer validateUserLogin(String username, String password) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        Customer customer = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Session session = (Session) entityManager.getDelegate();

            Query query = session.getNamedQuery("GetCustomerByUsernameAndPassword");
            query.setParameter("username", username);
            query.setParameter("password", password);
            customer = (Customer) query.getSingleResult();

            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null)
                entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
        return customer;
    }

    public void deleteOrderDish(String dishName, int orderId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            Session session = (Session) entityManager.getDelegate();

            Query query = session.getNamedQuery("DeleteOrderDishByOrderIdAndName");
            query.setParameter("name", dishName);
            query.setParameter("orderId", orderId);
            query.executeUpdate();

            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null)
                entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
    }

    public Customer getCustomer(String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        Customer customer = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            Session session = (Session) entityManager.getDelegate();

            Query query = session.getNamedQuery("GetCustomerByUsername");
            query.setParameter("username", username);
            customer = (Customer) query.getSingleResult();

            query = session.getNamedQuery("GetOrderListByCustomerId");
            query.setParameter("customerId", customer.getCustomerId());
            List<Order> orderList = (List<Order>) query.getResultList();
            customer.setOrderList(orderList);

            entityTransaction.commit();

        } catch (Exception e) {
            if (entityTransaction != null)
                entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
        return customer;
    }

    public void addOrder(Customer customer) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            customer.addOrder(new Order());
            entityManager.merge(customer);

            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null)
                entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
    }

    public void deleteOrder(int id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            Session session = (Session) entityManager.getDelegate();

            Query query = session.getNamedQuery("DeleteOrderDishByOrderId");
            query.setParameter("orderId", id);
            query.executeUpdate();
            query = session.getNamedQuery("DeleteOrderByOrderId");
            query.setParameter("orderId", id);
            query.executeUpdate();

            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null)
                entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
    }

    public Customer createCustomer(String name, String username, String password) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        Customer customer = new Customer();
        boolean success = false;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            customer.setName(name);
            customer.setUsername(username);
            customer.setPassword(password);
            entityManager.persist(customer);

            entityTransaction.commit();
            success = true;
        } catch (Exception e) {
            if (entityTransaction != null)
                entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
        return success ? customer : null;
    }
}
