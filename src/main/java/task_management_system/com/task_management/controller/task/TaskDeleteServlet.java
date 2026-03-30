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
import task_management_system.com.task_management.dto.UserDTO;

@WebServlet(name = "TaskDeleteServlet", urlPatterns = "/task/delete")
public class TaskDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UserDTO loginUser = null;

        if (session != null) {
            loginUser = (UserDTO) session.getAttribute("loginUser");
        }

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (loginUser.getRole() == 0) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        } else if (loginUser.getRole() == 1) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        UserDTO loginUser = null;

        if (session != null) {
            loginUser = (UserDTO) session.getAttribute("loginUser");
        }

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int loginUserId = loginUser.getId();

        String taskIdStr = request.getParameter("taskId");
        if (taskIdStr == null || taskIdStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        int taskId;
        try {
            taskId = Integer.parseInt(taskIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

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

        // ownerではないが共有メンバーなら、自分だけ共有解除
        boolean isMember = taskUserDAO.existsTaskUser(taskId, loginUserId);
        if (isMember) {
            taskUserDAO.deleteTaskUser(taskId, loginUserId);
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // 権限なしでも画面は dashboard に戻す
        session.setAttribute("flashMessage", "このタスクを削除する権限がありません。");
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
}