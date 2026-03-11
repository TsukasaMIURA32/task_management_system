<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>タスク管理システム</title>
<link href="css/user-menu.css" rel="stylesheet" type="text/css" />
<link href="css/dashboard.css" rel="stylesheet" type="text/css" />
<link href="css/dashboard-modal.css" rel="stylesheet" type="text/css" />
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
					<form class="note-form collapsed" id="noteForm" action="#"
						method="post">
						<input type="text" id="noteTitle" name="title" placeholder="タイトル">
						<div class="textarea-wrap">
							<textarea id="noteContent" name="content" placeholder="メモを入力..."></textarea>

							<label for="noteImage" class="image-upload-button"
								id="imageUploadButton"> <i class="far fa-image"></i>
							</label> <input type="file" id="noteImage" name="image" accept="image/*"
								hidden>
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
								<input type="file" id="noteImage" accept="image/*" hidden>

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

								<!-- メンバー追加 -->
								<div class="popover-panel" id="memberPopover">
									<div class="mini-form">
										<input type="text" id="memberNameInput"
											placeholder="メンバー名を入力">
										<button type="button" id="addMemberButton">追加</button>
									</div>
									<div id="memberPreview" class="member-preview"></div>
								</div>

								<!-- 詳細メニュー -->
								<div class="popover-panel" id="morePopover">
									<button type="button" id="deleteEditNote"
										class="popover-action danger">削除</button>
								</div>
							</div>
						</div>
					</form>
				</section>

				<!-- タスク一覧 -->
				<section class="notes-section">
					<div class="notes-grid">

						<article class="note-card color-yellow" data-note-id="1">
							<h3>買い物メモ</h3>
							<p>牛乳、パン、卵、コーヒーを買う</p>
						</article>

						<article class="note-card color-blue" data-note-id="2">
							<h3>課題</h3>
							<p>DAO親クラスの設計を進める</p>
						</article>

						<article class="note-card color-green" data-note-id="3">
							<h3>打ち合わせ</h3>
							<p>明日14時にチームで進捗確認</p>
						</article>

						<article class="note-card color-pink" data-note-id="4">
							<h3>やること</h3>
							<p>JSP画面作成、Git push、DB確認</p>
						</article>

						<article class="note-card color-default" data-note-id="5">
							<h3>メモ</h3>
							<p>画像アップロード機能は後で実装する</p>
						</article>

					</div>
				</section>
			</div>
		</main>

		<jsp:include page="dashboard-modal.jsp" />

	</div>
	<script src="js/dashboard.js"></script>
	<script src="js/user-menu.js"></script>
	<script src="js/dashboard-modal.js"></script>
</body>
</html>