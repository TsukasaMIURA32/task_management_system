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
import task_management_system.com.task_management.dao.TaskUserDAO;
import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.dto.TaskDTO;

@WebServlet("/user/delete")
public class UserDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginUserId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (Integer) session.getAttribute("loginUserId");

        TaskDAO taskDAO = new TaskDAO();
        TaskUserDAO taskUserDAO = new TaskUserDAO();
        UserDAO userDAO = new UserDAO();

        // 退会ユーザーがownerのタスク一覧を取得
        List<TaskDTO> ownerTaskList = taskDAO.getTasksByOwnerId(userId);

        for (TaskDTO task : ownerTaskList) {
            int otherMemberCount = taskUserDAO.countOtherMembers(task.getId(), userId);

            if (otherMemberCount == 0) {
                // 他メンバーがいないならタスクごと削除
                // task_images と tasks_users も TaskDAO.delete() 内で削除される
                taskDAO.delete(task.getId());
            } else {
                // 他メンバーがいるなら owner を外してタスクは残す
                taskDAO.clearOwner(task.getId());
            }
        }

        // ユーザー削除
        // tasks_users の user_id 側は ON DELETE CASCADE で自動削除
        int result = userDAO.delete(userId);

        if (result > 0) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "退会処理に失敗しました。");
        }
    }
}