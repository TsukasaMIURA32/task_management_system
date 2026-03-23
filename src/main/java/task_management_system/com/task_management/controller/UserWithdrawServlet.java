package task_management_system.com.task_management.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import task_management_system.com.task_management.dao.TaskDAO;
import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.dto.TaskDTO;
import task_management_system.com.task_management.dto.UserDTO;

@WebServlet(name="UserWithdrawServlet", urlPatterns="/user/withdraw")
public class UserWithdrawServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        UserDTO loginUser =  (UserDTO) session.getAttribute("loginUser");
        int userId = loginUser.getId();

        TaskDAO taskDAO = new TaskDAO();
//        TaskUserDAO taskUserDAO = new TaskUserDAO();
        UserDAO userDAO = new UserDAO();

        /* =========================
           退会対象ユーザー情報を取得
        ========================= */
        UserDTO targetUser = userDAO.getById(userId);

        if (targetUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        /* =========================
           最後の管理者は退会不可
        ========================= */
        if (targetUser.getRole() == 1) {
            int adminCount = userDAO.countAdminUsers();

            if (adminCount <= 1) {
            	String viewType = request.getParameter("viewType");
            	
            	if (viewType == null || viewType.isBlank()) {
            	    viewType = "registered"; // デフォルト
            	}
            	
                session.setAttribute("flashMessage", "最後の管理者は退会できません。");
                response.sendRedirect(
                	    request.getContextPath() + "/admin/users?viewType=" + viewType
                );
                return;
            }
        }

        /* =========================
           退会ユーザーがownerのタスク一覧を取得
        ========================= */
        List<TaskDTO> ownerTaskList = taskDAO.getTasksByOwnerId(userId);

        for (TaskDTO task : ownerTaskList) {
            // owner_id は NOT NULL のため、他メンバーがいてもタスクごと削除する
            // task_images と tasks_users も TaskDAO.delete() 内で削除される想定
            taskDAO.delete(task.getId());
        }

        /* =========================
           ユーザー削除
        ========================= */
        // tasks_users の user_id 側は ON DELETE CASCADE で自動削除
        int result = userDAO.delete(userId);

        if (result > 0) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "退会処理に失敗しました。");
        }
    }
}