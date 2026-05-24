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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int MAX_LOGIN_FAIL_COUNT = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        UserDTO user = userDAO.findByEmail(email);

        if (user == null) {
            request.setAttribute("error", "入力されたメールアドレスは登録されていません。");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        if (user.isAccountLocked()) {
            request.setAttribute("error", "このアカウントはロックされています。管理者にお問い合わせください。");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        boolean loginSuccess = password.equals(user.getPassword());

        if (!loginSuccess) {
            userDAO.incrementLoginFailCount(user.getId());

            int failCount = user.getLoginFailCount() + 1;

            if (failCount >= MAX_LOGIN_FAIL_COUNT) {
                userDAO.lockAccount(user.getId());
                request.setAttribute("error", "ログインに5回失敗したため、アカウントがロックされました。");
            } else {
                request.setAttribute("error",
                        "メールアドレスまたはパスワードが違います。"
                        + "（" + failCount + "回失敗 / " + MAX_LOGIN_FAIL_COUNT + "回でロック）");
            }

            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        userDAO.resetLoginFailCount(user.getId());

        if (user.getRole() == 2) {
            request.setAttribute("error", "管理ユーザーは現在承認待ちです。");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        if (user.getRole() == 3) {
            request.setAttribute("error", "管理ユーザー申請が却下されています。詳細は管理者にお問い合わせください。");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        user.setPassword(null);

        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);

        if (user.getRole() == 1) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}