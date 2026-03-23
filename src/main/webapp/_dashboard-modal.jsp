<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="task_management_system.com.task_management.dto.TaskDTO"%>
<%@ page import="task_management_system.com.task_management.dto.UserDTO"%>
<%
TaskDTO editTask = (TaskDTO) request.getAttribute("editTask");

List<Integer> editImageIdList = null;
int editImageCount = 0;
String editImageGridClass = "";

if (editTask != null && editTask.getImageIdList() != null) {
	editImageIdList = editTask.getImageIdList();
	editImageCount = editImageIdList.size();
	editImageGridClass = "image-grid-" + (editImageCount >= 4 ? 4 : editImageCount);
}
%>

<div class="edit-modal-overlay" id="editModalOverlay">
	<div class="edit-modal-shell">
		<form class="note-form expanded" id="editNoteForm"
			action="<%=request.getContextPath()%>/task/update"
			method="post" enctype="multipart/form-data">

			<input type="hidden" name="taskId" id="editTargetId"
				value="<%=editTask != null ? editTask.getId() : ""%>">
			<input type="hidden" name="colorId" id="editNoteColorId"
				value="<%=editTask != null ? editTask.getColorId() : 1%>">
			<input type="hidden" name="deleteImageIds" id="editDeleteImageIds" value="">
			<div class="task-scroll-box">
				<!-- 画像エリアは1つだけ -->
				<div id="editImagePreviewArea"
					class="image-preview-area <%=editImageCount > 0 ? "" : "hidden"%>">
					<div id="editImagePreviewList"
						class="image-preview-list <%=editImageGridClass%>">
						<%
						if (editImageCount > 0) {
							for (Integer imageId : editImageIdList) {
						%>
						<div class="image-preview-item existing-image-item"
							data-image-id="<%=imageId%>" data-image-type="existing">
							<img
								src="<%=request.getContextPath()%>/task/image?imageId=<%=imageId%>"
								alt="タスク画像">
							<button type="button" class="image-remove-button"
								data-image-id="<%=imageId%>" data-image-type="existing">×</button>
						</div>
						<%
							}
						}
						%>
					</div>
				</div>
	
				<input type="file" id="editNoteImage" name="newImages"
					accept="image/*" multiple hidden>
				<div class="task-content-box">
					<input type="text" id="editNoteTitle" name="title" placeholder="タイトル"
						value="<%=editTask != null && editTask.getTitle() != null ? editTask.getTitle() : ""%>">
		
					<div class="textarea-wrap">
						<textarea id="editNoteContent" name="content" placeholder="メモを入力..."><%=editTask != null && editTask.getContent() != null ? editTask.getContent() : ""%></textarea>
					</div>
					<div class="shared-users-text" id="editSharedUsersText"></div>
					<div class="update-date">最終更新日時:
						<div id="updateDate">
							<%=editTask != null && editTask.getUpdatedAt() != null ? editTask.getUpdatedAt() : ""%>
						</div>
					</div>
				</div>
				<div class="form-actions">
					<div class="note-tools">
						<button type="button" class="tool-button" aria-label="装飾">
							<i class="fas fa-underline"></i>
						</button>
	
						<button type="button" class="tool-button"
							data-edit-popover="editColorPopover" aria-label="背景色">
							<i class="fas fa-palette"></i>
						</button>
	
						<button type="button" class="tool-button" aria-label="通知">
							<i class="far fa-bell"></i>
						</button>
	
						<button type="button" class="tool-button" id="openEditMemberModal" aria-label="メンバー追加">
						    <i class="fas fa-user-plus"></i>
						</button>
	
						<button type="button" class="tool-button" id="editImageButton"
							aria-label="画像追加">
							<i class="far fa-image"></i>
						</button>
	
						<button type="button" class="tool-button" aria-label="アーカイブ">
							<i class="fas fa-archive"></i>
						</button>
	
						<button type="button" class="tool-button"
							data-edit-popover="editMorePopover" aria-label="詳細メニュー">
							<i class="fas fa-ellipsis-v"></i>
						</button>
					</div>
	
					<div class="action-buttons">
						<button type="button" class="submit-button" id="closeEditModal">閉じる</button>
					</div>
				</div>
				<div class="tool-popovers">
					<div class="popover-panel" id="editColorPopover">
						<div class="color-options">
							<button type="button" class="color-chip color-default"
								data-edit-color=""></button>
							<button type="button" class="color-chip color-yellow"
								data-edit-color="color-yellow"></button>
							<button type="button" class="color-chip color-blue"
								data-edit-color="color-blue"></button>
							<button type="button" class="color-chip color-green"
								data-edit-color="color-green"></button>
							<button type="button" class="color-chip color-pink"
								data-edit-color="color-pink"></button>
						</div>
					</div>
					<div class="popover-panel" id="editMorePopover">
						<button type="button" id="editDeleteButton"
							class="popover-action danger">削除</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

		<!--		==========================================================================グループメンバー追加モーダル-->
		<div class="member-modal-overlay" id="editMemberModalOverlay">
			<div class="member-modal">
				 <div class="member-modal-header">
				   <h2>共同編集者</h2>
				   <button type="button" class="member-modal-close" id="closeEditMemberModal">×</button>
				 </div>
			
				<div class="member-modal-body">
					<div class="member-selector" data-mode="edit">
						<div class="mini-form">
						  <input type="text" class="member-keyword-input" placeholder="名前またはメールアドレスを入力">
						  <button type="button" class="add-member-button">追加</button>
						</div>
					
						<div class="member-search-result"></div>
						<div class="member-preview"></div>
						<div class="shared-user-ids-container"></div>
					</div>
				</div>
			
				<div class="member-modal-footer">
					<button type="button" class="member-modal-done" id="doneEditMemberModal">閉じる</button>
				</div>
			</div>
		</div>
	<!--		==========================================================================================================-->