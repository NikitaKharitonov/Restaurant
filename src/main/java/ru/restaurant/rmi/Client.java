package ru.restaurant.rmi;

import ru.restaurant.model.Order;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {
    static ServerIntf obj = null;

    public static void main(String[] args) throws InterruptedException {
        boolean cont;
        FileReader fileReader = null;
        FileWriter fileWriter = null;
        String inputFilePath;
        String outputFilePath;
        Scanner scanner = new Scanner(System.in);

        do {
            cont = false;
            boolean success = false;
            System.out.println("Connecting to Server...");

            do {
                try {
                    obj = (ServerIntf) Naming.lookup("//localhost/org.example.rmi.Server");
                    success = true;
                } catch (NotBoundException | MalformedURLException | RemoteException e) {
                    Thread.sleep(3000);
                }
            } while (!success);

            success = false;

            do {
                System.out.println("Input file: ");
                inputFilePath = scanner.next();
                try {
                    fileReader = new FileReader(inputFilePath);
                    success = true;
                } catch (FileNotFoundException e) {
                    System.err.println(e.getMessage());
                    System.err.println("Try again");
                }
            } while (!success);

            success = false;

            do {
                System.out.println("Output file: ");
                outputFilePath = scanner.next();
                try {
                    fileWriter = new FileWriter(outputFilePath);
                    success = true;
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    System.err.println("Try again");
                }
            } while (!success);

            try {
                Order order = Order.read(fileReader);
                Order sortedOrder = obj.serve(order);
                Order.write(sortedOrder, fileWriter);
            } catch (RemoteException e) {
                System.err.println("Failed to invoke a remote method: " + e);
            }

            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.print("Continue? (y/n): ");
            String ans = scanner.next();
            if (ans.charAt(0) == 'y')
                cont = true;
        } while(cont);
    }
}
