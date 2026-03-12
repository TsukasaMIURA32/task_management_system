package task_management_system.com.task_management.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import task_management_system.com.task_management.util.DBCon;

public abstract class BaseDAO<T> {

	protected abstract String getTableName();
	protected abstract T mapRow(ResultSet rs) throws SQLException;
	public abstract int insert(T dto);  // 登録の抽象メソッド
	public abstract int update(T dto);  // 更新の抽象メソッド

	public T getById(int id) {
	    T dto = null;
	    String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
	    try (Connection conn = DBCon.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, id);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                dto = mapRow(rs);
	            }
	        }
	    } catch(SQLException e) {
			e.printStackTrace();
		}
	    return dto;
	}
	
	public List<T> getAll() {
	    List<T> results = new ArrayList<>();
	    String sql = "SELECT * FROM " + getTableName();
	    try (Connection conn = DBCon.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            results.add(mapRow(rs));
	        }
	    } catch(SQLException e) {
			e.printStackTrace();
		}
	    return results;
	}
	
	public int delete(int id) {
	    
		String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
	    int result = 0;
	    
	    try (Connection conn = DBCon.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, id);
	        result = pstmt.executeUpdate();
	    } catch(SQLException e) {
			e.printStackTrace();
		}
	    return result;
	}
}
