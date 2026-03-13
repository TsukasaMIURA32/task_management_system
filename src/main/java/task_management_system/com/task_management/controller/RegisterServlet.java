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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String userName = request.getParameter("userName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String roleParam = request.getParameter("role");

        // 入力チェック
        if (userName == null || userName.isEmpty() ||
            email == null || email.isEmpty() ||
            password == null || password.isEmpty()) {

            request.setAttribute("error", "すべての項目を入力してください。");
            RequestDispatcher rd = request.getRequestDispatcher("/register.jsp");
            rd.forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();

        // メール重複チェック
        if (userDAO.existsByEmail(email)) {
            request.setAttribute("error", "このメールアドレスは既に登録されています。");
            RequestDispatcher rd = request.getRequestDispatcher("/register.jsp");
            rd.forward(request, response);
            return;
        }

        // DTO作成
        UserDTO user = new UserDTO();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(password);

        // ユーザー権限チェック
        if ("user".equals(roleParam)) {
            user.setRole("user");
        } else if ("admin".equals(roleParam)) {
            user.setRole("pending_admin");
        }

        // DB登録
        boolean result = userDAO.insertUser(user);

        if (result) {
            response.sendRedirect(request.getContextPath() + "/registerComplete.jsp");
        } else {
            request.setAttribute("error", "登録に失敗しました。");
            RequestDispatcher rd = request.getRequestDispatcher("/register.jsp");
            rd.forward(request, response);
        }
    }
}