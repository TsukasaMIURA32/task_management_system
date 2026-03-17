package task_management_system.com.task_management.controller;


import java.io.IOException;

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

    // 画面表示
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    // 登録処理
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String userName = request.getParameter("userName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        // role変換
        int roleValue = 0;

        if ("admin".equals(role)) {
            roleValue = 2; // 承認待ち
        } else {
            roleValue = 0; // 一般ユーザー
        }

        UserDAO userDAO = new UserDAO();

        // メール重複チェック
        if (userDAO.existsByEmail(email)) {
            request.setAttribute("error", "このメールアドレスは既に登録されています。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // DTO作成
        UserDTO user = new UserDTO();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(roleValue);

        // DB登録
        boolean result = userDAO.insertUser(user);

        if (result) {
            response.sendRedirect(request.getContextPath() + "/registerComplete.jsp");
        } else {
            request.setAttribute("error", "ユーザー登録に失敗しました。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}