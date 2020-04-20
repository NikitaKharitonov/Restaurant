package ru.restaurant.rmi;

import ru.restaurant.model.Order;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerIntf extends Remote {
    Order serve(Order order) throws RemoteException;
}
