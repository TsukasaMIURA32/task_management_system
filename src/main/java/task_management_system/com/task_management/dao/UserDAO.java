package task_management_system.com.task_management.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import task_management_system.com.task_management.dto.UserDTO;
import task_management_system.com.task_management.util.DBCon;

public class UserDAO extends BaseDAO<UserDTO> {

    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    protected UserDTO mapRow(ResultSet rs) throws SQLException {
        UserDTO user = new UserDTO();

        user.setId(rs.getInt("id"));
        user.setUserName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));

        int roleValue = rs.getInt("role");
        if (roleValue == 0) {
            user.setRole("user");
        } else if (roleValue == 1) {
            user.setRole("admin");
        } else if (roleValue == 2) {
            user.setRole("pending_admin");
        }

        return user;
    }

    @Override
    public int insert(UserDTO user) {
        String sql = "INSERT INTO users (name, email, password, role, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, NOW(), NOW())";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int roleValue = 0;

            if ("user".equals(user.getRole())) {
                roleValue = 0;
            } else if ("admin".equals(user.getRole())) {
                roleValue = 1;
            } else if ("pending_admin".equals(user.getRole())) {
                roleValue = 2;
            }

            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, roleValue);

            return ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("ユーザー登録SQLエラー");
            e.printStackTrace();
        }

        return 0;
    }

    public boolean insertUser(UserDTO user) {
        return insert(user) > 0;
    }


    /**
     * BaseDAO の抽象メソッド実装
     * ユーザーメニューから
     * 名前・メールアドレスの更新を行う
     */

    @Override
	public int update(UserDTO user) {
        String sql = "UPDATE users SET name = ?, email = ?, updated_at = NOW() WHERE id = ?";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {


            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getId());

            return ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("ユーザー更新SQLエラー");
            e.printStackTrace();
        }

        return 0;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("メール重複チェックSQLエラー");
            e.printStackTrace();
        }

        return false;
    }

    public UserDTO findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("メール検索SQLエラー");
            e.printStackTrace();
        }

        return null;
    }

    public UserDTO login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        System.out.println("UserDAO.login start");

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.println("DB接続OK");

            ps.setString(1, email);
            ps.setString(2, password);

            System.out.println("SQL実行前");

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("SQL実行後");
                if (rs.next()) {
                    System.out.println("ユーザー見つかった");
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("ログインSQLエラー");
            e.printStackTrace();
        }

        System.out.println("ユーザー見つからず");
        return null;
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        String sql = "UPDATE users SET password = ?, updated_at = NOW() WHERE email = ?";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);

            int count = ps.executeUpdate();
            return count > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
}