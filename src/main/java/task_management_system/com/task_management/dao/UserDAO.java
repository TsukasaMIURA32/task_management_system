package task_management_system.com.task_management.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        user.setRole(rs.getInt("role"));
        user.setLoginFailCount(rs.getInt("login_fail_count"));
        user.setAccountLocked(rs.getBoolean("account_locked"));

        return user;
    }

    @Override
    public int insert(UserDTO user) {
        String sql = "INSERT INTO users (name, email, password, role, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, NOW(), NOW())";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword()); // ハッシュ済みパスワードを保存
            ps.setInt(4, user.getRole());

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

    /**
     * 旧ログイン用。
     * ハッシュ化対応後は、LoginServlet側で
     * findByEmail() + PasswordUtil.matches() を使うこと。
     */
    public UserDTO login(String email, String password) {
        return findByEmail(email);
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        String sql = "UPDATE users "
                   + "SET password = ?, "
                   + "login_fail_count = 0, "
                   + "account_locked = 0, "
                   + "locked_at = NULL, "
                   + "updated_at = NOW() "
                   + "WHERE email = ?";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword); // ハッシュ済みパスワードを保存
            ps.setString(2, email);

            int count = ps.executeUpdate();
            return count > 0;

        } catch (SQLException e) {
            System.out.println("パスワード更新SQLエラー");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 指定したroleのユーザー一覧を取得する
     */
    public List<UserDTO> findUsersByRole(int role) {
        List<UserDTO> userList = new ArrayList<>();

        String sql = "SELECT * FROM users WHERE role = ? ORDER BY id ASC";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, role);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userList.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("権限別ユーザー一覧取得SQLエラー");
            e.printStackTrace();
        }

        return userList;
    }

    /**
     * 指定したroleのユーザーを、名前またはメールアドレスで検索する
     */
    public List<UserDTO> searchUsersByRoleAndKeyword(int role, String keyword) {
        List<UserDTO> userList = new ArrayList<>();

        String sql = "SELECT * FROM users "
                   + "WHERE role = ? "
                   + "AND (name LIKE ? OR email LIKE ?) "
                   + "ORDER BY id ASC";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword + "%";

            ps.setInt(1, role);
            ps.setString(2, searchKeyword);
            ps.setString(3, searchKeyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userList.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("権限別ユーザー検索SQLエラー");
            e.printStackTrace();
        }

        return userList;
    }

    /**
     * 指定したユーザーの role を更新する
     */
    public int updateRole(int userId, int role) {
        String sql = "UPDATE users SET role = ?, updated_at = NOW() WHERE id = ?";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, role);
            ps.setInt(2, userId);

            return ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("権限更新SQLエラー");
            e.printStackTrace();
        }

        return 0;
    }

    public int countAdminUsers() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 1";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    
    /**
     * ログイン失敗回数を1増やす
     */
    public int incrementLoginFailCount(int userId) {
        String sql = "UPDATE users "
                   + "SET login_fail_count = login_fail_count + 1, updated_at = NOW() "
                   + "WHERE id = ?";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("ログイン失敗回数更新SQLエラー");
            e.printStackTrace();
        }

        return 0;
    }
    
    /**
     * ログイン成功時に失敗回数をリセットする
     */
    public int resetLoginFailCount(int userId) {
        String sql = "UPDATE users "
                   + "SET login_fail_count = 0, updated_at = NOW() "
                   + "WHERE id = ?";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("ログイン失敗回数リセットSQLエラー");
            e.printStackTrace();
        }

        return 0;
    }
    
    /**
     * アカウントをロックする
     */
    public int lockAccount(int userId) {
        String sql = "UPDATE users "
                   + "SET account_locked = 1, locked_at = NOW(), updated_at = NOW() "
                   + "WHERE id = ?";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("アカウントロックSQLエラー");
            e.printStackTrace();
        }

        return 0;
    }
    
    /**
     * アカウントロックを解除する
     */
    public int unlockAccount(int userId) {
        String sql = "UPDATE users "
                   + "SET account_locked = 0, login_fail_count = 0, locked_at = NULL, updated_at = NOW() "
                   + "WHERE id = ?";

        try (Connection con = DBCon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("アカウントロック解除SQLエラー");
            e.printStackTrace();
        }

        return 0;
    }
}
