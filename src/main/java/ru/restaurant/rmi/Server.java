package ru.restaurant.rmi;

import ru.restaurant.model.Order;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements ServerIntf {

    public Server() throws RemoteException {
    }

    @Override
    public Order serve(Order order) {
        order.sort((x, y) -> (int) (y.getCost() - x.getCost()));

        // delete duplicates
        for (int i = 1; i < order.orderDishesSize(); ++i) {
            if (order.getOrderDish(i - 1).equals(order.getOrderDish(i))) {
                order.removeOrderDish(i - 1);
                --i;
            }
        }

        return order;
    }

    public static void main(String[] args) {
        System.out.println("RMI server started");

        try { //special exception handler for registry creation
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
        }

        try {
            //Instantiate org.example.rmi.Server
            Server obj = new Server();

            // Bind this object instance to the name "org.example.rmi.Server"
            Naming.rebind("//localhost/org.example.rmi.Server", obj);

            System.out.println("PeerServer bound in registry");
        } catch (Exception e) {
            System.err.println("RMI server exception:" + e);
            e.printStackTrace();
        }
    }
}
