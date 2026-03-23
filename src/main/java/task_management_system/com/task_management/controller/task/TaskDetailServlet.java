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
import task_management_system.com.task_management.dao.TaskUserDAO;
import task_management_system.com.task_management.dto.TaskDTO;
import task_management_system.com.task_management.dto.UserDTO;

@WebServlet(name = "TaskDetailServlet", urlPatterns = "/task/detail")
public class TaskDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private TaskDAO taskDAO = new TaskDAO();
	private TaskUserDAO taskUserDAO = new TaskUserDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* =========================
		   セッション・ログインチェック
		========================= */
		HttpSession session = request.getSession(false);

		if (session == null) {
			writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "ログイン情報がありません。");
			return;
		}

		UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

		if (loginUser == null) {
			writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "ログイン情報がありません。");
			return;
		}

		/* =========================
		   パラメータ取得
		========================= */
		String taskIdStr = request.getParameter("taskId");

		if (taskIdStr == null || taskIdStr.isBlank()) {
			writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "taskIdがありません。");
			return;
		}

		/* =========================
		   バリデーション
		========================= */
		int taskId;

		try {
			taskId = Integer.parseInt(taskIdStr);
		} catch (NumberFormatException e) {
			writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "不正なtaskIdです。");
			return;
		}

		/* =========================
		   閲覧権限チェック
		   tasks_users に存在するユーザーのみ閲覧可
		========================= */
		boolean canView = taskUserDAO.existsTaskUser(taskId, loginUser.getId());

		if (!canView) {
			writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, "このタスクを閲覧する権限がありません。");
			return;
		}

		try {
			/* =========================
			   タスク取得
			========================= */
			TaskDTO task = taskDAO.getTaskById(taskId);

			if (task == null) {
				writeJsonError(response, HttpServletResponse.SC_NOT_FOUND, "タスクが見つかりません。");
				return;
			}

			/* =========================
			   ログインユーザーがownerか判定
			========================= */
			boolean isOwner = task.getOwnerId() == loginUser.getId();

			/* =========================
			   JSONレスポンス返却
			========================= */
			response.setContentType("application/json; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			out.print("{");
			out.print("\"id\":" + task.getId() + ",");
			out.print("\"title\":\"" + escapeJson(task.getTitle()) + "\",");
			out.print("\"content\":\"" + escapeJson(task.getContent()) + "\",");
			out.print("\"colorId\":" + task.getColorId() + ",");
			out.print("\"isOwner\":" + isOwner + ",");
			out.print("\"updatedAt\":\"" + escapeJson(task.getUpdatedAt()) + "\",");

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

		} catch (Exception e) {
			/* =========================
			   想定外エラー
			========================= */
			e.printStackTrace();
			throw new ServletException("タスク詳細取得処理でエラーが発生しました。", e);
		}
	}

	/**
	 * 想定内エラーをJSON形式で返す
	 */
	private void writeJsonError(HttpServletResponse response, int statusCode, String message) throws IOException {
		response.setStatus(statusCode);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		out.print("{");
		out.print("\"error\":\"" + escapeJson(message) + "\"");
		out.print("}");
	}

	/**
	 * JSON文字列用にエスケープする
	 */
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