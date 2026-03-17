<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="task_management_system.com.task_management.dto.UserDTO"%>

<%
String viewType = (String) request.getAttribute("viewType");
if (viewType == null) {
	viewType = "registered";
}

String keyword = request.getParameter("keyword");
if (keyword == null) {
	keyword = "";
}

List<UserDTO> registeredUserList = (List<UserDTO>) request.getAttribute("registeredUserList");
List<UserDTO> pendingAdminList = (List<UserDTO>) request.getAttribute("pendingAdminList");
List<UserDTO> adminUserList = (List<UserDTO>) request.getAttribute("adminUserList");
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>ユーザー管理</title>

<link href="<%=request.getContextPath()%>/css/user-menu.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/dashboard.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/admin-user-list.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
	<div class="app-layout">
		<!-- =========================
		     ヘッダー
		========================= -->
		<header class="header">
			<div class="header-left">
				<div>
					<button class="menu-button" id="menuToggle">☰</button>
				</div>
				<div class="logo logo-admin">
					<h1>
						<i class="fas fa-clipboard-list"></i> 
					</h1>
					<div class="app-title">
						<div class="app-name">Keep</div>
						<div class="page-subtitle">ユーザー管理</div>
					</div>
				</div>
			</div>

			<div class="header-right">
				<div class="header-icons">
					<i class="fas fa-users-cog"></i>
				</div>

				<div class="user-menu-wrapper">
					<button type="button" class="user-info" id="userMenuButton"
						aria-label="ユーザーメニュー">
						<i class="fas fa-user-circle"></i>
					</button>
					<jsp:include page="_user-menu.jsp" />
				</div>
			</div>
		</header>

		<!-- =========================
		     メインエリア
		========================= -->
		<main class="main-content">
			<!-- サイドバー -->
			<aside class="sidebar open" id="sidebar">
				<nav class="menu">
					<ul>
						<li class="<%="registered".equals(viewType) ? "active" : ""%>">
							<a
							href="<%=request.getContextPath()%>/admin/users?viewType=registered">
								<i class="fas fa-user-friends"></i> <span class="menu-text">登録ユーザー一覧</span>
						</a>
						</li>
						<li class="<%="admin".equals(viewType) ? "active" : ""%>"><a
							href="<%=request.getContextPath()%>/admin/users?viewType=admin">
								<i class="fas fa-user-shield"></i> <span class="menu-text">管理ユーザー一覧</span>
						</a></li>
					</ul>
				</nav>
			</aside>

			<!-- コンテンツ -->
			<div class="content-area" id="contentArea">

				<%
				if ("registered".equals(viewType)) {
				%>
				<!-- ========================================
				     登録ユーザー一覧
				======================================== -->
				<section class="admin-page">
					<div class="admin-section-card">
						<div class="section-header">
							<h2 class="section-title">登録ユーザー一覧</h2>
						</div>

						<form class="admin-search-form"
							action="<%=request.getContextPath()%>/admin/users" method="get">
							<input type="hidden" name="viewType" value="registered">

							<div class="search-form-row">
								<div class="search-field">
									<label for="keyword">ユーザーの検索</label> <input type="text"
										id="keyword" name="keyword" placeholder="名前またはメールアドレスを入力"
										value="<%=keyword%>">
								</div>

								<div class="search-button-wrap">
									<button type="submit" class="search-button">検索</button>
								</div>
							</div>
						</form>

						<div class="user-list-wrap">
							<div class="user-list-header">
								<div>ID</div>
								<div>名前</div>
								<div>メールアドレス</div>
								<div>操作</div>
							</div>
							<%
							if (registeredUserList != null && !registeredUserList.isEmpty()) {
							%>
							<%
							for (UserDTO user : registeredUserList) {
							%>
							<div class="user-row-card">
								<div class="user-id-label"><%=user.getId()%></div>
								<div class="user-name"><%=user.getUserName()%></div>
								<div class="user-mail"><%=user.getEmail()%></div>
								<div class="user-row-actions">
									<button type="button"
										class="delete-button open-delete-modal-button"
										data-user-id="<%=user.getId()%>"
										data-user-name="<%=user.getUserName()%>">削除</button>
								</div>
							</div>
							<%
							}
							%>
							<%
							} else {
							%>
							<p class="empty-message">該当する登録ユーザーはいません。</p>
							<%
							}
							%>
						</div>
					</div>
				</section>
				<%
				}
				%>

				<%
				if ("admin".equals(viewType)) {
				%>
				<!-- ========================================
				     管理ユーザー一覧
				======================================== -->
				<section class="admin-page">

					<!-- 申請中ユーザー -->
					<div class="admin-section-card">
						<div class="section-header">
							<h2 class="section-title">管理ユーザーの申請</h2>
						</div>

						<div class="user-list-wrap">
							<div class="user-list-header pending-header">
								<div>ID</div>
								<div>名前</div>
								<div>メールアドレス</div>
								<div>状態</div>
								<div>操作</div>
							</div>
							<%
							if (pendingAdminList != null && !pendingAdminList.isEmpty()) {
							%>
							<%
							for (UserDTO user : pendingAdminList) {
							%>
							<div class="user-row-card pending-row">
								<div class="user-id"><%=user.getId()%></div>
								<div class="user-name"><%=user.getUserName()%></div>
								<div class="user-mail"><%=user.getEmail()%></div>
								<div>
									<span class="status-badge status-pending">申請中</span>
								</div>
								<div class="user-row-actions">
									<form action="<%=request.getContextPath()%>/admin/approve"
										method="post" class="inline-form">
										<input type="hidden" name="userId" value="<%=user.getId()%>">
										<button type="submit" class="approval-button">許可</button>
									</form>

									<form action="<%=request.getContextPath()%>/admin/reject"
										method="post" class="inline-form">
										<input type="hidden" name="userId" value="<%=user.getId()%>">
										<button type="submit" class="reject-button">却下</button>
									</form>
								</div>
							</div>
							<%
							}
							%>
							<%
							} else {
							%>
							<p class="empty-message">申請中のユーザーはいません。</p>
							<%
							}
							%>
						</div>
					</div>

					<!-- 管理ユーザー一覧 -->
					<div class="admin-section-card">
						<div class="section-header">
							<h2 class="section-title">管理ユーザー一覧</h2>
						</div>

						<div class="user-list-wrap">
							<div class="user-list-header pending-header">
								<div>ID</div>
								<div>名前</div>
								<div>メールアドレス</div>
								<div>状態</div>
								<div>操作</div>
							</div>
							<%
							if (adminUserList != null && !adminUserList.isEmpty()) {
							%>
							<%
							for (UserDTO user : adminUserList) {
							%>
							<div class="user-row-card pending-row">
								<div class="user-id"><%=user.getId()%></div>
								<div class="user-name"><%=user.getUserName()%></div>
								<div class="user-mail"><%=user.getEmail()%></div>
								<div>
									<span class="status-badge status-admin">管理者</span>
								</div>
								<div class="user-row-actions">
									<button type="button"
										class="delete-button open-delete-modal-button"
										data-user-id="<%=user.getId()%>"
										data-user-name="<%=user.getUserName()%>">削除</button>
								</div>
							</div>
							<%
							}
							%>
							<%
							} else {
							%>
							<p class="empty-message">管理ユーザーはいません。</p>
							<%
							}
							%>
						</div>
					</div>
				</section>
				<%
				}
				%>
			</div>
		</main>

		<!-- ========================================
		     削除確認モーダル
		======================================== -->
		<div class="confirm-modal-overlay" id="deleteConfirmModal">
			<div class="confirm-modal">
				<p class="confirm-message">本当に削除しますか？</p>

				<form id="deleteUserForm"
					action="<%=request.getContextPath()%>/admin/delete-user"
					method="post">
					<input type="hidden" name="userId" id="deleteTargetUserId">

					<div class="confirm-actions">
						<button type="button" class="secondary-button"
							id="cancelDeleteButton">キャンセル</button>
						<button type="submit" class="delete-button">削除する</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<script>
		window.contextPath = '<%=request.getContextPath()%>';
	</script>
	<script src="<%=request.getContextPath()%>/js/user-menu.js"></script>
	<script src="<%=request.getContextPath()%>/js/admin-user-list.js"></script>
</body>
</html>