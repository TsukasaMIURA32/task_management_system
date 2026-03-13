<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="task_management_system.com.task_management.dto.TaskDTO"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>タスク管理システム</title>
<link href="<%=request.getContextPath()%>/css/user-menu.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/dashboard.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/dashboard-modal.css" rel="stylesheet" type="text/css" />

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
	<div class="app-layout">
		<!-- ヘッダー -->
		<header class="header">
			<div class="header-left">
				<div>
					<button class="menu-button" id="menuToggle">☰</button>
				</div>
				<div class="logo">
					<h1>
						<i class="fas fa-clipboard-list"></i> Keep
					</h1>
				</div>
			</div>

			<div class="header-right">
				<div class="header-icons">
					<i class="fas fa-redo"></i> <i class="fas fa-columns"></i> <i
						class="fas fa-cog"></i> <i class="fas fa-th"></i>
				</div>
				<div class="user-menu-wrapper">
					<button type="button" class="user-info" id="userMenuButton"
						aria-label="ユーザーメニュー">
						<i class="fas fa-user-circle"></i>
					</button>
					<jsp:include page="user-menu.jsp" />

				</div>
			</div>
		</header>

		<!-- メインエリア -->
		<main class="main-content">
			<!-- サイドバー -->
			<aside class="sidebar open" id="sidebar">
				<nav class="menu">
					<ul>
						<li class="active"><a href="#"> <i
								class="far fa-lightbulb"></i> <span class="menu-text">タスク</span>
						</a></li>
						<li><a href="#"> <i class="far fa-bell"></i> <span
								class="menu-text">リマインダー</span>
						</a></li>
						<li><a href="#"> <i class="fas fa-pen"></i> <span
								class="menu-text">ラベル</span>
						</a></li>
						<li><a href="#"> <i class="fas fa-archive"></i> <span
								class="menu-text">アーカイブ</span>
						</a></li>
						<li><a href="#"> <i class="far fa-trash-alt"></i> <span
								class="menu-text">ゴミ箱</span>
						</a></li>
					</ul>
				</nav>
			</aside>

			<!-- 入力エリア -->
			<div class="content-area" id="contentArea">
				<section class="note-input-area">
					<form class="note-form collapsed" id="noteForm"
						action="<%=request.getContextPath()%>/task/create" method="post"
						enctype="multipart/form-data">
						<div id="imagePreviewArea" class="image-preview-area hidden">
							<div id="imagePreviewList" class="image-preview-list"></div>
							<button type="button" id="removeImageButton"
								class="remove-image-button">×</button>
						</div>
						<input type="text" id="noteTitle" name="title" placeholder="タイトル">
						<div class="textarea-wrap">
							<textarea id="noteContent" name="content" placeholder="メモを入力..."></textarea>

							<label for="noteImage" class="image-upload-button"
								id="imageUploadButton"> <i class="far fa-image"></i>
							</label> <input type="file" id="noteImage" name="image" accept="image/*"
								multiple hidden>
						</div>

						<div class="form-actions">
							<div class="note-tools">
								<button type="button" class="tool-button" aria-label="装飾">
									<i class="fas fa-underline"></i>
								</button>

								<button type="button" class="tool-button"
									data-popover="colorPopover" aria-label="背景色">
									<i class="fas fa-palette"></i>
								</button>

								<button type="button" class="tool-button" aria-label="通知">
									<i class="far fa-bell"></i>
								</button>

								<button type="button" class="tool-button"
									data-popover="memberPopover" aria-label="メンバー追加">
									<i class="fas fa-user-plus"></i>
								</button>

								<button type="button" class="tool-button" id="imageSelectButton"
									aria-label="画像追加">
									<i class="far fa-image"></i>
								</button>

								<button type="button" class="tool-button"
									data-popover="archivePopover" aria-label="アーカイブ">
									<i class="fas fa-archive"></i>
								</button>

								<button type="button" class="tool-button"
									data-popover="morePopover" aria-label="詳細メニュー">
									<i class="fas fa-ellipsis-v"></i>
								</button>

								<button type="button" class="tool-button" id="undoButton"
									aria-label="元に戻す">
									<i class="fas fa-undo"></i>
								</button>

								<button type="button" class="tool-button" id="redoButton"
									aria-label="やり直す">
									<i class="fas fa-undo fa-flip-horizontal"></i>
								</button>
							</div>

							<div class="action-buttons">
								<button type="button" class="submit-button" id="closeNoteForm">閉じる</button>
							</div>


							<div class="tool-popovers">
								<!-- 背景色 -->
								<div class="popover-panel" id="colorPopover">
									<div class="color-options">
										<button type="button" class="color-chip color-default"
											data-color=""></button>
										<button type="button" class="color-chip color-yellow"
											data-color="color-yellow"></button>
										<button type="button" class="color-chip color-blue"
											data-color="color-blue"></button>
										<button type="button" class="color-chip color-green"
											data-color="color-green"></button>
										<button type="button" class="color-chip color-pink"
											data-color="color-pink"></button>
									</div>
								</div>
								<input type="hidden" id="noteColorId" name="colorId" value="1">
								<!-- メンバー追加 -->
								<div class="popover-panel" id="memberPopover">
									<div class="mini-form">
										<input type="text" id="memberNameInput" placeholder="メンバー名を入力">
										<button type="button" id="addMemberButton">追加</button>
									</div>
									<div id="memberPreview" class="member-preview"></div>
								</div>

								<!-- 詳細メニュー -->
								<div class="popover-panel" id="morePopover">
									<button type="button" id="deleteButton"
										class="popover-action danger">削除</button>
								</div>
							</div>
						</div>
					</form>
				</section>

				<!-- タスク一覧 -->
				<section class="notes-section">
					<div class="notes-grid">
						<%
						List<TaskDTO> taskList = (List<TaskDTO>) request.getAttribute("taskList");

						if (taskList != null && !taskList.isEmpty()) {
							for (TaskDTO task : taskList) {

								String colorClass = "color-default";

							switch (task.getColorId()) {
								case 2:
									colorClass = "color-yellow";
									break;
								case 3:
									colorClass = "color-blue";
									break;
								case 4:
									colorClass = "color-green";
									break;
								case 5:
									colorClass = "color-pink";
									break;
								default:
									colorClass = "color-default";
									break;
								}
						%>
						<article class="note-card <%=colorClass%>"
							data-note-id="<%=task.getId()%>">

							<%
							List<Integer> imageIdList = task.getImageIdList();
							int imageCount = imageIdList == null ? 0 : imageIdList.size();

							if (imageCount > 0) {
								String imageGridClass = "image-grid-" + (imageCount >= 4 ? 4 : imageCount);
							%>
							<div class="note-card-image-grid <%=imageGridClass%>">
								<%
								int displayCount = Math.min(imageCount, 4);
								for (int i = 0; i < displayCount; i++) {
									Integer imageId = imageIdList.get(i);
								%>
								<div
									class="note-card-image-wrap <%=(i == 3 && imageCount > 4) ? "has-more" : ""%>">
									<img
										src="<%=request.getContextPath()%>/task/image?imageId=<%=imageId%>"
										alt="タスク画像" class="note-card-image">

									<%
									if (i == 3 && imageCount > 4) {
									%>
									<div class="note-card-image-more">
										+<%=imageCount - 4%></div>
									<%
									}
									%>
								</div>
								<%
								}
								%>
							</div>
							<%
							}
							%>

							<h3><%=task.getTitle() == null ? "" : task.getTitle()%></h3>
							<p><%=task.getContent() == null ? "" : task.getContent()%></p>
						</article>
						<%
						}
						} else {
						%>
						<p>表示するタスクがありません。</p>
						<%
						}
						%>
					</div>
				</section>
			</div>
			
<!--			<form id="taskDetailForm" action="<%=request.getContextPath()%>/task/detail" method="get">-->
<!--				<input type="hidden" name="taskId" id="detailTaskId">-->
<!--			</form>-->
		</main>

		<jsp:include page="dashboard-modal.jsp" />

	</div>
	
<script>
  window.contextPath = '<%=request.getContextPath()%>';
</script>
<script src="<%=request.getContextPath()%>/js/dashboard.js"></script>
<script src="<%=request.getContextPath()%>/js/user-menu.js"></script>
<script src="<%=request.getContextPath()%>/js/dashboard-modal.js"></script>
</body>
</html>