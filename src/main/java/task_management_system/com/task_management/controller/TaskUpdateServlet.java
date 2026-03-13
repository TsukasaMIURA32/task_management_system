package task_management_system.com.task_management.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import task_management_system.com.task_management.dao.TaskDAO;
import task_management_system.com.task_management.dto.TaskDTO;

/**
 * Servlet implementation class TaskUpdateServlet
 */
@WebServlet(name = "TaskUpdateServlet", urlPatterns = "/task/update")
@MultipartConfig
public class TaskUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaskUpdateServlet() {
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

		request.setCharacterEncoding("UTF-8");

		String taskIdStr = request.getParameter("taskId");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String colorIdStr = request.getParameter("colorId");
		String deleteImageIds = request.getParameter("deleteImageIds");

		if (taskIdStr == null || taskIdStr.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/dashboard");
			return;
		}

		int taskId = Integer.parseInt(taskIdStr);
		int colorId = (colorIdStr == null || colorIdStr.isEmpty()) ? 1 : Integer.parseInt(colorIdStr);

		TaskDTO dto = new TaskDTO();
		dto.setId(taskId);
		dto.setTitle(title);
		dto.setContent(content);
		dto.setColorId(colorId);

		TaskDAO taskDAO = new TaskDAO();

		// tasks テーブル更新
		taskDAO.update(dto);

		// 削除対象画像があれば削除
		if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
			String[] imageIdArray = deleteImageIds.split(",");

			for (String imageIdStr : imageIdArray) {
				if (imageIdStr != null && !imageIdStr.trim().isEmpty()) {
					taskDAO.deleteTaskImageById(Integer.parseInt(imageIdStr.trim()));
				}
			}
		}

		// 新規画像追加
		Collection<Part> parts = request.getParts();
		for (Part part : parts) {
			if ("newImages".equals(part.getName()) && part.getSize() > 0) {
				try (InputStream is = part.getInputStream()) {
					taskDAO.insertTaskImage(taskId, is);
				}
			}
		}

		response.sendRedirect(request.getContextPath() + "/dashboard");
	}
}

