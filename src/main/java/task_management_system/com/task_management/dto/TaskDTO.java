package task_management_system.com.task_management.dto;

public class TaskDTO extends BaseDTO{

	private String title;
	private String content;
	private int ownerId;
	private int colorId;

	

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	public int getColorId() {
		return colorId;
	}
	public void setColorId(int colorId) {
		this.colorId = colorId;
	}

	
    // toStringメソッド
	@Override
	public String toString() {
	    return "TaskDTO{" +
	            "id=" + id +
	            ", title='" + title + '\'' +
	            ", content='" + content + '\'' +
	            ", ownerId=" + ownerId +
	            ", colorId=" + colorId +
	            ", createdAt='" + createdAt + '\'' +
	            ", updatedAt='" + updatedAt + '\'' +
	            '}';
	}
}
