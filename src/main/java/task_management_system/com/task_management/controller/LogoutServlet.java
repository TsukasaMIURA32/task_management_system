package task_management_system.com.task_management.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import task_management_system.com.task_management.dto.UserDTO;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	HttpSession session = request.getSession();
		UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
		  // ログインユーザーが取得できない場合もログイン画面へ
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if(loginUser.getRole() ==0) {
        	request.getRequestDispatcher("/dashboard").forward(request, response);
        	return;
        }else if(loginUser.getRole()== 1) {
        	request.getRequestDispatcher("/admin/users").forward(request, response);
        	return;
        }else {
        	response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/logoutComplete.jsp");
    }
}