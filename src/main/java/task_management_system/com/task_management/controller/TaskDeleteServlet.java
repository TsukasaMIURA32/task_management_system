package task_management_system.com.task_management.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import task_management_system.com.task_management.dao.TaskDAO;

@WebServlet(name = "TaskDeleteServlet", urlPatterns = "/task/delete")
public class TaskDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String taskIdStr = request.getParameter("taskId");

		if (taskIdStr == null || taskIdStr.isBlank()) {
			response.sendRedirect(request.getContextPath() + "/dashboard");
			return;
		}

		int taskId = Integer.parseInt(taskIdStr);

		TaskDAO taskDAO = new TaskDAO();
		taskDAO.delete(taskId);

		response.sendRedirect(request.getContextPath() + "/dashboard");
	}
}