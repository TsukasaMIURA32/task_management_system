package task_management_system.com.task_management.controller.admin;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.dto.UserDTO;

/**
 * 管理画面のユーザー一覧表示用サーブレット
 */
@WebServlet(name="AdminUsersServlet", urlPatterns="/admin/users")
public class AdminUsersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /* =========================
           ログインチェック
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

        /* 管理者(role=1)以外は管理画面に入れない */
        if (loginUser.getRole() != 1) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        /* =========================
           パラメータ取得
        ========================= */
        String viewType = request.getParameter("viewType");
        String keyword = request.getParameter("keyword");

        if (viewType == null || viewType.isBlank()) {
            viewType = "registered";
        }

        UserDAO userDAO = new UserDAO();

        /* JSPで今どっちの一覧を表示しているか判定するため */
        request.setAttribute("viewType", viewType);

        /* =========================
           登録ユーザー一覧
        ========================= */
        if ("registered".equals(viewType)) {
            List<UserDTO> registeredUserList;

            /* 検索ワードがあるときは検索、ないときは一覧取得 */
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
	     //  常に申請中管理ユーザーを取得
	        List<UserDTO> pendingAdminList = userDAO.findUsersByRole(2);
	        request.setAttribute("pendingAdminList", pendingAdminList);
	        request.setAttribute("pendingCount", pendingAdminList.size());
	
	        // admin画面のときだけ管理者一覧を取得
	        if ("admin".equals(viewType)) {
	            List<UserDTO> adminUserList = userDAO.findUsersByRole(1);
	            request.setAttribute("adminUserList", adminUserList);
	        }
	        
	        int adminCount = userDAO.countAdminUsers();
	        request.setAttribute("adminCount", adminCount);
	        
        /* =========================
           申請却下済みユーザー一覧
        ========================= */
	     // 却下済みユーザー一覧画面のときだけ却下済みユーザー一覧を取得
	        if ("rejected".equals(viewType)) {
		        List<UserDTO> rejectedAdminList = userDAO.findUsersByRole(3);
		        request.setAttribute("rejectedAdminList", rejectedAdminList);
	        }

        /* =========================
           JSPへフォワード
        ========================= */
        RequestDispatcher rd = request.getRequestDispatcher("/admin-user-list.jsp");
        rd.forward(request, response);
    }
}