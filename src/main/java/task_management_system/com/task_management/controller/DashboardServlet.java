package task_management_system.com.task_management.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import task_management_system.com.task_management.dao.TaskDAO;
import task_management_system.com.task_management.dto.TaskDTO;
import task_management_system.com.task_management.dto.UserDTO;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		System.out.println("dashboard");
		HttpSession session = request.getSession(false);
		UserDTO loginUser = null;

		if (session != null) {
		    loginUser = (UserDTO) session.getAttribute("loginUser");
		}

		if (loginUser == null) {
		    response.sendRedirect(request.getContextPath() + "/login.jsp");
		    return;
		}
		int loginUserId = loginUser.getId();
//		System.out.println("Login success user = " + loginUser);
//		System.out.println("session id at login = " + request.getSession().getId());
				
		request.setAttribute("loginUser", loginUser);

		TaskDAO dao = new TaskDAO();
		List<TaskDTO> taskList = dao.getTasksByUserId(loginUserId);
//		List<TaskDTO> taskList = dao.getTasksByOwnerId(loginUserId);

		request.setAttribute("taskList", taskList);
//		System.out.println("taskList size = " + taskList.size());

		RequestDispatcher dispatcher = request.getRequestDispatcher("/dashboard.jsp");
		dispatcher.forward(request, response);
	}
}