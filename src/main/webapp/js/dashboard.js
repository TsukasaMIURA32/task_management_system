const menuToggle = document.getElementById("menuToggle");
const sidebar = document.getElementById("sidebar");
const contentArea = document.getElementById("contentArea");

const noteForm = document.getElementById("noteForm");
const noteTitle = document.getElementById("noteTitle");
const noteContent = document.getElementById("noteContent");
const closeNoteForm = document.getElementById("closeNoteForm");

const imageUploadButton = document.getElementById("imageUploadButton");
const noteImage = document.getElementById("noteImage");

/* popover関連 */
const popoverButtons = noteForm.querySelectorAll("[data-popover]");
const popovers = noteForm.querySelectorAll(".popover-panel");
const colorChips = noteForm.querySelectorAll("[data-color]");

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

  if (titleValue === "" && contentValue === "") {
	resetNoteColor(); 
	
    noteForm.classList.remove("expanded");
    noteForm.classList.add("collapsed");
  }
}

function forceCloseNoteForm() {
  noteTitle.value = "";
  noteContent.value = "";
  noteImage.value = "";
  
  resetNoteColor();   
  
  memberPreview.textContent = "";
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
  noteImage.click();
});

/* -------------------------
   閉じる処理
------------------------- */
closeNoteForm.addEventListener("click", function (e) {
  e.preventDefault();
  e.stopPropagation();
  forceCloseNoteForm();
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
   ① 背景色選択
------------------------- */
/* -------------------------
   カラー選択
------------------------- */

//const colorChips = document.querySelectorAll(".color-chip");

colorChips.forEach(chip => {
  chip.addEventListener("click", function () {

    // いま付いている active を全部外す
    colorChips.forEach(c => c.classList.remove("active"));

    // 押したものだけ active
    chip.classList.add("active");

    // フォームの色変更
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

  });
});
/* -------------------------
   背景色リセット
------------------------- */
function resetNoteColor() {

  // noteFormの色クラス削除
  noteForm.classList.remove(
    "color-yellow",
    "color-blue",
    "color-green",
    "color-pink",
    "color-gray"
  );

  // カラーチップのactive解除
  document.querySelectorAll(".color-chip")
    .forEach(chip => chip.classList.remove("active"));
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
   ③ 画像登録
------------------------- */
imageSelectButton.addEventListener("click", function () {
  noteImage.click();
  closeAllPopovers();
});

/* -------------------------
   ④ アーカイブ
------------------------- */
archiveButton.addEventListener("click", function () {
  archived = true;
  alert("アーカイブしました");
  closeAllPopovers();
});

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
  };

  noteTitle.value = "";
  noteContent.value = "";
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

  if (noteTitle.value !== "" || noteContent.value !== "") {
    deletedDraft = {
      title: noteTitle.value,
      content: noteContent.value,
      colorClasses: Array.from(noteForm.classList).filter((cls) =>
        cls.startsWith("color-")
      ),
    };

    noteTitle.value = "";
    noteContent.value = "";
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