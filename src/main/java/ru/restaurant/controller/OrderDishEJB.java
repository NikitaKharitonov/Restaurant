package ru.restaurant.controller;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.restaurant.model.Dish;
import ru.restaurant.model.Order;
import ru.restaurant.model.OrderDish;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Remote
@Stateless
public class OrderDishEJB {

    public static EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("Restaurant");

    public static boolean orderDishExists(String name, int orderId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        OrderDish orderDish = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Session session = (Session) entityManager.getDelegate();

            Query query = session.getNamedQuery("GetOrderDishByDishNameAndOrderId");
            query.setParameter("name", name);
            query.setParameter("orderId", orderId);
            orderDish = (OrderDish) query.getSingleResult();

            entityTransaction.commit();
        } catch (javax.persistence.NoResultException e) {
            return false;
        } catch (Exception e) {
            if (entityTransaction != null)
                entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
        return orderDish != null;
    }

    public static int getOrderDishQuantity(int orderId, String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        int quantity = 0;
        try {
            entityTransaction = entityManager.getTransaction();
            Session session = (Session) entityManager.getDelegate();

            Query query = session.getNamedQuery("GetOrderDishByDishNameAndOrderId");
            query.setParameter("name", name);
            query.setParameter("orderId", orderId);
            OrderDish orderDish = (OrderDish) query.getSingleResult();
            quantity = orderDish.getQuantity();

            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null)
                entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
        return quantity;
    }

    public static void updateOrderDishQuantity(int orderId, String name, int quantity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Session session = (Session) entityManager.getDelegate();

            Query query = session.getNamedQuery("UpdateOrderDishQuantity");
            query.setParameter("quantity", quantity);
            query.setParameter("orderId", orderId);
            query.setParameter("name", name);
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

    public void addOrderDish(int orderId, String name, int quantity) {
        System.out.println("orderId " + orderId);
        System.out.println("name " + name);
        System.out.println("quantity " + quantity);
        if(orderDishExists(name, orderId))
            updateOrderDishQuantity(orderId, name, getOrderDishQuantity(orderId, name) + quantity);
        else {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction entityTransaction = null;
            try {
                entityTransaction = entityManager.getTransaction();
                entityTransaction.begin();

                Order order = entityManager.find(Order.class, orderId);
                Dish dish = entityManager.find(Dish.class, name);
                order.addOrderDish(new OrderDish(dish, order, quantity));
                entityManager.merge(order);

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

    }

    public List<String> getDishNameList() throws SQLException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        List<String> dishNameList = new ArrayList<>();
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Session session = (Session) entityManager.getDelegate();



            Query query = session.getNamedQuery("GetDishList");
            List<Dish> dishList = (List<Dish>) query.getResultList();
            for (Dish dish: dishList)
                dishNameList.add(dish.getName());


            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null)
                entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
        return dishNameList;
    }

}
