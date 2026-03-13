package task_management_system.com.task_management.dto;

public class TaskImageDTO {
	private int id;
	private int taskId;
	private String imagePath;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}
