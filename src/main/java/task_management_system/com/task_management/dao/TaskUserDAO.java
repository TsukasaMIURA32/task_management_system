package task_management_system.com.task_management.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import task_management_system.com.task_management.dto.UserDTO;
import task_management_system.com.task_management.util.DBCon;

public class TaskUserDAO {

    public int insertTaskUser(int taskId, int userId) {
        String sql = "INSERT INTO tasks_users (task_id, user_id, created_at) VALUES (?, ?, NOW())";
        int result = 0;

        try (Connection conn = DBCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            pstmt.setInt(2, userId);
            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("tasks_users 登録SQLエラー");
            e.printStackTrace();
        }

        return result;
    }

    public void insertTaskUsers(int taskId, String[] sharedUserIds, int loginUserId) {
        insertTaskUser(taskId, loginUserId);

        if (sharedUserIds == null) {
            return;
        }

        for (String userIdStr : sharedUserIds) {
            if (userIdStr == null || userIdStr.isBlank()) {
                continue;
            }

            int userId = Integer.parseInt(userIdStr);

            if (userId == loginUserId) {
                continue;
            }

            insertTaskUser(taskId, userId);
        }
    }

    public int countOtherMembers(int taskId, int userId) {
        String sql = "SELECT COUNT(*) FROM tasks_users WHERE task_id = ? AND user_id <> ?";
        int count = 0;

        try (Connection conn = DBCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("メンバー数取得SQLエラー");
            e.printStackTrace();
        }

        return count;
    }

    public boolean existsTaskUser(int taskId, int userId) {
        String sql = "SELECT COUNT(*) FROM tasks_users WHERE task_id = ? AND user_id = ?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("tasks_users 存在確認SQLエラー");
            e.printStackTrace();
        }

        return false;
    }

    public int deleteTaskUser(int taskId, int userId) {
        String sql = "DELETE FROM tasks_users WHERE task_id = ? AND user_id = ?";
        int result = 0;

        try (Connection conn = DBCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            pstmt.setInt(2, userId);
            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("tasks_users 削除SQLエラー");
            e.printStackTrace();
        }

        return result;
    }

    public int deleteTaskUsersByTaskId(int taskId) {
        String sql = "DELETE FROM tasks_users WHERE task_id = ?";
        int result = 0;

        try (Connection conn = DBCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("tasks_users 全削除SQLエラー");
            e.printStackTrace();
        }

        return result;
    }

    public List<UserDTO> getSharedUsersByTaskId(int taskId) {
        List<UserDTO> userList = new ArrayList<>();

        String sql = "SELECT u.id, u.name, u.email "
                   + "FROM tasks_users tu "
                   + "INNER JOIN users u ON tu.user_id = u.id "
                   + "WHERE tu.task_id = ? "
                   + "ORDER BY u.id";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserDTO dto = new UserDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setUserName(rs.getString("name"));
                    dto.setEmail(rs.getString("email"));
                    userList.add(dto);
                }
            }

        } catch (SQLException e) {
            System.out.println("共有ユーザー取得SQLエラー");
            e.printStackTrace();
        }

        return userList;
    }
    
    public List<UserDTO> searchUsersByNameOrEmail(String keyword) {
    	List<UserDTO> userList = new ArrayList<>();

    	String sql = "SELECT id, name, email "
    			+ "FROM users "
    			+ "WHERE name LIKE ? OR email LIKE ? "
    			+ "ORDER BY name ASC";

    	try (Connection conn = DBCon.getConnection();
    			PreparedStatement pstmt = conn.prepareStatement(sql)) {

    		String likeKeyword = "%" + keyword + "%";
    		pstmt.setString(1, likeKeyword);
    		pstmt.setString(2, likeKeyword);

    		try (ResultSet rs = pstmt.executeQuery()) {
    			while (rs.next()) {
    				UserDTO dto = new UserDTO();
    				dto.setId(rs.getInt("id"));
    				dto.setUserName(rs.getString("name"));
    				dto.setEmail(rs.getString("email"));
    				userList.add(dto);
    			}
    		}

    	} catch (SQLException e) {
    		System.out.println("ユーザー検索SQLエラー");
    		e.printStackTrace();
    	}

    	return userList;
    }
}