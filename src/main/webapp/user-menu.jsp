<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<div class="user-menu-popover" id="userMenuPopover">
	<!-- メニュー画面 -->
	<div id="userMenuView">
		<button type="button" class="user-menu-close" id="userMenuClose"
			aria-label="閉じる">×</button>

		<!-- メールアドレス -->
		<div class="editable-row">
			<div id="emailDisplay" class="editable-display">
				<span id="emailText">c32o0v0o30x@gmail.com</span>
				<button type="button" class="edit-icon-btn" id="editEmailBtn"
					aria-label="メールアドレスを編集">
					<i class="fas fa-pen"></i>
				</button>
			</div>

			<div id="emailEdit" class="editable-edit hidden">
				<input type="email" id="emailInput" value="c32o0v0o30x@gmail.com">
				<button type="button" class="save-btn" id="saveEmailBtn">保存</button>
				<button type="button" class="cancel-btn" id="cancelEmailBtn">キャンセル</button>
			</div>
		</div>

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
						Hi, <span id="nameText">つかさ</span>!
					</p>
					<button type="button" class="edit-icon-btn" id="editNameBtn"
						aria-label="名前を編集">
						<i class="fas fa-pen"></i>
					</button>
				</div>

				<div id="nameEdit" class="editable-edit hidden">
					<input type="text" id="nameInput" value="つかさ">
					<button type="button" class="save-btn" id="saveNameBtn">保存</button>
					<button type="button" class="cancel-btn" id="cancelNameBtn">キャンセル</button>
				</div>
			</div>
			<p>Manage your Account</p>
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

				<div class="user-menu-link-item">
					<div class="link-icon-circle plain">
						<i class="fas fa-sign-out-alt"></i>
					</div>
					<span>ログアウト</span>
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

		</div>
	</div>
</div>