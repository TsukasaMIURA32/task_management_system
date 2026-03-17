package task_management_system.com.task_management.controller.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import task_management_system.com.task_management.dao.TaskDAO;
import task_management_system.com.task_management.dto.TaskDTO;
import task_management_system.com.task_management.dto.UserDTO;

@WebServlet("/task/detail")
public class TaskDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String taskIdStr = request.getParameter("taskId");

		if (taskIdStr == null || taskIdStr.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "taskIdがありません");
			return;
		}

		int taskId = Integer.parseInt(taskIdStr);

		TaskDAO taskDAO = new TaskDAO();
		TaskDTO task = taskDAO.getTaskById(taskId);

		if (task == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "タスクが見つかりません");
			return;
		}

		HttpSession session = request.getSession(false);
		UserDTO loginUser = null;
		if (session != null) {
			loginUser = (UserDTO) session.getAttribute("loginUser");
		}

		boolean isOwner = loginUser != null && task.getOwnerId() == loginUser.getId();

		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();

		out.print("{");
		out.print("\"id\":" + task.getId() + ",");
		out.print("\"title\":\"" + escapeJson(task.getTitle()) + "\",");
		out.print("\"content\":\"" + escapeJson(task.getContent()) + "\",");
		out.print("\"colorId\":" + task.getColorId() + ",");
		out.print("\"isOwner\":" + isOwner + ",");

		// 画像一覧
		out.print("\"imageIdList\":[");
		List<Integer> imageIdList = task.getImageIdList();
		if (imageIdList != null) {
			for (int i = 0; i < imageIdList.size(); i++) {
				out.print(imageIdList.get(i));
				if (i < imageIdList.size() - 1) {
					out.print(",");
				}
			}
		}
		out.print("],");

		// 共有ユーザー一覧
		out.print("\"sharedUsers\":[");
		List<UserDTO> sharedUserList = task.getSharedUserList();
		if (sharedUserList != null) {
			for (int i = 0; i < sharedUserList.size(); i++) {
				UserDTO user = sharedUserList.get(i);

				out.print("{");
				out.print("\"id\":" + user.getId() + ",");
				out.print("\"name\":\"" + escapeJson(user.getUserName()) + "\",");
				out.print("\"email\":\"" + escapeJson(user.getEmail()) + "\"");
				out.print("}");

				if (i < sharedUserList.size() - 1) {
					out.print(",");
				}
			}
		}
		out.print("]");

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