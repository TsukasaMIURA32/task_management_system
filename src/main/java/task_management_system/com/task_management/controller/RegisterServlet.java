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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 8文字以上、数字を1文字以上、記号を1文字以上含む
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/register.jsp")
               .forward(request, response);
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

        if (userName == null || userName.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {

            request.setAttribute("error", "未入力の項目があります。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            request.setAttribute("error", "パスワードは8文字以上で、数字と記号を1文字以上含めてください。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "パスワードと確認用パスワードが一致しません。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        int role = 0;
        if ("admin".equals(roleStr)) {
            role = 2; // 管理ユーザーは承認待ち
        }

        UserDAO userDao = new UserDAO();

        if (userDao.existsByEmail(email)) {
            request.setAttribute("error", "このメールアドレスは既に登録されています。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        UserDTO user = new UserDTO();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        boolean result = userDao.insertUser(user);

        if (result) {
            response.sendRedirect(request.getContextPath() + "/registerComplete.jsp");
        } else {
            request.setAttribute("error", "ユーザー登録に失敗しました。");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}