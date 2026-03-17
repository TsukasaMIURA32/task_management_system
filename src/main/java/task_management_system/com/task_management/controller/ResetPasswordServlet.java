package task_management_system.com.task_management.controller;


import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import task_management_system.com.task_management.dao.UserDAO;

@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
    }
   
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // 前後の空白を除去
        if (email != null) {
            email = email.trim();
        }
        if (newPassword != null) {
            newPassword = newPassword.trim();
        }
        if (confirmPassword != null) {
            confirmPassword = confirmPassword.trim();
        }

        // 未入力チェック
        if (email == null || email.isEmpty()
                || newPassword == null || newPassword.isEmpty()
                || confirmPassword == null || confirmPassword.isEmpty()) {

            request.setAttribute("error", "未入力の項目があります。");
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        // パスワード文字数チェック 
        if (newPassword.length() < 8) {
            request.setAttribute("error", "パスワードは8文字以上で入力してください。");
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        // パスワード一致チェック
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "確認用パスワードが一致しません。");
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        boolean result = dao.updatePasswordByEmail(email, newPassword);

        if (result) {
            response.sendRedirect(request.getContextPath() + "/resetComplete.jsp");
        } else {
            request.setAttribute("error", "メールアドレスが存在しないか、更新に失敗しました。");
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
        }
    }
}