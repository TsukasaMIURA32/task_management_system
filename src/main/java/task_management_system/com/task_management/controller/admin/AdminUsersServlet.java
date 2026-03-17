package task_management_system.com.task_management.controller.admin;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.dto.UserDTO;

/**
 * ユーザー管理画面の一覧表示を行うサーブレット
 * 
 * viewType=registered
 *   → 一般ユーザー一覧(role=0)を表示
 * 
 * viewType=admin
 *   → 管理者申請中一覧(role=2) + 管理ユーザー一覧(role=1)を表示
 */
@WebServlet(name="/AdminUsersServlet", urlPatterns="/admin/users")
public class AdminUsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminUsersServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* 画面の表示切り替え用パラメータ */
		String viewType = request.getParameter("viewType");

		/* 未指定なら一般ユーザー一覧を表示 */
		if (viewType == null || viewType.isBlank()) {
			viewType = "registered";
		}

		UserDAO userDAO = new UserDAO();

		/* JSP側でサイドバーの active 判定などに使う */
		request.setAttribute("viewType", viewType);

		/* =========================
		   一般ユーザー一覧
		========================= */
		if ("registered".equals(viewType)) {
			String keyword = request.getParameter("keyword");

			/* 入力値を検索欄に戻すために保持 */
			request.setAttribute("keyword", keyword);

			List<UserDTO> registeredUserList;

			if (keyword != null && !keyword.isBlank()) {
				registeredUserList = userDAO.searchUsersByRoleAndKeyword(0, keyword);
			} else {
				registeredUserList = userDAO.findUsersByRole(0);
			}

			request.setAttribute("registeredUserList", registeredUserList);
		}

		/* =========================
		   管理ユーザー一覧
		========================= */
		if ("admin".equals(viewType)) {
			List<UserDTO> pendingAdminList = userDAO.findUsersByRole(2);
			List<UserDTO> adminUserList = userDAO.findUsersByRole(1);

			request.setAttribute("pendingAdminList", pendingAdminList);
			request.setAttribute("adminUserList", adminUserList);
		}

		request.getRequestDispatcher("/admin-user-list.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
