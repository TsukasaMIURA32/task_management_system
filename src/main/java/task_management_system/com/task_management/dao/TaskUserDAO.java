package task_management_system.com.task_management.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}