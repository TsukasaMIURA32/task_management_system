package task_management_system.com.task_management.controller;

import java.io.IOException;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.dto.UserDTO;
import task_management_system.com.task_management.util.PasswordUtil;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // メールアドレス形式チェック
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // 8文字以上、数字を1文字以上、記号を1文字以上含む
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String userName = request.getParameter("userName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String roleStr = request.getParameter("role");

        if (userName != null) {
            userName = userName.trim();
        }
        if (email != null) {
            email = email.trim();
        }

        // 入力値保持
        request.setAttribute("userName", userName);
        request.setAttribute("email", email);
        request.setAttribute("role", roleStr);

        // 未入力チェック
        if (userName == null || userName.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {

            request.setAttribute("error", "未入力の項目があります。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // メール形式チェック
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            request.setAttribute("emailError", "メールアドレスの形式が正しくありません。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // パスワード形式チェック
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            request.setAttribute("error", "パスワードは8文字以上で、数字と記号を1文字以上含めてください。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // 確認用パスワード一致チェック
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "パスワードと確認用パスワードが一致しません。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // 権限設定
        int role = 0; // 一般ユーザー
        if ("admin".equals(roleStr)) {
            role = 2; // 管理ユーザーは承認待ち
        }

        UserDAO userDao = new UserDAO();

        // メール重複チェック
        if (userDao.existsByEmail(email)) {
            request.setAttribute("error", "このメールアドレスは既に登録されています。");

        UserDTO existingUser = userDao.findByEmail(email);

        if (existingUser != null) {
            if (existingUser.getRole() == 3) {
                request.setAttribute("error", "管理ユーザー申請が却下されています。詳細は管理者にお問い合わせください。");
            } else {
                request.setAttribute("error", "このメールアドレスは既に登録されています。");
            }

            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        try {
            // パスワードをハッシュ化
            String hashedPassword = PasswordUtil.hashPassword(password);

            UserDTO user = new UserDTO();
            user.setUserName(userName);
            user.setEmail(email);
            user.setPassword(hashedPassword);
            user.setRole(role);

            boolean result = userDao.insertUser(user);

            if (result) {
                response.sendRedirect(request.getContextPath() + "/registerComplete.jsp");
            } else {
                request.setAttribute("error", "ユーザー登録に失敗しました。");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "システムエラーが発生しました。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
}