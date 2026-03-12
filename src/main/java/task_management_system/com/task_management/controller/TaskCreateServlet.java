package task_management_system.com.task_management.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import task_management_system.com.task_management.dao.TaskDAO;
import task_management_system.com.task_management.dto.TaskDTO;

/**
 * Servlet implementation class TaskCreateServlet
 */
@WebServlet(name = "TaskCreateServlet",urlPatterns = "/task/create")
@MultipartConfig
public class TaskCreateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaskCreateServlet() {
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

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();

		// 仮ログイン用
		if (session.getAttribute("loginUserId") == null) {
			session.setAttribute("loginUserId", 1);
		}

		Integer loginUserId = (Integer) session.getAttribute("loginUserId");

		if (loginUserId == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String colorIdStr = request.getParameter("colorId");

		Collection<Part> parts = request.getParts();
		List<Part> imageParts = new ArrayList<>();
		
		System.out.println("parts size = " + parts.size());

		for (Part part : parts) {
			System.out.println("part name = " + part.getName() + ", size = " + part.getSize());
			if ("image".equals(part.getName()) && part.getSize() > 0) {
				imageParts.add(part);
			}
		}

		System.out.println("imageParts size = " + imageParts.size());
		
		if (imageParts.size() > 4) {
			imageParts = imageParts.subList(0, 4);
		}

		boolean hasTitle = title != null && !title.trim().isEmpty();
		boolean hasContent = content != null && !content.trim().isEmpty();
		boolean hasImage = !imageParts.isEmpty();

		// 全部空なら保存しない
		if (!hasTitle && !hasContent && !hasImage) {
			response.sendRedirect(request.getContextPath() + "/dashboard");
			return;
		}

		int colorId = 1;
		if (colorIdStr != null && !colorIdStr.isEmpty()) {
			try {
				colorId = Integer.parseInt(colorIdStr);
			} catch (NumberFormatException e) {
				colorId = 1;
			}
		}

		TaskDTO dto = new TaskDTO();
		dto.setTitle(title);
		dto.setContent(content);
		dto.setOwnerId(loginUserId);
		dto.setColorId(colorId);

		TaskDAO dao = new TaskDAO();

		int taskId = dao.insert(dto);

		if (taskId > 0) {
			for (Part imagePart : imageParts) {
				try (InputStream inputStream = imagePart.getInputStream()) {
					dao.insertTaskImage(taskId, inputStream);
				}
			}
		}

		response.sendRedirect(request.getContextPath() + "/dashboard");
	}
}
