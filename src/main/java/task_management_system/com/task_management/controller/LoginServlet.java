package task_management_system.com.task_management.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.dto.UserDTO;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserDAO dao = new UserDAO();
        UserDTO user = dao.login(email, password);


        UserDAO userDAO = new UserDAO();
        UserDTO loginUser = userDAO.login(email, password);

        if (loginUser == null) {
        	response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
        }
        
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", user);           
        } else {

            request.setAttribute("error", "メールアドレスまたはパスワードが違います。");
            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
            rd.forward(request, response);
        }


        if ("pending_admin".equals(loginUser.getRole())) {
            request.setAttribute("error", "管理ユーザーは現在承認待ちです。");
            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
            rd.forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        loginUser.setPassword(null);
        session.setAttribute("loginUser", loginUser);
        

        response.sendRedirect(request.getContextPath() + "/dashboard");

    }
}