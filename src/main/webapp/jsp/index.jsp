<%@ page import="ru.restaurant.Database" %>
<%@ page import="ru.restaurant.model.Customer" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
    <head>
        <title>Index</title>
        <style>
            <%@ include file="../css/style.css"%>
        </style>
    </head>
    <body>
        <%
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            String type = "t0";

            final String username = request.getParameter("username");
            final String password = request.getParameter("password");

            if (username == null || password == null) {
            }
            else if (username.compareTo("") == 0 || password.compareTo("") == 0) {
                type = "t1";
            }
            else {
                try {
                    if (Database.customerExists(username, password)) {
                        Customer customer = new Customer(0, "", username, password);
                        session.setAttribute("customer", customer);
                        response.sendRedirect("/restaurant/result.html");
                    } else {
                        type = "t2";
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        %>
        <div class="login">
            <h1>Login</h1>
            <form method="post" action="">
                <input class="<%=type%>" type="text" name="username" placeholder="Username" required="required" />
                <input class="<%=type%>" type="password" name="password" placeholder="Password" required="required" />
                <label style="color: red">
                    <%      if (type.equals("t1")) {                        %>
                    <%=         "поле обязательно для заполнения"           %><br>
                    <%      } else if (type.equals("t2")) {                 %>
                    <%=         "пользователь не найден"                    %><br>
                    <%      }                                               %>
                </label>
                <input type="submit" value="Log in" class="btn"/>
            </form>
        </div>
    </body>
</html>
