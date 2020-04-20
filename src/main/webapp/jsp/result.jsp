<%@ page import="ru.restaurant.model.Customer" %>
<%@ page import="ru.restaurant.Database" %>
<%@ page import="ru.restaurant.model.Order" %>
<%@ page import="java.util.List" %>
<%@ page import="ru.restaurant.model.OrderDish" %>
<%@ page import="java.io.IOException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Result</title>
    <style>
        <%@ include file="../css/style.css"%>
    </style>
</head>
<body>

<%
    Customer customer = (Customer) session.getAttribute("customer");
    if (customer != null) {
        List<Order> orders = customer.getOrderList();
%>
<div class="container-table">

    <div class="greeting">
        <label>Hello, <%=customer.getName()%>!</label>
    </div>

    <% for (Order order: orders) { %>
    <table>
        <caption>
            Order <%=order.getOrderId()%>
        </caption>
        <thead>
            <tr>
                <th>Name</th>
                <th>Price</th>
                <th>Quantity</th>
                <th>Total cost</th>
            </tr>
        </thead>
        <tbody>
        <% for (OrderDish orderDish: order.getOrderDishList()) { %>
            <tr>
                <td><%=orderDish.getDish().getName()%></td>
                <td><%=orderDish.getDish().getPrice()%></td>
                <td><%=orderDish.getQuantity()%></td>
                <td><%=orderDish.getCost()%></td>
            </tr>
        <% } %>
        </tbody>
    </table>
    <% } %>

    <div class="links">
        <span>
            <a href="/restaurant/">Index</a>
        </span>
        <span>
            <a href="/restaurant/result.xml">result.xml</a>
        </span>
    </div>
</div>

<%
    } else {
        response.sendRedirect("/restaurant/error");
    }
%>

</body>
</html>
