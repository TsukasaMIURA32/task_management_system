package task_management_system.com.task_management.dto;

public class BaseDTO {
	protected int id;
	protected String createdAt;
	protected String updatedAt;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	
    // toStringメソッド
	@Override
	public String toString() {
	    return "TaskDTO{" +
	            "id=" + id +
	            ", createdAt='" + createdAt + '\'' +
	            ", updatedAt='" + updatedAt + '\'' +
	            '}';
	}
}
