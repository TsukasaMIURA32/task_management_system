const menuToggle = document.getElementById("menuToggle");
const sidebar = document.getElementById("sidebar");
const contentArea = document.getElementById("contentArea");

const noteForm = document.getElementById("noteForm");
const noteTitle = document.getElementById("noteTitle");
const noteContent = document.getElementById("noteContent");
const closeNoteForm = document.getElementById("closeNoteForm");

const imageUploadButton = document.getElementById("imageUploadButton");
const noteImage = document.getElementById("noteImage");

/* プレビュー画像関連 */
const imagePreviewArea = document.getElementById("imagePreviewArea");
const imagePreviewList = document.getElementById("imagePreviewList");
const removeImageButton = document.getElementById("removeImageButton");

/* popover関連 */
const popoverButtons = noteForm.querySelectorAll("[data-popover]");
const popovers = noteForm.querySelectorAll(".popover-panel");
const colorChips = noteForm.querySelectorAll("[data-color]");
const noteColorId = document.getElementById("noteColorId");

const memberNameInput = document.getElementById("memberNameInput");
const addMemberButton = document.getElementById("addMemberButton");
const memberPreview = document.getElementById("memberPreview");

const imageSelectButton = document.getElementById("imageSelectButton");
const archiveButton = document.getElementById("archiveButton");
const deleteButton = document.getElementById("deleteButton");
const undoButton = document.getElementById("undoButton");
const redoButton = document.getElementById("redoButton");

/* 簡易状態管理 */
let deletedDraft = null;
let archived = false;

/* 画像状態管理（追加式） */
let selectedImageFiles = [];

/* -------------------------
   サイドバー開閉
------------------------- */
menuToggle.addEventListener("click", function () {
  sidebar.classList.toggle("closed");
  contentArea.classList.toggle("sidebar-closed");
});

/* -------------------------
   メモ入力フォーム展開・閉じる
------------------------- */
function openNoteForm() {
  noteForm.classList.remove("collapsed");
  noteForm.classList.add("expanded");
}

function closeNoteFormIfEmpty() {
  const titleValue = noteTitle.value.trim();
  const contentValue = noteContent.value.trim();
  const hasImage = selectedImageFiles.length > 0;

  if (titleValue === "" && contentValue === "" && !hasImage) {
    resetNoteColor();
    noteForm.classList.remove("expanded");
    noteForm.classList.add("collapsed");
  }
}

function forceCloseNoteForm() {
  noteTitle.value = "";
  noteContent.value = "";

  selectedImageFiles = [];
  syncNoteImageInput();
  renderImagePreviews();

  resetNoteColor();

  memberPreview.textContent = "";
  memberNameInput.value = "";

  noteForm.classList.remove("expanded");
  noteForm.classList.add("collapsed");
  closeAllPopovers();
}

/* -------------------------
   フォームを開く処理
------------------------- */
noteContent.addEventListener("click", function (e) {
  e.stopPropagation();
  openNoteForm();
});

noteContent.addEventListener("focus", function () {
  openNoteForm();
});

noteTitle.addEventListener("focus", function () {
  openNoteForm();
});

noteForm.addEventListener("click", function (e) {
  e.stopPropagation();
});

/* -------------------------
   画像ボタン（collapsed時の右上アイコン）
------------------------- */
imageUploadButton.addEventListener("click", function (e) {
  e.stopPropagation();
  openNoteForm();
  noteImage.click();
});

/* -------------------------
   閉じる処理
------------------------- */
function hasNoteInput() {
  const titleValue = noteTitle.value.trim();
  const contentValue = noteContent.value.trim();
  const hasImage = selectedImageFiles.length > 0;

  return titleValue !== "" || contentValue !== "" || hasImage;
}

closeNoteForm.addEventListener("click", function (e) {
  e.preventDefault();
  e.stopPropagation();

  if (hasNoteInput()) {
    syncNoteImageInput();
    noteForm.submit();
  } else {
    forceCloseNoteForm();
  }
});

document.addEventListener("click", function () {
  closeNoteFormIfEmpty();
  closeAllPopovers();
});

/* -------------------------
   Popover共通
------------------------- */
function closeAllPopovers() {
  popovers.forEach((popover) => {
    popover.classList.remove("show");
  });
}

popoverButtons.forEach((button) => {
  button.addEventListener("click", function (e) {
    e.stopPropagation();
    openNoteForm();

    const targetId = button.dataset.popover;
    const targetPopover = document.getElementById(targetId);

    const alreadyOpen = targetPopover.classList.contains("show");
    closeAllPopovers();

    if (!alreadyOpen) {
      const buttonRect = button.getBoundingClientRect();
      const formRect = noteForm.getBoundingClientRect();

      targetPopover.style.left = (buttonRect.left - formRect.left) + "px";
      targetPopover.style.top = (buttonRect.bottom - formRect.top + 8) + "px";

      targetPopover.classList.add("show");
    }
  });
});

popovers.forEach((popover) => {
  popover.addEventListener("click", function (e) {
    e.stopPropagation();
  });
});

/* -------------------------
   カラー選択
------------------------- */
colorChips.forEach(chip => {
  chip.addEventListener("click", function () {
    colorChips.forEach(c => c.classList.remove("active"));
    chip.classList.add("active");

    const colorClass = chip.dataset.color;

    noteForm.classList.remove(
      "color-yellow",
      "color-blue",
      "color-green",
      "color-pink"
    );

    if (colorClass) {
      noteForm.classList.add(colorClass);
    }

    let colorId = 1;

    switch (colorClass) {
      case "color-yellow":
        colorId = 2;
        break;
      case "color-blue":
        colorId = 3;
        break;
      case "color-green":
        colorId = 4;
        break;
      case "color-pink":
        colorId = 5;
        break;
      default:
        colorId = 1;
        break;
    }

    noteColorId.value = colorId;
  });
});

/* -------------------------
   背景色リセット
------------------------- */
function resetNoteColor() {
  noteForm.classList.remove(
    "color-yellow",
    "color-blue",
    "color-green",
    "color-pink",
    "color-gray"
  );

  document.querySelectorAll(".color-chip")
    .forEach(chip => chip.classList.remove("active"));

  noteColorId.value = 1;
}

/* -------------------------
   ② メンバー追加
------------------------- */
addMemberButton.addEventListener("click", function () {
  const name = memberNameInput.value.trim();
  if (name !== "") {
    memberPreview.textContent = "追加メンバー: " + name;
    memberNameInput.value = "";
    closeAllPopovers();
  }
});

/* -------------------------
   ③ 画像登録（Google Keep風：1枚ずつ追加）
------------------------- */
imageSelectButton.addEventListener("click", function () {
  openNoteForm();
  noteImage.click();
  closeAllPopovers();
});

/* inputから選ばれた画像を状態に追加 */
noteImage.addEventListener("change", function () {
  const newFiles = Array.from(noteImage.files || []);

  if (newFiles.length === 0) {
    return;
  }

  for (const file of newFiles) {
    if (selectedImageFiles.length >= 4) {
      alert("画像は最大4枚まで追加できます。");
      break;
    }

    const isDuplicate = selectedImageFiles.some(existing =>
      existing.name === file.name &&
      existing.size === file.size &&
      existing.lastModified === file.lastModified
    );

    if (!isDuplicate) {
      selectedImageFiles.push(file);
    }
  }

  syncNoteImageInput();
  renderImagePreviews();
  openNoteForm();

  /* 同じファイルを再度選んだときにもchangeが発火しやすいように */
  noteImage.value = "";
});

/* File配列をinput.filesへ同期 */
function syncNoteImageInput() {
  const dataTransfer = new DataTransfer();

  selectedImageFiles.forEach(file => {
    dataTransfer.items.add(file);
  });

  noteImage.files = dataTransfer.files;
}

/* プレビュー描画 */
function renderImagePreviews() {
  imagePreviewList.innerHTML = "";

  imagePreviewList.classList.remove(
    "image-grid-1",
    "image-grid-2",
    "image-grid-3",
    "image-grid-4"
  );

  const fileCount = selectedImageFiles.length;

  if (fileCount === 0) {
    imagePreviewArea.classList.add("hidden");
    return;
  }

  if (fileCount === 1) {
    imagePreviewList.classList.add("image-grid-1");
  } else if (fileCount === 2) {
    imagePreviewList.classList.add("image-grid-2");
  } else if (fileCount === 3) {
    imagePreviewList.classList.add("image-grid-3");
  } else {
    imagePreviewList.classList.add("image-grid-4");
  }

  selectedImageFiles.forEach((file, index) => {
    const reader = new FileReader();

    reader.onload = function (e) {
      const item = document.createElement("div");
      item.classList.add("image-preview-item");

      const img = document.createElement("img");
      img.src = e.target.result;
      img.alt = "画像プレビュー";

      const removeButton = document.createElement("button");
      removeButton.type = "button";
      removeButton.classList.add("image-remove-button");
      removeButton.textContent = "×";

      removeButton.addEventListener("click", function (event) {
        event.stopPropagation();
        removeSelectedImage(index);
      });

      item.appendChild(img);
      item.appendChild(removeButton);
      imagePreviewList.appendChild(item);
    };

    reader.readAsDataURL(file);
  });

  imagePreviewArea.classList.remove("hidden");
}

/* 画像1枚削除 */
function removeSelectedImage(index) {
  selectedImageFiles.splice(index, 1);
  syncNoteImageInput();
  renderImagePreviews();
}

/* 画像全部削除 */
removeImageButton.addEventListener("click", function () {
  selectedImageFiles = [];
  syncNoteImageInput();
  renderImagePreviews();
});

/* -------------------------
   ④ アーカイブ
------------------------- */
//archiveButton.addEventListener("click", function () {
//  archived = true;
//  alert("アーカイブしました");
//  closeAllPopovers();
//});

/* -------------------------
   ⑤ 詳細メニュー → 削除
------------------------- */
deleteButton.addEventListener("click", function () {
  deletedDraft = {
    title: noteTitle.value,
    content: noteContent.value,
    colorClasses: Array.from(noteForm.classList).filter((cls) =>
      cls.startsWith("color-")
    ),
    memberText: memberPreview.textContent,
    images: [...selectedImageFiles]
  };

  noteTitle.value = "";
  noteContent.value = "";
  memberPreview.textContent = "";

  selectedImageFiles = [];
  syncNoteImageInput();
  renderImagePreviews();

  noteForm.classList.remove(
    "color-yellow",
    "color-blue",
    "color-green",
    "color-pink",
    "color-gray"
  );

  alert("下書きを削除しました");
  closeAllPopovers();
  closeNoteFormIfEmpty();
});

/* -------------------------
   ⑥ undo
------------------------- */
undoButton.addEventListener("click", function (e) {
  e.stopPropagation();

  if (deletedDraft) {
    noteTitle.value = deletedDraft.title;
    noteContent.value = deletedDraft.content;
    memberPreview.textContent = deletedDraft.memberText || "";

    selectedImageFiles = [...(deletedDraft.images || [])];
    syncNoteImageInput();
    renderImagePreviews();

    noteForm.classList.remove(
      "color-yellow",
      "color-blue",
      "color-green",
      "color-pink",
      "color-gray"
    );

    deletedDraft.colorClasses.forEach((cls) => noteForm.classList.add(cls));

    openNoteForm();
    deletedDraft = null;
  }
});

/* -------------------------
   ⑦ redo
------------------------- */
redoButton.addEventListener("click", function (e) {
  e.stopPropagation();

  if (
    noteTitle.value !== "" ||
    noteContent.value !== "" ||
    selectedImageFiles.length > 0
  ) {
    deletedDraft = {
      title: noteTitle.value,
      content: noteContent.value,
      colorClasses: Array.from(noteForm.classList).filter((cls) =>
        cls.startsWith("color-")
      ),
      memberText: memberPreview.textContent,
      images: [...selectedImageFiles]
    };

    noteTitle.value = "";
    noteContent.value = "";
    memberPreview.textContent = "";

    selectedImageFiles = [];
    syncNoteImageInput();
    renderImagePreviews();

    noteForm.classList.remove(
      "color-yellow",
      "color-blue",
      "color-green",
      "color-pink",
      "color-gray"
    );

    closeNoteFormIfEmpty();
  }
});

/* -------------------------
   submit前にfilesを最終同期
------------------------- */
noteForm.addEventListener("submit", function () {
  syncNoteImageInput();
});

