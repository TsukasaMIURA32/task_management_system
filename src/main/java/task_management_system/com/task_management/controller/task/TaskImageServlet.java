package task_management_system.com.task_management.controller.task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import task_management_system.com.task_management.util.DBCon;

/**
 * Servlet implementation class TaskImageServlet
 */
@WebServlet(name = "TaskImageServlet",urlPatterns = "/task/image")
public class TaskImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaskImageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String imageIdStr = request.getParameter("imageId");

		if (imageIdStr == null || imageIdStr.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		int imageId;
		try {
			imageId = Integer.parseInt(imageIdStr);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String sql = "SELECT file FROM task_images WHERE id = ?";

		try (Connection conn = DBCon.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, imageId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					byte[] imageData = rs.getBytes("file");

					if (imageData != null && imageData.length > 0) {
						response.setContentType(detectImageContentType(imageData));
						response.setContentLength(imageData.length);

						OutputStream out = response.getOutputStream();
						out.write(imageData);
						out.flush();
						return;
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private String detectImageContentType(byte[] data) {
		if (data.length >= 8
				&& (data[0] & 0xFF) == 0x89
				&& (data[1] & 0xFF) == 0x50
				&& (data[2] & 0xFF) == 0x4E
				&& (data[3] & 0xFF) == 0x47) {
			return "image/png";
		}

		if (data.length >= 3
				&& (data[0] & 0xFF) == 0xFF
				&& (data[1] & 0xFF) == 0xD8
				&& (data[2] & 0xFF) == 0xFF) {
			return "image/jpeg";
		}

		if (data.length >= 6) {
			String header = new String(data, 0, 6, StandardCharsets.US_ASCII);
			if ("GIF87a".equals(header) || "GIF89a".equals(header)) {
				return "image/gif";
			}
		}

		return "application/octet-stream";
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
