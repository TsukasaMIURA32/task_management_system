package task_management_system.com.task_management.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import task_management_system.com.task_management.dto.TaskDTO;
import task_management_system.com.task_management.util.DBCon;

public class TaskDAO extends BaseDAO<TaskDTO> {

	@Override
	protected String getTableName() {
		return "tasks";
	}

	@Override
	protected TaskDTO mapRow(ResultSet rs) throws SQLException {
		TaskDTO dto = new TaskDTO();
		dto.setId(rs.getInt("id"));
		dto.setTitle(rs.getString("title"));
		dto.setContent(rs.getString("content"));
		dto.setOwnerId(rs.getInt("owner_id"));
		dto.setColorId(rs.getInt("color_id"));
		dto.setCreatedAt(rs.getString("created_at"));
		dto.setUpdatedAt(rs.getString("updated_at"));
		return dto;
	}
	// 登録して生成されたIDを返す
	@Override
	public int insert(TaskDTO dto) {
		String sql = "INSERT INTO tasks (title, content, owner_id, color_id, created_at, updated_at) "
				+ "VALUES (?, ?, ?, ?, NOW(), NOW())";

		int taskId = 0;

		try (Connection conn = DBCon.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, dto.getTitle());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getOwnerId());
			pstmt.setInt(4, dto.getColorId());

			int result = pstmt.executeUpdate();

			if (result > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						taskId = rs.getInt(1);
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return taskId;

	}

	@Override
	public int update(TaskDTO dto) {
		String sql = "UPDATE tasks "
				+ "SET title = ?, content = ?, color_id = ?, updated_at = NOW() "
				+ "WHERE id = ?";
		int result = 0;

		try (Connection conn = DBCon.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, dto.getTitle());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getColorId());
			pstmt.setInt(4, dto.getId());

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public List<TaskDTO> getTasksByUserId(int userId) {
		List<TaskDTO> taskList = new ArrayList<>();

		String sql = "SELECT DISTINCT t.* "
				+ "FROM tasks t "
				+ "LEFT JOIN tasks_users tu ON t.id = tu.task_id "
				+ "WHERE t.owner_id = ? OR tu.user_id = ? "
				+ "ORDER BY t.updated_at DESC";

		try (Connection conn = DBCon.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, userId);
			pstmt.setInt(2, userId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					TaskDTO dto = mapRow(rs);

					List<Integer> imageIdList = getImageIdsByTaskId(conn, dto.getId());
					dto.setImageIdList(imageIdList);
					dto.setHasImage(!imageIdList.isEmpty());

					taskList.add(dto);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return taskList;
	}
	
	private List<Integer> getImageIdsByTaskId(Connection conn, int taskId) throws SQLException {
		List<Integer> imageIdList = new ArrayList<>();

		String sql = "SELECT id FROM task_images WHERE task_id = ? ORDER BY id ASC";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, taskId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					imageIdList.add(rs.getInt("id"));
				}
			}
		}

		return imageIdList;
	}
	
	public int insertTaskImage(int taskId, InputStream imageInputStream) {
		String sql = "INSERT INTO task_images (task_id, file, created_at) VALUES (?, ?, NOW())";
		int result = 0;

		try (Connection conn = DBCon.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, taskId);
			pstmt.setBlob(2, imageInputStream);

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
	
}