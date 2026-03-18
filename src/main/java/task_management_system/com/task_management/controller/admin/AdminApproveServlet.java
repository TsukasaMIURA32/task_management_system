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
 * Servlet implementation class AdminApproveServlet
 */
@WebServlet(name="/AdminApproveServlet", urlPatterns="/admin/approve")
public class AdminApproveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminApproveServlet() {
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
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

        /* 管理者(role=1)以外は実行不可 */
        if (loginUser.getRole() != 1) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        /* =========================
           パラメータ取得
        ========================= */
        String userIdParam = request.getParameter("userId");

        if (userIdParam == null || userIdParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/users?viewType=admin");
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?viewType=admin");
            return;
        }

        /* =========================
           role更新
           申請中(2) → 管理者(1)
        ========================= */
        UserDAO userDAO = new UserDAO();
        userDAO.updateRole(userId, 1);

        /* =========================
           一覧へ戻す
        ========================= */
        response.sendRedirect(request.getContextPath() + "/admin/users?viewType=admin");
    }
}
