package task_management_system.com.task_management.controller.task;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import task_management_system.com.task_management.dao.TaskDAO;
import task_management_system.com.task_management.dao.TaskUserDAO;
import task_management_system.com.task_management.dto.TaskDTO;

@WebServlet(name = "TaskDeleteServlet", urlPatterns = "/task/delete")
public class TaskDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUserId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int loginUserId = (Integer) session.getAttribute("loginUserId");

        String taskIdStr = request.getParameter("taskId");
        if (taskIdStr == null || taskIdStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        int taskId = Integer.parseInt(taskIdStr);

        TaskDAO taskDAO = new TaskDAO();
        TaskUserDAO taskUserDAO = new TaskUserDAO();

        TaskDTO task = taskDAO.getTaskById(taskId);
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // ownerならタスク自体を削除
        if (task.getOwnerId() == loginUserId) {
            taskDAO.delete(taskId);
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // ownerではないが共有メンバーなら、自分だけ外す
        boolean isMember = taskUserDAO.existsTaskUser(taskId, loginUserId);
        if (isMember) {
            taskUserDAO.deleteTaskUser(taskId, loginUserId);
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // どちらでもなければ権限なし
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "このタスクを削除する権限がありません。");
    }
}