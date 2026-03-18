package task_management_system.com.task_management.controller.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.dto.UserDTO;

/**
 * Servlet implementation class DeleteAdminUserServlet
 */
@WebServlet(name="/AdminDeleteUserServlet", urlPatterns="/admin/delete-user")
public class DeleteAdminUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteAdminUserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* =========================
		   ログイン・権限チェック
		========================= */
		HttpSession session = request.getSession(false);

		if (session == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

		if (loginUser == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		if (loginUser.getRole() != 1) {
			response.sendRedirect(request.getContextPath() + "/dashboard");
			return;
		}

		/* =========================
		   パラメータ取得
		========================= */
		String userIdParam = request.getParameter("userId");

		if (userIdParam == null || userIdParam.isBlank()) {
			response.sendRedirect(request.getContextPath() + "/admin/users?viewType=registered");
			return;
		}

		int userId;
		try {
			userId = Integer.parseInt(userIdParam);
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/admin/users?viewType=registered");
			return;
		}

		UserDAO userDAO = new UserDAO();
		UserDTO targetUser = userDAO.getById(userId);

		if (targetUser == null) {
			response.sendRedirect(request.getContextPath() + "/admin/users?viewType=registered");
			return;
		}

		/* =========================
		   自分自身の削除防止
		========================= */
		if (loginUser.getId() == targetUser.getId()) {
			session.setAttribute("flashMessage", "自分自身は削除できません。");
			response.sendRedirect(request.getContextPath() + "/admin/users?viewType=admin");
			return;
		}

		/* =========================
		   最後の管理者は削除不可
		========================= */
		if (targetUser.getRole() == 1) {
			int adminCount = userDAO.countAdminUsers();

			if (adminCount <= 1) {
				session.setAttribute("flashMessage", "最後の管理者は削除できません。");
				response.sendRedirect(request.getContextPath() + "/admin/users?viewType=admin");
				return;
			}
		}

		/* =========================
		   削除実行
		========================= */
		userDAO.delete(userId);
		session.setAttribute("flashMessage", "ユーザーを削除しました。");

		if (targetUser.getRole() == 1) {
			response.sendRedirect(request.getContextPath() + "/admin/users?viewType=admin");
		} else {
			response.sendRedirect(request.getContextPath() + "/admin/users?viewType=registered");
		}
	}

}
