package task_management_system.com.task_management.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import task_management_system.com.task_management.dto.TaskDTO;

public class TaskDAO extends BaseDAO<TaskDTO>{
	
    // コンストラクタ
    public TaskDAO() {
    }
	
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
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	protected int update(TaskDTO dto) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}


}
