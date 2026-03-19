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

@WebServlet(name="LoginServlet", urlPatterns="/login")
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
        UserDTO loginUser = dao.login(email, password);

        // ===== ログイン失敗 =====
        if (loginUser == null) {
            request.setAttribute("error", "メールアドレスまたはパスワードが違います。");
            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
            rd.forward(request, response);
            return;
        }

        // ===== 管理者申請中 =====
        if (loginUser.getRole() == 2) {
            request.setAttribute("error", "管理ユーザーは現在承認待ちです。");
            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
            rd.forward(request, response);
            return;
        }
        
        // ===== 管理者申請中 =====
        if (loginUser.getRole() == 3) {
            request.setAttribute("error", "管理ユーザー申請が却下されています。詳細は管理者にお問い合わせください。");
            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
            rd.forward(request, response);
            return;
        }

        // ===== セッション保存 =====
        HttpSession session = request.getSession();
        loginUser.setPassword(null);
        session.setAttribute("loginUser", loginUser);

        // ===== 画面振り分け =====
        if (loginUser.getRole() == 1) {
            // 管理者
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } else {
            // 一般ユーザー
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}
