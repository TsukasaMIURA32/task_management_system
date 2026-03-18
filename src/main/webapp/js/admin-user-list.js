const menuToggle = document.getElementById("menuToggle");
const sidebar = document.getElementById("sidebar");
const contentArea = document.getElementById("contentArea");

const deleteConfirmModal = document.getElementById("deleteConfirmModal");
const deleteTargetUserId = document.getElementById("deleteTargetUserId");
const cancelDeleteButton = document.getElementById("cancelDeleteButton");

const openDeleteModalButtons = document.querySelectorAll(".open-delete-modal-button");

/* サイドバー開閉 */
if (menuToggle && sidebar && contentArea) {
  menuToggle.addEventListener("click", function () {
    sidebar.classList.toggle("closed");
    contentArea.classList.toggle("sidebar-closed");
  });
}

/* 削除確認モーダルを開く */
function openDeleteModal(userId) {
  if (deleteTargetUserId) {
    deleteTargetUserId.value = userId;
  }

  if (deleteConfirmModal) {
    deleteConfirmModal.classList.add("show");
  }
}

/* 削除確認モーダルを閉じる */
function closeDeleteModal() {
  if (deleteConfirmModal) {
    deleteConfirmModal.classList.remove("show");
  }
}

/* 一覧の削除ボタン */
openDeleteModalButtons.forEach(function (button) {
  button.addEventListener("click", function () {
    const userId = this.dataset.userId;
    openDeleteModal(userId);
  });
});

/* キャンセル */
if (cancelDeleteButton) {
  cancelDeleteButton.addEventListener("click", function () {
    closeDeleteModal();
  });
}

/* 背景クリック */
if (deleteConfirmModal) {
  deleteConfirmModal.addEventListener("click", function (e) {
    if (e.target === deleteConfirmModal) {
      closeDeleteModal();
    }
  });
}

/* Escape */
document.addEventListener("keydown", function (e) {
  if (e.key === "Escape") {
    closeDeleteModal();
  }
});

document.addEventListener("DOMContentLoaded", function () {

	/* =========================
	   却下確認モーダル
	========================= */
	var rejectModal = document.getElementById("rejectConfirmModal");
	var rejectTargetUserId = document.getElementById("rejectTargetUserId");
	var rejectTargetUserName = document.getElementById("rejectTargetUserName");
	var cancelRejectButton = document.getElementById("cancelRejectButton");

	var openRejectButtons = document.querySelectorAll(".open-reject-modal-button");

	// モーダルを開く
	for (var i = 0; i < openRejectButtons.length; i++) {
		openRejectButtons[i].addEventListener("click", function () {

			var userId = this.getAttribute("data-user-id");
			var userName = this.getAttribute("data-user-name");

			rejectTargetUserId.value = userId;
			rejectTargetUserName.textContent = userName;

			rejectModal.classList.add("show");
		});
	}

	// キャンセルボタン
	if (cancelRejectButton) {
		cancelRejectButton.addEventListener("click", function () {
			rejectModal.classList.remove("show");
		});
	}

	// 背景クリックで閉じる
	if (rejectModal) {
		rejectModal.addEventListener("click", function (e) {
			if (e.target === rejectModal) {
				rejectModal.classList.remove("is-active");
			}
		});
	}
});