<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<div class="user-menu-popover" id="userMenuPopover">
	<!-- メニュー画面 -->
	<div id="userMenuView" name="user-menu-view">
		<button type="button" class="user-menu-close" id="userMenuClose"
			aria-label="閉じる">×</button>
<!--			<div class="user-menu-title">-->
<!--				<p>アカウントを管理</p>-->
<!--			</div>-->

		<div class="user-menu-profile">
			<div class="user-menu-photo-wrap">
				<i class="fas fa-user-circle"></i>
				<button type="button" class="user-menu-camera"
					aria-label="プロフィール画像を変更">
					<i class="fas fa-camera"></i>
				</button>
			</div>

			<!-- 名前 -->
			<div class="editable-row">
				<div id="nameDisplay"
					class="editable-display user-menu-greeting-wrap">
					<p class="user-menu-greeting">
						Hi, <span id="nameText">${loginUser.userName}</span>!
					</p>
					<button type="button" class="edit-icon-btn" id="editNameBtn"
						aria-label="名前を編集">
						<i class="fas fa-pen"></i>
					</button>
				</div>

				<div id="nameEdit" class="editable-edit hidden">
					<input type="text" id="nameInput" name="userName" value="${loginUser.userName}">
					<button type="button" class="save-btn" id="saveNameBtn">保存</button>
					<button type="button" class="cancel-btn" id="cancelNameBtn"><i class="fas fa-times"></i></button>
				</div>
			</div>
			
			<!-- メールアドレス -->
			<div class="editable-row">
				<div id="emailDisplay" class="editable-display">
					<span id="emailText">${loginUser.email}</span>
					<button type="button" class="edit-icon-btn" id="editEmailBtn"
						aria-label="メールアドレスを編集">
						<i class="fas fa-pen"></i>
					</button>
				</div>
	
				<div id="emailEdit" class="editable-edit hidden">
					<input type="email" id="emailInput" name="email" value="${loginUser.email}">
					<button type="button" class="save-btn" id="saveEmailBtn">保存</button>
					<button type="button" class="cancel-btn" id="cancelEmailBtn"><i class="fas fa-times"></i></button>
				</div>
				<p id="emailChangeMessage" class="email-change-message"></p>
				
				<div class="password-change-link-wrap">
					<button type="button" id="openPasswordChangeBtn" class="password-change-link-btn">
						パスワードを変更
					</button>
				</div>
			</div>
			
		</div>

		<div class="user-menu-account-box">

			<!-- 通常メニュー -->
			<div id="accountMenuDefault">
				<div class="user-menu-link-item border-bottom" id="withdrawBtn">
					<div class="link-icon-circle">
						<i class="fas fa-times-circle"></i>
					</div>
					<span>退会する</span>
				</div>

				<form action="<%=request.getContextPath()%>/logout" method="post">
					<button type="submit" class="user-menu-link-item logout-btn">
						<div class="link-icon-circle plain">
							<i class="fas fa-sign-out-alt"></i>
						</div>
						<span>ログアウト</span>
					</button>
				</form>
			</div>

			<!-- パスワード変更表示 -->
			<div id="passwordChangeBox" class="hidden">
				<p class="withdraw-confirm-text">パスワードを変更します</p>
			
				<div class="password-change-form">
					<div class="password-area">
						<label for="currentPasswordInput">現在のパスワード</label>
						<input type="password" id="currentPasswordInput" >

						<label for="currentPasswordInput">新しいパスワード</label>
						<input type="password" id="newPasswordInput" placeholder="※8字以上、記号と数字1文字以上">

						<label for="currentPasswordInput">新しいパスワード（確認用）</label>
						<input type="password" id="confirmNewPasswordInput" placeholder="※8字以上、記号と数字1文字以上">
					</div>
					<p id="passwordChangeMessage" class="password-change-message"></p>
				</div>
			
				<div class="withdraw-confirm-actions">
					<button type="button" id="cancelPasswordChangeBtn" class="withdraw-cancel-btn">
						キャンセル
					</button>
					<button type="button" id="savePasswordChangeBtn" class="save-btn">
						変更する
					</button>
				</div>
			</div>
			<!-- 退会確認表示 -->
			<div id="withdrawConfirmBox" class="hidden">
				<p class="withdraw-confirm-text">本当に退会しますか？</p>
				<div class="withdraw-confirm-actions">
					<button type="button" id="cancelWithdrawBtn"
						class="withdraw-cancel-btn">キャンセル</button>
					<button type="button" id="confirmWithdrawBtn"
						class="withdraw-confirm-btn">退会</button>
				</div>
			</div>
			<form id="withdrawForm" action="<%=request.getContextPath()%>/user/withdraw" method="post">
				<% if (request.getParameter("viewType") != null) { %>
					<input type="hidden" name="viewType" value="<%= request.getParameter("viewType") %>">
				<% } %>
			</form>

		</div>
	</div>
</div>