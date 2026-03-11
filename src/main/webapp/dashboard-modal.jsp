<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!-- 編集モーダル -->
<div class="edit-modal-overlay" id="editModalOverlay">
	<div class="edit-modal-shell">
		<form class="note-form expanded" id="editNoteForm">
			<input type="hidden" id="editTargetId"> <input type="text"
				id="editNoteTitle" name="title" placeholder="タイトル">

			<div class="textarea-wrap">
				<textarea id="editNoteContent" name="content" placeholder="メモを入力..."></textarea>
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

					<button type="button" class="tool-button"
						data-edit-popover="editMemberPopover" aria-label="メンバー追加">
						<i class="fas fa-user-plus"></i>
					</button>

					<button type="button" class="tool-button" id="editImageButton"
						aria-label="画像追加">
						<i class="far fa-image"></i>
					</button>
					<input type="file" id="editNoteImage" accept="image/*" hidden>

					<button type="button" class="tool-button" aria-label="アーカイブ">
						<i class="fas fa-archive"></i>
					</button>

					<button type="button" class="tool-button"
						data-edit-popover="editMorePopover" aria-label="詳細メニュー">
						<i class="fas fa-ellipsis-v"></i>
					</button>

					<button type="button" class="tool-button" id="editUndoButton"
						aria-label="元に戻す">
						<i class="fas fa-undo"></i>
					</button>

					<button type="button" class="tool-button" id="editRedoButton"
						aria-label="やり直す">
						<i class="fas fa-undo fa-flip-horizontal"></i>
					</button>
				</div>

				<div class="action-buttons">
					<button type="button" class="submit-button" id="closeEditModal">閉じる</button>
				</div>

				<div class="tool-popovers">
					<!-- 背景色 -->
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

					<!-- メンバー追加 -->
					<div class="popover-panel" id="editMemberPopover">
						<div class="mini-form">
							<input type="text" id="memberNameInput" placeholder="メンバー名を入力">
							<button type="button" id="editAddMemberButton">追加</button>
						</div>
						<div id="memberPreview" class="member-preview"></div>
					</div>

					<!-- 画像追加 -->
					<!--						<div class="popover-panel" id="editImagePopover">-->
					<!--							<button type="button" id="imageSelectButton"-->
					<!--								class="popover-action">画像を選択</button>-->
					<!--						</div>-->

					<!-- アーカイブ -->
					<div class="popover-panel" id="editArchivePopover">
						<button type="button" id="archiveButton" class="popover-action">アーカイブする</button>
					</div>

					<!-- 詳細メニュー -->
					<div class="popover-panel" id="editMorePopover">
						<button type="button" id="deleteButton"
							class="popover-action danger">削除</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
