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
import task_management_system.com.task_management.dto.UserDTO;
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

		String sql = "SELECT t.* "
				+ "FROM tasks t "
				+ "INNER JOIN tasks_users tu ON t.id = tu.task_id "
				+ "WHERE tu.user_id = ? "
				+ "ORDER BY t.created_at DESC";

		try (Connection conn = DBCon.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, userId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					TaskDTO dto = mapRow(rs);

					List<Integer> imageIdList = getImageIdsByTaskId(conn, dto.getId());
					dto.setImageIdList(imageIdList);
					dto.setHasImage(!imageIdList.isEmpty());

					// ログイン中ユーザー以外の共有ユーザーをセット
					List<UserDTO> sharedUserList = getSharedUsersByTaskId(conn, dto.getId(), userId);
					dto.setSharedUserList(sharedUserList);

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

	public int deleteTaskImageById(int imageId) {
		String sql = "DELETE FROM task_images WHERE id = ?";
		int result = 0;

		try (Connection conn = DBCon.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, imageId);
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public TaskDTO getTaskById(int taskId) {
		TaskDTO dto = null;

		String sql = "SELECT * FROM tasks WHERE id = ?";

		try (Connection conn = DBCon.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, taskId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					dto = mapRow(rs);

					List<Integer> imageIdList = getImageIdsByTaskId(conn, dto.getId());
					dto.setImageIdList(imageIdList);
					dto.setHasImage(!imageIdList.isEmpty());

					// owner以外の共有ユーザーをセット
					List<UserDTO> sharedUserList = getSharedUsersByTaskId(conn, dto.getId(), dto.getOwnerId());
					dto.setSharedUserList(sharedUserList);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dto;
	}

	@Override
	public int delete(int taskId) {
		int result = 0;
		String sql = "DELETE FROM tasks WHERE id = ?";

		try (Connection conn = DBCon.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, taskId);
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public List<TaskDTO> getTasksByOwnerId(int ownerId) {
		List<TaskDTO> taskList = new ArrayList<>();
		String sql = "SELECT * FROM tasks WHERE owner_id = ? ORDER BY created_at DESC";

		try (Connection con = DBCon.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, ownerId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					TaskDTO dto = mapRow(rs);

					List<Integer> imageIdList = getImageIdsByTaskId(con, dto.getId());
					dto.setImageIdList(imageIdList);
					dto.setHasImage(!imageIdList.isEmpty());

					List<UserDTO> sharedUserList = getSharedUsersByTaskId(con, dto.getId(), ownerId);
					dto.setSharedUserList(sharedUserList);

					taskList.add(dto);
				}
			}

		} catch (SQLException e) {
			System.out.println("ownerタスク取得SQLエラー");
			e.printStackTrace();
		}

		return taskList;
	}

	public int clearOwner(int taskId) {
		String sql = "UPDATE tasks SET owner_id = NULL, updated_at = NOW() WHERE id = ?";
		int result = 0;

		try (Connection conn = DBCon.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, taskId);
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("owner解除SQLエラー");
			e.printStackTrace();
		}

		return result;
	}

	// 指定ユーザーを除いた共有ユーザー一覧を取得
	private List<UserDTO> getSharedUsersByTaskId(Connection conn, int taskId, int excludeUserId) throws SQLException {
		List<UserDTO> sharedUserList = new ArrayList<>();

		String sql = "SELECT u.id, u.name, u.email "
				+ "FROM tasks_users tu "
				+ "INNER JOIN users u ON tu.user_id = u.id "
				+ "WHERE tu.task_id = ? AND tu.user_id <> ? "
				+ "ORDER BY u.name ASC";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, taskId);
			pstmt.setInt(2, excludeUserId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					UserDTO user = new UserDTO();
					user.setId(rs.getInt("id"));
					user.setUserName(rs.getString("name"));
					user.setEmail(rs.getString("email"));
					sharedUserList.add(user);
				}
			}
		}

		return sharedUserList;
	}
}