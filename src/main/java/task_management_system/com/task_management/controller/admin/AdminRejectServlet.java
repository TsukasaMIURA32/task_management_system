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
 * 管理者申請却下処理サーブレット
 */
@WebServlet(name="/AdminRejectServlet", urlPatterns="/admin/reject")
public class AdminRejectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminRejectServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
	 * POST：申請却下処理
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* =========================
		   ログイン・権限チェック
		========================= */
		HttpSession session = request.getSession(false);

		// セッションなし → ログイン画面へ
		if (session == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

		// ログイン情報なし → ログイン画面へ
		if (loginUser == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		/* 管理者(role=1)以外は実行不可 */
		if (loginUser.getRole() != 1) {
			response.sendRedirect(request.getContextPath() + "/dashboard");
			return;
		}

		/* =========================
		   パラメータ取得
		========================= */
		String userIdParam = request.getParameter("userId");

		// userIdが取得できない場合
		if (userIdParam == null || userIdParam.isBlank()) {
			response.sendRedirect(request.getContextPath() + "/admin/users?viewType=admin");
			return;
		}

		int userId;

		try {
			userId = Integer.parseInt(userIdParam);
		} catch (NumberFormatException e) {
			// 不正なID
			response.sendRedirect(request.getContextPath() + "/admin/users?viewType=admin");
			return;
		}

		/* =========================
		   role更新
		   申請中(2) → 却下済みユーザー(3)
		========================= */
		UserDAO userDAO = new UserDAO();
		userDAO.updateRole(userId, 3);

		/* =========================
		   一覧へ戻す
		========================= */
		response.sendRedirect(request.getContextPath() + "/admin/users?viewType=admin");
	}
}
