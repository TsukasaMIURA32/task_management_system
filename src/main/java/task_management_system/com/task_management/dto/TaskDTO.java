package task_management_system.com.task_management.dto;

import java.util.ArrayList;
import java.util.List;

public class TaskDTO extends BaseDTO{

	private String title;
	private String content;
	private int ownerId;
	private int colorId;
	private boolean hasImage;
	private List<Integer> imageIdList = new ArrayList<>();
	private List<UserDTO> sharedUserList;

	

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
	
	public boolean isHasImage() {
		return hasImage;
	}

	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}
	
	public List<Integer> getImageIdList() {
		return imageIdList;
	}

	public void setImageIdList(List<Integer> imageIdList) {
		this.imageIdList = imageIdList;
	}

	public List<UserDTO> getSharedUserList() {
		return sharedUserList;
	}

	public void setSharedUserList(List<UserDTO> sharedUserList) {
		this.sharedUserList = sharedUserList;
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
