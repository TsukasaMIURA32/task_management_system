/* =========================
   レイアウト関連の要素
   ========================= */
const menuToggle = document.getElementById("menuToggle");
const sidebar = document.getElementById("sidebar");
const contentArea = document.getElementById("contentArea");

/* =========================
   新規タスク作成フォーム関連の要素
   ========================= */
const noteForm = document.getElementById("noteForm");
const noteTitle = document.getElementById("noteTitle");
const noteContent = document.getElementById("noteContent");
const closeNoteForm = document.getElementById("closeNoteForm");

/* collapsed時の右上画像アイコンと、実際の file input */
const imageUploadButton = document.getElementById("imageUploadButton");
const noteImage = document.getElementById("noteImage");

/* =========================
   画像プレビュー関連の要素
   ========================= */
const imagePreviewArea = document.getElementById("imagePreviewArea");
const imagePreviewList = document.getElementById("imagePreviewList");
const removeImageButton = document.getElementById("removeImageButton");

/* =========================
   popover関連の要素
   ========================= */
const popoverButtons = noteForm.querySelectorAll("[data-popover]");
const popovers = noteForm.querySelectorAll(".popover-panel");
const colorChips = noteForm.querySelectorAll("[data-color]");
const noteColorId = document.getElementById("noteColorId");

/* =========================
   ツールボタン類
   ========================= */
const imageSelectButton = document.getElementById("imageSelectButton");
const archiveButton = document.getElementById("archiveButton");
const deleteButton = document.getElementById("deleteButton");
const undoButton = document.getElementById("undoButton");
const redoButton = document.getElementById("redoButton");

/* =========================
   共同編集者モーダル関連
   ========================= */
const openCreateMemberModal = document.getElementById("openCreateMemberModal");
const createMemberModalOverlay = document.getElementById("createMemberModalOverlay");
const closeCreateMemberModal = document.getElementById("closeCreateMemberModal");
const doneCreateMemberModal = document.getElementById("doneCreateMemberModal");

/* =========================
   一時状態管理用の変数
   ========================= */

/* 削除前の下書きを一時退避するための変数（今は主に削除時用） */
let deletedDraft = null;

/* アーカイブ状態用。今は未使用に近い */
let archived = false;

/* 
  新規作成フォームで選択された画像ファイルを保持する配列
  file input は選び直し時に扱いづらいので、JS側でも持っておく
*/
let selectedImageFiles = [];

/* =========================
   サイドバー開閉
   ========================= */

/* 
  ハンバーガーボタン押下時
  - サイドバーに closed クラスを付け外し
  - メイン表示領域にも sidebar-closed を付け外し
*/
menuToggle.addEventListener("click", function () {
  sidebar.classList.toggle("closed");
  contentArea.classList.toggle("sidebar-closed");
});

/* =========================
   新規フォームの開閉
   ========================= */

/* フォームを expanded 状態にする */
function openNoteForm() {
  noteForm.classList.remove("collapsed");
  noteForm.classList.add("expanded");
}

/* 
  タイトル・本文・画像がすべて空ならフォームを閉じる
  クリックアウト時などに使う
*/
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

/* 
  フォームを強制的に初期状態へ戻す
  - 入力内容クリア
  - 画像クリア
  - 色クリア
  - 共同編集者クリア
  - popoverも閉じる
*/
function forceCloseNoteForm() {
  noteTitle.value = "";
  noteContent.value = "";

  selectedImageFiles = [];
  syncNoteImageInput();
  renderImagePreviews();

  resetNoteColor();

  if (window.createMemberSelectorApi) {
    window.createMemberSelectorApi.clearUsers();
  }

  noteForm.classList.remove("expanded");
  noteForm.classList.add("collapsed");
  closeAllPopovers();
}

/* =========================
   フォームを開くきっかけになるイベント
   ========================= */

/* 本文クリックでフォームを開く */
noteContent.addEventListener("click", function (e) {
  e.stopPropagation();
  openNoteForm();
});

/* 本文フォーカスでもフォームを開く */
noteContent.addEventListener("focus", function () {
  openNoteForm();
});

/* タイトルフォーカスでもフォームを開く */
noteTitle.addEventListener("focus", function () {
  openNoteForm();
});

/* フォーム内クリックは外側クリック扱いにしない */
noteForm.addEventListener("click", function (e) {
  e.stopPropagation();
});

/* =========================
   collapsed時の右上画像ボタン
   ========================= */

/* 
  フォームが閉じている状態でも画像だけ先に選べるようにする
  クリック時にフォームを開いて file input を押す
*/
imageUploadButton.addEventListener("click", function (e) {
  e.stopPropagation();
  openNoteForm();
});

/* =========================
   閉じるボタン処理
   ========================= */

/* 
  現在フォームに何か入力があるか判定
  画像が1枚でもあれば「入力あり」とみなす
*/
function hasNoteInput() {
  const titleValue = noteTitle.value.trim();
  const contentValue = noteContent.value.trim();
  const hasImage = selectedImageFiles.length > 0;

  return titleValue !== "" || contentValue !== "" || hasImage;
}

/* 
  閉じるボタン押下時
  - 入力があれば submit
  - 何もなければフォームを初期化して閉じる
*/
closeNoteForm.addEventListener("click", function (e) {
  e.preventDefault();
  e.stopPropagation();
  submitOrCloseNoteForm();
});

/* 
  画面のどこかをクリックしたとき
  - 空ならフォームを閉じる
  - 開いているpopoverを閉じる
*/
document.addEventListener("click", function () {
  if (noteForm.classList.contains("expanded") || hasNoteInput()) {
    submitOrCloseNoteForm();
  }
  closeAllPopovers();
});

function submitOrCloseNoteForm() {
  if (hasNoteInput()) {
    syncNoteImageInput();
    noteForm.submit();
  } else {
    forceCloseNoteForm();
  }
}

/* =========================
   popover共通処理
   ========================= */

/* すべてのpopoverを閉じる */
function closeAllPopovers() {
  popovers.forEach(function (popover) {
    popover.classList.remove("show");
  });
}

/* 
  背景色・詳細メニューなどのpopoverボタン押下時
  - フォームを開く
  - 対象popoverの位置を計算
  - そのpopoverだけ表示
*/
popoverButtons.forEach(function (button) {
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

/* popover内部クリックは外側クリック扱いにしない */
popovers.forEach(function (popover) {
  popover.addEventListener("click", function (e) {
    e.stopPropagation();
  });
});

/* =========================
   背景色選択
   ========================= */

/* 
  色チップ押下時
  - active表示を切り替える
  - formの背景色クラスを切り替える
  - hiddenのcolorIdを更新する
*/
colorChips.forEach(function (chip) {
  chip.addEventListener("click", function () {
    colorChips.forEach(function (c) {
      c.classList.remove("active");
    });

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

/* 
  背景色をデフォルト状態に戻す
  色クラスもactive表示も消す
*/
function resetNoteColor() {
  noteForm.classList.remove(
    "color-yellow",
    "color-blue",
    "color-green",
    "color-pink",
    "color-gray"
  );

  document.querySelectorAll(".color-chip").forEach(function (chip) {
    chip.classList.remove("active");
  });

  noteColorId.value = 1;
}

/* =========================
   画像追加（Google Keep風）
   ========================= */

/* ツールバーの画像追加ボタン押下 */
imageSelectButton.addEventListener("click", function () {
  openNoteForm();
  noteImage.click();
  closeAllPopovers();
});

/* 
  file input で画像選択後
  - 最大4枚まで追加
  - 重複ファイルは除外
  - input.files とプレビューを更新
*/
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

    const isDuplicate = selectedImageFiles.some(function (existing) {
      return (
        existing.name === file.name &&
        existing.size === file.size &&
        existing.lastModified === file.lastModified
      );
    });

    if (!isDuplicate) {
      selectedImageFiles.push(file);
    }
  }

  syncNoteImageInput();
  renderImagePreviews();
  openNoteForm();

  /* 同じ画像を再選択できるよう input値を空にする */
  noteImage.value = "";
});

/* 
  JSで保持している selectedImageFiles を
  実際の input[type=file] に反映する
*/
function syncNoteImageInput() {
  const dataTransfer = new DataTransfer();

  selectedImageFiles.forEach(function (file) {
    dataTransfer.items.add(file);
  });

  noteImage.files = dataTransfer.files;
}

/* 
  現在選択中の画像一覧をプレビュー描画する
  枚数に応じて grid クラスも切り替える
*/
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

  selectedImageFiles.forEach(function (file, index) {
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

      /* 画像ごとの×ボタンでその画像だけ削除 */
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

/* 指定indexの画像1枚だけ削除 */
function removeSelectedImage(index) {
  selectedImageFiles.splice(index, 1);
  syncNoteImageInput();
  renderImagePreviews();
}

/* 画像全部削除ボタン押下 */
removeImageButton.addEventListener("click", function () {
  selectedImageFiles = [];
  syncNoteImageInput();
  renderImagePreviews();
});

/* =========================
   詳細メニュー → 下書き削除
   ========================= */

/* 
  新規フォームの入力内容を丸ごとクリアする
  必要に応じて deletedDraft に退避
*/
deleteButton.addEventListener("click", function () {
  deletedDraft = {
    title: noteTitle.value,
    content: noteContent.value,
    colorClasses: Array.from(noteForm.classList).filter(function (cls) {
      return cls.startsWith("color-");
    }),
    images: [].concat(selectedImageFiles)
  };

  noteTitle.value = "";
  noteContent.value = "";

  selectedImageFiles = [];
  syncNoteImageInput();
  renderImagePreviews();

  if (window.createMemberSelectorApi) {
    window.createMemberSelectorApi.clearUsers();
  }

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

/* =========================
   共同編集者モーダル開閉
   ========================= */

/* 共同編集者モーダルを開く */
function openCreateMemberModalFn() {
  if (createMemberModalOverlay) {
    createMemberModalOverlay.classList.add("show");
  }
}

/* 共同編集者モーダルを閉じる */
function closeCreateMemberModalFn() {
  if (createMemberModalOverlay) {
    createMemberModalOverlay.classList.remove("show");
  }
}

/* ツールバーの共同編集者ボタン押下 */
if (openCreateMemberModal) {
  openCreateMemberModal.addEventListener("click", function (e) {
    e.preventDefault();
    e.stopPropagation();
    openNoteForm();
    closeAllPopovers();
    openCreateMemberModalFn();
  });
}

/* ×ボタンで閉じる */
if (closeCreateMemberModal) {
  closeCreateMemberModal.addEventListener("click", function () {
    closeCreateMemberModalFn();
  });
}

/* フッターの閉じるボタンでも閉じる */
if (doneCreateMemberModal) {
  doneCreateMemberModal.addEventListener("click", function () {
    closeCreateMemberModalFn();
  });
}

/* オーバーレイ背景クリックで閉じる */
if (createMemberModalOverlay) {
  createMemberModalOverlay.addEventListener("click", function (e) {
    if (e.target === createMemberModalOverlay) {
      closeCreateMemberModalFn();
    }
  });
}

/* =========================
   form送信前の最終同期
   ========================= */

/* 送信直前に file input に画像一覧を反映 */
noteForm.addEventListener("submit", function () {
  syncNoteImageInput();
});