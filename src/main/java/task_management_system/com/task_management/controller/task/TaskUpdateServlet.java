package task_management_system.com.task_management.controller.task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import task_management_system.com.task_management.dao.TaskDAO;
import task_management_system.com.task_management.dao.TaskUserDAO;
import task_management_system.com.task_management.dto.TaskDTO;
import task_management_system.com.task_management.dto.UserDTO;

@WebServlet(name = "TaskUpdateServlet", urlPatterns = "/task/update")
@MultipartConfig
public class TaskUpdateServlet extends HttpServlet {
    private TaskDAO taskDAO = new TaskDAO();
    private TaskUserDAO taskUserDAO = new TaskUserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String taskIdStr = request.getParameter("taskId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String colorIdStr = request.getParameter("colorId");
        String[] sharedUserIds = request.getParameterValues("sharedUserIds");
        String deleteImageIds = request.getParameter("deleteImageIds");

        int taskId = Integer.parseInt(taskIdStr);

        int colorId = 1;
        if (colorIdStr != null && !colorIdStr.isBlank()) {
            colorId = Integer.parseInt(colorIdStr);
        }

        TaskDTO dto = new TaskDTO();
        dto.setId(taskId);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setColorId(colorId);

        // 1. tasks 更新
        int updateResult = taskDAO.update(dto);

        if (updateResult > 0) {
            // 2. tasks_users を全削除して再登録
            taskUserDAO.deleteTaskUsersByTaskId(taskId);
            taskUserDAO.insertTaskUsers(taskId, sharedUserIds, loginUser.getId());

            // 3. 削除対象画像を削除
            if (deleteImageIds != null && !deleteImageIds.isBlank()) {
                String[] imageIdArray = deleteImageIds.split(",");

                for (String imageIdStr : imageIdArray) {
                    if (imageIdStr != null && !imageIdStr.isBlank()) {
                        int imageId = Integer.parseInt(imageIdStr.trim());
                        taskDAO.deleteTaskImageById(imageId);
                    }
                }
            }

            // 4. 新規画像追加
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                if ("newImages".equals(part.getName()) && part.getSize() > 0) {
                    try (InputStream is = part.getInputStream()) {
                        taskDAO.insertTaskImage(taskId, is);
                    }
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}