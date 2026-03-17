package task_management_system.com.task_management.controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.dto.UserDTO;

@WebServlet("/user/update")
public class UserUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        
      System.out.println("=== UserUpdateServlet ===");
      System.out.println("session loginUser = " + loginUser);

        if (session == null || session.getAttribute("loginUser") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\":false,\"message\":\"ログイン情報がありません。\"}");
            return;
        }

        request.setAttribute("loginUser", loginUser);

        String userName = request.getParameter("userName");
        String email = request.getParameter("email");

        if (userName == null) {
            userName = "";
        }
        if (email == null) {
            email = "";
        }

        userName = userName.trim();
        email = email.trim();

        if (userName.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"名前を入力してください。\"}");
            return;
        }

        if (email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"メールアドレスを入力してください。\"}");
            return;
        }

        UserDTO user = new UserDTO();
        user.setId(loginUser.getId());
        user.setUserName(userName);
        user.setEmail(email);

        UserDAO userDAO = new UserDAO();
        int result = userDAO.update(user);

        if (!(result > 0)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"更新に失敗しました。\"}");
            return;
        }

        UserDTO updatedUser = userDAO.getById(loginUser.getId());

        out.print("{");
        out.print("\"success\":true,");
        out.print("\"userName\":\"" + escapeJson(updatedUser.getUserName()) + "\",");
        out.print("\"email\":\"" + escapeJson(updatedUser.getEmail()) + "\"");
        out.print("}");
    }

    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }

        return str
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}