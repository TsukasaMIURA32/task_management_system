package task_management_system.com.task_management.controller.task;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import task_management_system.com.task_management.dao.TaskUserDAO;
import task_management_system.com.task_management.dto.UserDTO;

/**
 * Servlet implementation class UserSearchServlet
 */
@WebServlet(name = "UserSearchServlet", urlPatterns = "/user/search")
public class UserSerachServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UserSerachServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String keyword = request.getParameter("keyword");

		if (keyword == null) {
			keyword = "";
		}

		TaskUserDAO taskUserDAO = new TaskUserDAO();
		List<UserDTO> userList = taskUserDAO.searchUsersByNameOrEmail(keyword);

		response.setContentType("application/json; charset=UTF-8");

		StringBuilder json = new StringBuilder();
		json.append("[");

		for (int i = 0; i < userList.size(); i++) {
			UserDTO user = userList.get(i);

			json.append("{")
				.append("\"id\":").append(user.getId()).append(",")
				.append("\"name\":\"").append(escapeJson(user.getUserName())).append("\",")
				.append("\"email\":\"").append(escapeJson(user.getEmail())).append("\"")
				.append("}");

			if (i < userList.size() - 1) {
				json.append(",");
			}
		}

		json.append("]");

		response.getWriter().write(json.toString());
	}

	private String escapeJson(String str) {
		if (str == null) {
			return "";
		}
		return str.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}