/* ==========================================================
   編集モーダル本体の要素取得
========================================================== */
const editModalOverlay = document.getElementById("editModalOverlay");
const editNoteForm = document.getElementById("editNoteForm");
const editTargetId = document.getElementById("editTargetId");
const editNoteTitle = document.getElementById("editNoteTitle");
const editNoteContent = document.getElementById("editNoteContent");
const updatedAt = document.getElementById("updateDate");
const closeEditModal = document.getElementById("closeEditModal");

/* ==========================================================
   編集モーダル内の画像関連要素
========================================================== */
const editImageButton = document.getElementById("editImageButton");
const editNoteImage = document.getElementById("editNoteImage");
const editNoteColorId = document.getElementById("editNoteColorId");
const editDeleteImageIds = document.getElementById("editDeleteImageIds");

const editImagePreviewArea = document.getElementById("editImagePreviewArea");
const editImagePreviewList = document.getElementById("editImagePreviewList");

/* ==========================================================
   削除ボタン・共有ユーザー表示
========================================================== */
const editDeleteButton = document.getElementById("editDeleteButton");
const editSharedUsersText = document.getElementById("editSharedUsersText");

/* ==========================================================
   共同編集者モーダル関連
========================================================== */
const openEditMemberModal = document.getElementById("openEditMemberModal");
const editMemberModalOverlay = document.getElementById("editMemberModalOverlay");
const closeEditMemberModal = document.getElementById("closeEditMemberModal");
const doneEditMemberModal = document.getElementById("doneEditMemberModal");

/* 一覧のタスクカード */
const noteCards = document.getElementsByClassName("note-card");

/* 
   編集モーダル内で新規追加した画像ファイルを保持する配列
   既存画像はサーバー側のデータなので別管理
*/
let selectedNewFiles = [];

/* 編集開始時の元の値を保持する */
let originalEditTitle = "";
let originalEditContent = "";
let originalEditColorId = "1";
let originalExistingImageIds = [];
let originalSharedUserIds = [];

/* 
   必須要素がそろっているときだけ初期化を進める
   要素が不足しているページでエラーになるのを防ぐ
*/
if (
  editModalOverlay &&
  editNoteForm &&
  editTargetId &&
  editNoteTitle &&
  editNoteContent
) {
  /* 編集フォームで使う背景色クラス一覧 */
  const NOTE_COLOR_CLASSES = [
    "color-yellow",
    "color-blue",
    "color-green",
    "color-pink"
  ];

  /* ==========================================================
     編集用の共同編集者選択機能を初期化
     dashboard-member.js 側の initMemberSelector を使う
  ========================================================== */
  const editMemberSelectorContainer = document.querySelector('.member-selector[data-mode="edit"]');
  if (editMemberSelectorContainer && typeof initMemberSelector === "function") {
    window.editMemberSelectorApi = initMemberSelector(editMemberSelectorContainer);
  }

  /* ==========================================================
     編集モーダル内のpopover関係の要素取得
  ========================================================== */

  /* data-edit-popover を持つボタン一覧を取得 */
  function getEditPopoverButtons() {
    return editNoteForm.querySelectorAll("[data-edit-popover]");
  }

  /* 色チップ一覧を取得 */
  function getEditColorChips() {
    return editNoteForm.querySelectorAll("[data-edit-color]");
  }

  /* 編集モーダル内のpopover本体を取得 */
  function getEditPopovers() {
    return editNoteForm.getElementsByClassName("popover-panel");
  }

  /* ==========================================================
     背景色制御
  ========================================================== */

  /* 編集フォームについている色クラスをすべて外す */
  function clearEditFormColors() {
    for (let i = 0; i < NOTE_COLOR_CLASSES.length; i++) {
      editNoteForm.classList.remove(NOTE_COLOR_CLASSES[i]);
    }
  }

  /* 指定された色クラスだけ編集フォームに付与する */
  function setEditFormColor(colorClass) {
    clearEditFormColors();

    if (colorClass) {
      editNoteForm.classList.add(colorClass);
    }
  }

  /* 色チップの active 表示をいったん全部外す */
  function clearEditChipActive() {
    const editColorChips = getEditColorChips();

    for (let i = 0; i < editColorChips.length; i++) {
      editColorChips[i].classList.remove("active");
    }
  }

  /* 
     現在の色クラスに対応する色チップへ active を付ける
     デフォルト色のときは data-edit-color="" を対象にする
  */
  function setActiveEditChipByColor(colorClass) {
    clearEditChipActive();

    let selector = "";
    if (colorClass) {
      selector = '[data-edit-color="' + colorClass + '"]';
    } else {
      selector = '[data-edit-color=""]';
    }

    const targetChip = editNoteForm.querySelector(selector);
    if (targetChip) {
      targetChip.classList.add("active");
    }
  }

  /* DBの colorId を CSS クラスへ変換 */
  function getColorClassByColorId(colorId) {
    switch (Number(colorId)) {
      case 2:
        return "color-yellow";
      case 3:
        return "color-blue";
      case 4:
        return "color-green";
      case 5:
        return "color-pink";
      default:
        return "";
    }
  }

  /* CSS クラスを DB 保存用の colorId に変換 */
  function getColorIdByColorClass(colorClass) {
    switch (colorClass) {
      case "color-yellow":
        return 2;
      case "color-blue":
        return 3;
      case "color-green":
        return 4;
      case "color-pink":
        return 5;
      default:
        return 1;
    }
  }

  /* ==========================================================
     popover / モーダル開閉
  ========================================================== */

  /* 編集モーダル内のpopoverを全部閉じる */
  function closeEditPopovers() {
    const editPopovers = getEditPopovers();

    for (let i = 0; i < editPopovers.length; i++) {
      editPopovers[i].classList.remove("show");
      editPopovers[i].style.left = "";
      editPopovers[i].style.top = "";
    }
  }

  /* 編集モーダル本体を閉じる */
  function closeEditModalFn() {
    closeEditPopovers();
    editModalOverlay.classList.remove("show");
  }

  /* 共同編集者モーダルを開く */
  function openEditMemberModalFn() {
    if (editMemberModalOverlay) {
      editMemberModalOverlay.classList.add("show");
    }
  }

  /* 共同編集者モーダルを閉じる */
  function closeEditMemberModalFn() {
    if (editMemberModalOverlay) {
      editMemberModalOverlay.classList.remove("show");
    }
  }

  /* ==========================================================
     画像削除対象ID管理
  ========================================================== */

  /* 
     既存画像を削除したとき、その imageId を hidden に蓄積する
     サーバーは deleteImageIds を見て削除対象を判断する
  */
  function addDeleteImageId(imageId) {
    if (!editDeleteImageIds) {
      return;
    }

    let currentIds = [];

    if (editDeleteImageIds.value) {
      const splitIds = editDeleteImageIds.value.split(",");
      for (let i = 0; i < splitIds.length; i++) {
        if (splitIds[i] !== "") {
          currentIds.push(splitIds[i]);
        }
      }
    }

    if (currentIds.indexOf(String(imageId)) === -1) {
      currentIds.push(String(imageId));
      editDeleteImageIds.value = currentIds.join(",");
    }
  }

  /* ==========================================================
     画像プレビュー関連
  ========================================================== */

  /* プレビュー一覧に現在存在する画像要素を取得 */
  function getAllPreviewItems() {
    return editImagePreviewList.getElementsByClassName("image-preview-item");
  }

  /* 
     画像枚数に応じて grid クラスを付け替える
     0枚ならプレビュー領域を hidden にする
  */
  function updatePreviewGridClass() {
    if (!editImagePreviewArea || !editImagePreviewList) {
      return;
    }

    const items = getAllPreviewItems();
    editImagePreviewList.className = "image-preview-list";

    if (items.length === 0) {
      editImagePreviewArea.classList.add("hidden");
      return;
    }

    const gridClass = "image-grid-" + (items.length >= 4 ? 4 : items.length);
    editImagePreviewList.classList.add(gridClass);
    editImagePreviewArea.classList.remove("hidden");
  }

  /* 
     プレビュー内の × ボタンに削除イベントを付ける
     - 既存画像なら deleteImageIds に追加
     - 新規画像なら selectedNewFiles から削除
  */
  function bindPreviewRemoveEvents() {
    const removeButtons = editImagePreviewList.getElementsByClassName("image-remove-button");

    for (let i = 0; i < removeButtons.length; i++) {
      removeButtons[i].onclick = function (e) {
        e.preventDefault();
        e.stopPropagation();

        const button = this;
        const imageType = button.getAttribute("data-image-type");
        const imageItem = button.closest(".image-preview-item");

        if (!imageItem) {
          return;
        }

        /* 既存画像を削除した場合 */
        if (imageType === "existing") {
          const imageId = button.getAttribute("data-image-id");
          if (imageId) {
            addDeleteImageId(imageId);
          }
          imageItem.remove();
          updatePreviewGridClass();
          return;
        }

        /* 新規追加画像を削除した場合 */
        if (imageType === "new") {
          const newIndex = Number(button.getAttribute("data-new-index"));
          const nextFiles = [];

          for (let j = 0; j < selectedNewFiles.length; j++) {
            if (j !== newIndex) {
              nextFiles.push(selectedNewFiles[j]);
            }
          }

          selectedNewFiles = nextFiles;
          syncFileInputFromSelectedFiles();

          /* 残っている既存画像IDを拾い直して再描画 */
          const existingItems = editImagePreviewList.getElementsByClassName("existing-image-item");
          const existingImageIdList = [];

          for (let k = 0; k < existingItems.length; k++) {
            existingImageIdList.push(existingItems[k].dataset.imageId);
          }

          renderUnifiedPreview(existingImageIdList);
        }
      };
    }
  }

  /* 
     JSで保持している selectedNewFiles を
     実際の input[type=file] に同期する
  */
  function syncFileInputFromSelectedFiles() {
    if (!editNoteImage) {
      return;
    }

    const dataTransfer = new DataTransfer();

    for (let i = 0; i < selectedNewFiles.length; i++) {
      dataTransfer.items.add(selectedNewFiles[i]);
    }

    editNoteImage.files = dataTransfer.files;
  }

  /* 
     既存画像 + 新規画像をまとめてプレビュー描画する
     毎回いったん全消ししてから組み立て直す
  */
  function renderUnifiedPreview(existingImageIdList) {
    if (!editImagePreviewArea || !editImagePreviewList) {
      return;
    }

    editImagePreviewList.innerHTML = "";

    /* まず既存画像を描画 */
    if (existingImageIdList !== null && existingImageIdList !== undefined) {
      for (let i = 0; i < existingImageIdList.length; i++) {
        const imageId = existingImageIdList[i];

        const item = document.createElement("div");
        item.className = "image-preview-item existing-image-item";
        item.dataset.imageId = imageId;
        item.dataset.imageType = "existing";

        item.innerHTML =
          '<img src="' + window.contextPath + '/task/image?imageId=' + imageId + '" alt="タスク画像">' +
          '<button type="button" class="image-remove-button" ' +
          'data-image-id="' + imageId + '" data-image-type="existing"><i class="fas fa-trash-alt"></i></button>';

        editImagePreviewList.appendChild(item);
      }
    }

    /* 新規画像がなければここで終了 */
    if (selectedNewFiles.length === 0) {
      updatePreviewGridClass();
      bindPreviewRemoveEvents();
      return;
    }

    /* 新規画像は FileReader で読み込んで描画 */
    for (let j = 0; j < selectedNewFiles.length; j++) {
      (function (file, index) {
        const reader = new FileReader();

        reader.onload = function (e) {
          const item = document.createElement("div");
          item.className = "image-preview-item new-image-item";
          item.dataset.imageType = "new";
          item.dataset.newIndex = index;

          item.innerHTML =
            '<img src="' + e.target.result + '" alt="新規画像">' +
            '<button type="button" class="image-remove-button" ' +
            'data-image-type="new" data-new-index="' + index + '"><i class="fas fa-trash-alt"></i></button>';

          editImagePreviewList.appendChild(item);
          updatePreviewGridClass();
          bindPreviewRemoveEvents();
        };

        reader.readAsDataURL(file);
      })(selectedNewFiles[j], j);
    }

    updatePreviewGridClass();
    bindPreviewRemoveEvents();
  }

  /* ==========================================================
     共有ユーザー表示
  ========================================================== */

  /* 編集フォーム上に共有ユーザー名を表示する */
  function renderEditSharedUsers(sharedUsers) {
    if (!editSharedUsersText) {
      return;
    }

    if (!sharedUsers || sharedUsers.length === 0) {
      editSharedUsersText.innerHTML = "";
      return;
    }

    const names = sharedUsers.map(function (user) {
      return user.name;
    }).join("、");

    editSharedUsersText.innerHTML = '<i class="fas fa-users"></i>: ' + names;
  }
  
  /* 現在の共同編集者ID一覧を取得する */
  function getCurrentEditSharedUserIds() {
    const container = document.querySelector('.member-selector[data-mode="edit"] .shared-user-ids-container');

    if (!container) {
      return [];
    }

    const inputs = container.querySelectorAll('input[name="sharedUserIds"]');
    const userIds = [];

    for (let i = 0; i < inputs.length; i++) {
      userIds.push(String(inputs[i].value));
    }

    userIds.sort();
    return userIds;
  }

  /* 配列比較 */
  function isSameArray(arr1, arr2) {
    if (arr1.length !== arr2.length) {
      return false;
    }

    for (let i = 0; i < arr1.length; i++) {
      if (arr1[i] !== arr2[i]) {
        return false;
      }
    }

    return true;
  }

  /* ==========================================================
     タスク詳細取得 → 編集モーダル表示
  ========================================================== */

  /* 
     カードを押したときにタスク詳細をサーバーから取得し、
     編集モーダルへ反映して表示する
  */
	 async function openEditModalByAjax(taskId) {
	   try {
	     const response = await fetch(
	       window.contextPath + "/task/detail?taskId=" + encodeURIComponent(taskId)
	     );

	     const contentType = response.headers.get("content-type") || "";

	     if (!response.ok) {
	       if (contentType.includes("application/json")) {
	         const errorData = await response.json();
	         throw new Error(errorData.error || "タスク詳細の取得に失敗しました。");
	       } else {
	         throw new Error("タスク詳細の取得に失敗しました。しばらくしてから再度お試しください。");
	       }
	     }

	     if (!contentType.includes("application/json")) {
	       throw new Error("タスク詳細の取得に失敗しました。");
	     }

	     const task = await response.json();

	     editTargetId.value = task.id || "";
	     editNoteTitle.value = task.title || "";
	     editNoteContent.value = task.content || "";
	     if (updatedAt) {
	       updatedAt.textContent = task.updatedAt || "";
	     }

	     originalEditTitle = task.title || "";
	     originalEditContent = task.content || "";
	     originalEditColorId = String(task.colorId || 1);
	     originalExistingImageIds = (task.imageIdList || []).map(function (id) {
	       return String(id);
	     });
	     originalSharedUserIds = (task.sharedUsers || []).map(function (user) {
	       return String(user.id);
	     }).sort();

	     if (editNoteColorId) {
	       editNoteColorId.value = task.colorId || 1;
	     }

	     if (editDeleteImageIds) {
	       editDeleteImageIds.value = "";
	     }

	     selectedNewFiles = [];
	     if (editNoteImage) {
	       editNoteImage.value = "";
	     }

	     const colorClass = getColorClassByColorId(task.colorId);
	     setEditFormColor(colorClass);
	     setActiveEditChipByColor(colorClass);

	     renderUnifiedPreview(task.imageIdList || []);
	     renderEditSharedUsers(task.sharedUsers || []);

	     if (window.editMemberSelectorApi) {
	       window.editMemberSelectorApi.setUsers(task.sharedUsers || []);
	     }

	     if (editDeleteButton) {
	       if (task.isOwner) {
	         editDeleteButton.textContent = "削除";
	         editDeleteButton.dataset.deleteMode = "owner";
	       } else {
	         editDeleteButton.textContent = "共有を解除";
	         editDeleteButton.dataset.deleteMode = "member";
	       }
	     }

	     closeEditPopovers();
	     closeEditMemberModalFn();
	     editModalOverlay.classList.add("show");

	   } catch (error) {
	     console.error("タスク詳細取得エラー:", error);
	     showToastMessage(error.message || "タスク詳細の取得に失敗しました。");
	   }
	 }

  /* 一覧カードクリック時に編集モーダルを開く */
  for (let i = 0; i < noteCards.length; i++) {
    noteCards[i].addEventListener("click", async function () {
      const noteId = this.getAttribute("data-note-id");
      if (!noteId) {
        return;
      }

      await openEditModalByAjax(noteId);
    });
  }
  /* エラーメッセージの表示 */
  function showToastMessage(message) {
    let toast = document.getElementById("toastMessage");
 	console.log(toast);
    if (!toast) {
      toast = document.createElement("div");
      toast.id = "toastMessage";
      toast.className = "toast-message";
      document.body.appendChild(toast);
    }

    toast.innerHTML = '<i class="fas fa-info-circle"></i><span>' + message + '</span>';

    toast.classList.add("show");

    setTimeout(function () {
      toast.classList.remove("show");
    }, 3000);
  }
  /* ==========================================================
     編集モーダルの基本イベント
  ========================================================== */
  /* 変更があるかチェック */
  function hasEditChange() {
    /* 現在のタイトル・本文 */
    const currentTitle = editNoteTitle.value;
    const currentContent = editNoteContent.value;

    /* 現在の色ID */
    const currentColorId = editNoteColorId ? String(editNoteColorId.value) : "1";

    /* 削除対象に入っている既存画像ID */
    let deletedImageIds = [];
    if (editDeleteImageIds && editDeleteImageIds.value) {
      deletedImageIds = editDeleteImageIds.value.split(",").filter(function (id) {
        return id !== "";
      });
    }

    /* 既存画像の削除があるか */
    const hasDeletedExistingImage = deletedImageIds.length > 0;

    /* 新規画像の追加があるか */
    const hasNewImage = selectedNewFiles.length > 0;

    /* 共同編集者が変更されたか */
    const currentSharedUserIds = getCurrentEditSharedUserIds();
    const hasSharedUsersChanged = !isSameArray(originalSharedUserIds, currentSharedUserIds);

    return (
      currentTitle !== originalEditTitle ||
      currentContent !== originalEditContent ||
      currentColorId !== originalEditColorId ||
      hasDeletedExistingImage ||
      hasNewImage ||
      hasSharedUsersChanged
    );
  }
  /* 閉じるボタン */
  if (closeEditModal) {
    closeEditModal.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();
      submitOrCloseEditModal();
    });
  }

  /* 背景クリック */
  editModalOverlay.addEventListener("click", function (e) {
    if (e.target === editModalOverlay) {
      submitOrCloseEditModal();
    }
  });
  
  function submitOrCloseEditModal() {
    if (hasEditChange()) {
      editNoteForm.submit();
    } else {
      closeEditModalFn();
    }
  }

  /* 削除 / 共有解除ボタン処理 */
  if (editDeleteButton) {
    editDeleteButton.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();

      const taskId = editTargetId.value;
      if (!taskId) {
        return;
      }

      const deleteMode = editDeleteButton.dataset.deleteMode;
      let message = "";

      if (deleteMode === "owner") {
        message = "メモを削除しますか？\n削除したメモは、どの共有相手にも表示されなくなります。";
      } else {
        message = "このメモの共有を解除しますか？\nこのメモはあなたには表示されなくなります。";
      }

      const result = confirm(message);
      if (!result) {
        return;
      }

      const form = document.createElement("form");
      form.method = "post";
      form.action = window.contextPath + "/task/delete";

      const input = document.createElement("input");
      input.type = "hidden";
      input.name = "taskId";
      input.value = taskId;

      form.appendChild(input);
      document.body.appendChild(form);
      form.submit();
    });
  }

  /* フォーム内クリックは外側クリック扱いにしない */
  editNoteForm.addEventListener("click", function (e) {
    e.stopPropagation();
  });

  /* ==========================================================
     編集モーダル内のpopover制御
  ========================================================== */

  /* 色変更や詳細メニューなどのpopoverボタン処理 */
  const editPopoverButtons = getEditPopoverButtons();
  for (let i = 0; i < editPopoverButtons.length; i++) {
    editPopoverButtons[i].addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();

      const targetId = this.getAttribute("data-edit-popover");
      if (!targetId) {
        return;
      }

      const targetPopover = document.getElementById(targetId);
      if (!targetPopover) {
        return;
      }

      const alreadyOpen = targetPopover.classList.contains("show");
      closeEditPopovers();

      if (!alreadyOpen) {
        const buttonRect = this.getBoundingClientRect();
        const formRect = editNoteForm.getBoundingClientRect();

        const left = buttonRect.left - formRect.left;
        const top = buttonRect.bottom - formRect.top + 8;

        targetPopover.style.left = left + "px";
        targetPopover.style.top = top + "px";
        targetPopover.classList.add("show");
      }
    });
  }

  /* popover内クリックで閉じないようにする */
  const editPopovers = getEditPopovers();
  for (let i = 0; i < editPopovers.length; i++) {
    editPopovers[i].addEventListener("click", function (e) {
      e.stopPropagation();
    });
  }

  /* 色チップ押下時に背景色変更 */
  const editColorChips = getEditColorChips();
  for (let i = 0; i < editColorChips.length; i++) {
    editColorChips[i].addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();

      const colorClass = this.getAttribute("data-edit-color") || "";
      const colorId = getColorIdByColorClass(colorClass);

      if (editNoteColorId) {
        editNoteColorId.value = colorId;
      }

      setActiveEditChipByColor(colorClass);
      setEditFormColor(colorClass);
      closeEditPopovers();
    });
  }

  /* ==========================================================
     編集モーダル内の画像追加
  ========================================================== */

  if (editImageButton && editNoteImage) {
    /* 画像追加ボタン押下で file input を開く */
    editImageButton.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();
      closeEditPopovers();
      editNoteImage.click();
    });

    /* file input 変更時、新規画像を state に追加して再描画 */
    editNoteImage.addEventListener("change", function () {
      const newFiles = Array.from(editNoteImage.files || []);

      for (let i = 0; i < newFiles.length; i++) {
        selectedNewFiles.push(newFiles[i]);
      }

      syncFileInputFromSelectedFiles();

      const existingItems = editImagePreviewList.getElementsByClassName("existing-image-item");
      const existingImageIdList = [];

      for (let j = 0; j < existingItems.length; j++) {
        existingImageIdList.push(existingItems[j].dataset.imageId);
      }

      renderUnifiedPreview(existingImageIdList);
    });
  }

  /* ==========================================================
     共同編集者モーダル開閉
  ========================================================== */

  /* 共同編集者ボタン押下でモーダルを開く */
  if (openEditMemberModal) {
    openEditMemberModal.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();
      closeEditPopovers();
      openEditMemberModalFn();
    });
  }

  /* × ボタンで閉じる */
  if (closeEditMemberModal) {
    closeEditMemberModal.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();
      closeEditMemberModalFn();
    });
  }

  /* フッターの閉じるボタンでも閉じる */
  if (doneEditMemberModal) {
    doneEditMemberModal.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();
      closeEditMemberModalFn();
    });
  }

  /* モーダル背景クリックで閉じる */
  if (editMemberModalOverlay) {
    editMemberModalOverlay.addEventListener("click", function (e) {
      if (e.target === editMemberModalOverlay) {
        closeEditMemberModalFn();
      }
    });
  }

  /* ==========================================================
     キーボード・外側クリック処理
  ========================================================== */

  /* 
     Escapeキー押下時
     - 共同編集者モーダルが開いていればそれを閉じる
     - そうでなければ編集モーダルを閉じる
  */
  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
      if (editMemberModalOverlay && editMemberModalOverlay.classList.contains("show")) {
        closeEditMemberModalFn();
        return;
      }

      if (editModalOverlay.classList.contains("show")) {
        closeEditModalFn();
      }
    }
  });

  /* 編集モーダル表示中に外側クリックしたらpopoverだけ閉じる */
  document.addEventListener("click", function () {
    if (editModalOverlay.classList.contains("show")) {
      closeEditPopovers();
    }
  });
}