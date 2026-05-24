package task_management_system.com.task_management.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.dto.UserDTO;

@WebServlet("/")
public class DemoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserDAO userDAO = new UserDAO();
        UserDTO user = userDAO.findByEmail("demo@example.com");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        user.setPassword(null);

        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}