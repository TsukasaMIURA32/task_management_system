package task_management_system.com.task_management.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import task_management_system.com.task_management.dto.TaskImageDTO;
import task_management_system.com.task_management.util.DBCon;

public class TaskImageDAO {

	public TaskImageDTO getById(int imageId) {
		String sql = "SELECT * FROM task_images WHERE id = ?";
		TaskImageDTO dto = null;

		try (Connection conn = DBCon.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, imageId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					dto = new TaskImageDTO();
					dto.setId(rs.getInt("id"));
					dto.setTaskId(rs.getInt("task_id"));
					dto.setImagePath(rs.getString("image_path"));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dto;
	}

	public int deleteByIdAndTaskId(int imageId, int taskId) {
		String sql = "DELETE FROM task_images WHERE id = ? AND task_id = ?";
		int result = 0;

		try (Connection conn = DBCon.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, imageId);
			ps.setInt(2, taskId);
			result = ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public int insert(TaskImageDTO dto) {
		String sql = "INSERT INTO task_images (task_id, image_path, created_at) VALUES (?, ?, NOW())";
		int result = 0;

		try (Connection conn = DBCon.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, dto.getTaskId());
			ps.setString(2, dto.getImagePath());
			result = ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public int countByTaskId(int taskId) {
		String sql = "SELECT COUNT(*) AS cnt FROM task_images WHERE task_id = ?";
		int count = 0;

		try (Connection conn = DBCon.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, taskId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					count = rs.getInt("cnt");
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}
}