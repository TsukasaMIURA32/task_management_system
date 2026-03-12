package task_management_system.com.task_management.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.dto.UserDTO;


@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RegisterServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String userName = request.getParameter("userName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        // 未入力チェック
        if (userName == null || userName.isEmpty()
                || email == null || email.isEmpty()
                || password == null || password.isEmpty()
                || role == null || role.isEmpty()) {

            request.setAttribute("error", "未入力の項目があります。");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // roleの値チェック
        if (!"user".equals(role) && !"admin".equals(role)) {
            request.setAttribute("error", "ユーザー種別が不正です。");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
            dispatcher.forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();

        // メールアドレス重複チェック
        if (userDAO.existsByEmail(email)) {
            request.setAttribute("error", "そのメールアドレスは既に登録されています。");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // DTOにセット
        UserDTO user = new UserDTO();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        // DB登録
        boolean result = userDAO.insertUser(user);

        if (result) {
            response.sendRedirect("registerComplete.jsp");
        } else {
            request.setAttribute("error", "ユーザー登録に失敗しました。");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
            dispatcher.forward(request, response);
        }
    }
}