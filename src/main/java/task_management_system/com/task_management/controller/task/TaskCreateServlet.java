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

@WebServlet(name = "TaskCreateServlet", urlPatterns = "/task/create")
@MultipartConfig
public class TaskCreateServlet extends HttpServlet {
    private TaskDAO taskDAO = new TaskDAO();
    private TaskUserDAO taskUserDAO = new TaskUserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

        System.out.println("=== TaskCreateServlet ===");
        System.out.println("session loginUser = " + loginUser);

//        if (loginUser != null) {
//            System.out.println("loginUser id = " + loginUser.getId());
//            System.out.println("loginUser name = " + loginUser.getUserName());
//            System.out.println("loginUser email = " + loginUser.getEmail());
//        }
		
		request.setAttribute("loginUser", loginUser);

        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String colorIdStr = request.getParameter("colorId");
        String[] sharedUserIds = request.getParameterValues("sharedUserIds");

        int colorId = 1;
        if (colorIdStr != null && !colorIdStr.isBlank()) {
            colorId = Integer.parseInt(colorIdStr);
        }

        TaskDTO dto = new TaskDTO();
        dto.setOwnerId(loginUser.getId());
//        System.out.println("dto ownerId = " + dto.getOwnerId());
        dto.setTitle(title);
        dto.setContent(content);
        dto.setOwnerId(loginUser.getId());
        dto.setColorId(colorId);

        // 1. tasks 登録
        int taskId = taskDAO.insert(dto);

        if (taskId > 0) {
            // 2. tasks_users 登録
            taskUserDAO.insertTaskUsers(taskId, sharedUserIds, loginUser.getId());

            // 3. 画像登録
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                if ("image".equals(part.getName()) && part.getSize() > 0) {
                    try (InputStream is = part.getInputStream()) {
                        taskDAO.insertTaskImage(taskId, is);
                    }
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}