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

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		// 仮ログイン用（あとで削除）
		if (session.getAttribute("loginUserId") == null) {
			session.setAttribute("loginUserId", 1);
		}

		Integer loginUserId = (Integer) session.getAttribute("loginUserId");

		if (loginUserId == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		TaskDAO dao = new TaskDAO();
		List<TaskDTO> taskList = dao.getTasksByUserId(loginUserId);

		request.setAttribute("taskList", taskList);
//		System.out.println("taskList size = " + taskList.size());

		RequestDispatcher dispatcher = request.getRequestDispatcher("dashboard.jsp");
		dispatcher.forward(request, response);
	}
}