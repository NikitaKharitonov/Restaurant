package ru.restaurant.servlets;

import ru.restaurant.model.Customer;
import ru.restaurant.Database;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "ResultHtml", urlPatterns = "/result.html")
public class ResultHtml extends HttpServlet {

    private final static String RESULT_JSP = "jsp/result.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object object = request.getSession().getAttribute("customer");
        if (object instanceof Customer) {
            Customer customer = (Customer) object;
            try {
                customer = Database.getCustomerByUsername(customer.getUsername());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            request.getSession().setAttribute("customer", customer);
        }
        request.getRequestDispatcher(RESULT_JSP).forward(request, response);
    }
}
