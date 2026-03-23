package task_management_system.com.task_management.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.util.PasswordUtil;

@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 8文字以上、数字を含む、記号を含む
    private static final String PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$";

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

        request.setAttribute("email", email);

        // 未入力チェック
        if (isBlank(email) || isBlank(newPassword) || isBlank(confirmPassword)) {
            request.setAttribute("error", "未入力の項目があります。");
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        // パスワード一致チェック
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "新しいパスワードと確認用パスワードが一致しません。");
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        // パスワード形式チェック
        if (!newPassword.matches(PASSWORD_REGEX)) {
            request.setAttribute("error", "パスワードは8文字以上で、数字と記号を含めてください。");
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();

        // メール存在チェック
        if (!userDAO.existsByEmail(email)) {
            request.setAttribute("error", "このメールアドレスは登録されていません。");
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        try {
            // ★パスワードをハッシュ化
            String hashedPassword = PasswordUtil.hashPassword(newPassword);

            // ★DAOにはハッシュを渡す
            boolean result = userDAO.updatePasswordByEmail(email, hashedPassword);

            if (result) {
                response.sendRedirect(request.getContextPath() + "/resetComplete.jsp");
            } else {
                request.setAttribute("error", "パスワードの更新に失敗しました。");
                request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "システムエラーが発生しました。");
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}