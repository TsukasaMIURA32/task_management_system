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
import task_management_system.com.task_management.util.PasswordUtil;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginServlet() {
        super();
    }

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
     // メールでユーザー取得（パスワードはまだ見ない）
        UserDTO user = userDAO.findByEmail(email);

        if (user == null) {
            request.setAttribute("error", "メールアドレスまたはパスワードが違います。");
            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
            rd.forward(request, response);
            return;
        }

        String dbPassword = user.getPassword();
        boolean loginSuccess = false;

        // ① ハッシュ済みパスワードの場合
        if (isHashedPassword(dbPassword)) {
            loginSuccess = PasswordUtil.matches(password, dbPassword);
        }
        // ② 旧ユーザーの平文パスワードの場合
        else {
            if (password.equals(dbPassword)) {
                loginSuccess = true;

                // 初回ログイン成功時にハッシュ化してDB更新
                String hashedPassword = PasswordUtil.hashPassword(password);
                boolean updated = userDAO.updatePasswordByEmail(email, hashedPassword);

                if (updated) {
                    // セッションに入るUserDTOも最新状態にしておく
                    user.setPassword(hashedPassword);
                } else {
                    request.setAttribute("error", "パスワード移行処理に失敗しました。");
                    RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
                    rd.forward(request, response);
                    return;
                }
            }
        }

        if (!loginSuccess) {
            request.setAttribute("error", "メールアドレスまたはパスワードが違います。");
            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
            rd.forward(request, response);
            return;
        }
        

        // セッション保存前にパスワードは消す
        user.setPassword(null);
      // ===== セッション保存 =====
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);
//        System.out.println("91行目"+user);
        // ===== 画面振り分け =====
        
        if(user.getRole() == 2) {

        }
        
        if (user.getRole() == 1) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } else if(user.getRole() ==2){
//        	System.out.println("2に入った！");
        	request.setAttribute("error", "管理ユーザー申請の承認待ちです。詳細は管理者にお問い合わせください。");
	        request.getRequestDispatcher("/login.jsp").forward(request, response);
	        return;
        }else if(user.getRole() == 3) {
//        	System.out.println("3に入った！");
        	request.setAttribute("error", "管理ユーザー申請が却下されています。詳細は管理者にお問い合わせください。");
	        request.getRequestDispatcher("/login.jsp").forward(request, response);
	        return;
        }else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    /**
     * bcrypt 形式のハッシュかどうかを判定
     */
    private boolean isHashedPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$");
    }
}