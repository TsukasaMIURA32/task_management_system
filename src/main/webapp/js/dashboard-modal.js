/* =========================
   編集モーダル
========================= */

var editModalOverlay = document.getElementById("editModalOverlay");
var editNoteForm = document.getElementById("editNoteForm");
var editTargetId = document.getElementById("editTargetId");
var editNoteTitle = document.getElementById("editNoteTitle");
var editNoteContent = document.getElementById("editNoteContent");
var closeEditModal = document.getElementById("closeEditModal");
var deleteEditNote = document.getElementById("deleteEditNote");

var editMemberNameInput = document.getElementById("editMemberNameInput");
var editAddMemberButton = document.getElementById("editAddMemberButton");
var editMemberPreview = document.getElementById("editMemberPreview");

var editImageButton = document.getElementById("editImageButton");
var editNoteImage = document.getElementById("editNoteImage");

var editUndoButton = document.getElementById("editUndoButton");
var editRedoButton = document.getElementById("editRedoButton");

var noteCards = document.querySelectorAll(".note-card");

var deletedEditDraft = null;
var editArchived = false;

/* ガード */
if (
  editModalOverlay &&
  editNoteForm &&
  editTargetId &&
  editNoteTitle &&
  editNoteContent
) {
  var editPopoverButtons = editNoteForm.querySelectorAll("[data-edit-popover]");
  var editColorChips = editNoteForm.querySelectorAll("[data-edit-color]");
  var editPopovers = editNoteForm.querySelectorAll(".popover-panel");

  var NOTE_COLOR_CLASSES = [
    "color-yellow",
    "color-blue",
    "color-green",
    "color-pink"
  ];

  function clearEditFormColors() {
    var i;
    for (i = 0; i < NOTE_COLOR_CLASSES.length; i++) {
      editNoteForm.classList.remove(NOTE_COLOR_CLASSES[i]);
    }
  }

  function setEditFormColor(colorClass) {
    clearEditFormColors();

    if (colorClass) {
      var i;
      for (i = 0; i < NOTE_COLOR_CLASSES.length; i++) {
        if (NOTE_COLOR_CLASSES[i] === colorClass) {
          editNoteForm.classList.add(colorClass);
          break;
        }
      }
    }
  }

  function clearEditChipActive() {
    var i;
    for (i = 0; i < editColorChips.length; i++) {
      editColorChips[i].classList.remove("active");
    }
  }

  function setActiveEditChipByColor(colorClass) {
    var selector;
    var targetChip;

    clearEditChipActive();

    if (colorClass) {
      selector = '[data-edit-color="' + colorClass + '"]';
    } else {
      selector = '[data-edit-color=""]';
    }

    targetChip = editNoteForm.querySelector(selector);

    if (targetChip) {
      targetChip.classList.add("active");
    }
  }

  function getCardColorClass(card) {
    var i;
    for (i = 0; i < NOTE_COLOR_CLASSES.length; i++) {
      if (card.classList.contains(NOTE_COLOR_CLASSES[i])) {
        return NOTE_COLOR_CLASSES[i];
      }
    }
    return "";
  }

  function closeEditPopovers() {
    var i;
    for (i = 0; i < editPopovers.length; i++) {
      editPopovers[i].classList.remove("show");
      editPopovers[i].style.left = "";
      editPopovers[i].style.top = "";
    }
  }

  function openEditModal(card) {
    var noteId, title, content, colorClass;
    var titleElement, contentElement;

    if (!card) {
      return;
    }

    noteId = card.getAttribute("data-note-id") || "";

    titleElement = card.querySelector("h3");
    contentElement = card.querySelector("p");

    title = titleElement ? titleElement.textContent : "";
    content = contentElement ? contentElement.textContent : "";
    colorClass = getCardColorClass(card);

    editTargetId.value = noteId;
    editNoteTitle.value = title;
    editNoteContent.value = content;

    setEditFormColor(colorClass);
    setActiveEditChipByColor(colorClass);

    closeEditPopovers();
    editModalOverlay.classList.add("show");
  }

  function closeEditModalFn() {
    closeEditPopovers();
    editModalOverlay.classList.remove("show");
  }

  /* -------------------------
     カードクリックで開く
  ------------------------- */
  var i;
  for (i = 0; i < noteCards.length; i++) {
    noteCards[i].addEventListener("click", function () {
      openEditModal(this);
    });
  }

  /* -------------------------
     モーダルを閉じる
  ------------------------- */
  if (closeEditModal) {
    closeEditModal.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();
      closeEditModalFn();
    });
  }

  /* オーバーレイ部分クリックで閉じる */
  editModalOverlay.addEventListener("click", function (e) {
    if (e.target === editModalOverlay) {
      closeEditModalFn();
    }
  });

  /* フォーム内部クリックでは閉じない */
  editNoteForm.addEventListener("click", function (e) {
    e.stopPropagation();
  });

  /* -------------------------
     モーダル内ポップオーバー
  ------------------------- */
  for (i = 0; i < editPopoverButtons.length; i++) {
    editPopoverButtons[i].addEventListener("click", function (e) {
      var targetId;
      var targetPopover;
      var alreadyOpen;
      var buttonRect;
      var formRect;
      var left;
      var top;

      e.preventDefault();
      e.stopPropagation();

      targetId = this.getAttribute("data-edit-popover");
      if (!targetId) {
        return;
      }

      targetPopover = document.getElementById(targetId);
      if (!targetPopover) {
        return;
      }

      alreadyOpen = targetPopover.classList.contains("show");
      closeEditPopovers();

      if (!alreadyOpen) {
        buttonRect = this.getBoundingClientRect();
        formRect = editNoteForm.getBoundingClientRect();

        left = buttonRect.left - formRect.left;
        top = buttonRect.bottom - formRect.top + 8;

        targetPopover.style.left = left + "px";
        targetPopover.style.top = top + "px";
        targetPopover.classList.add("show");
      }
    });
  }

  for (i = 0; i < editPopovers.length; i++) {
    editPopovers[i].addEventListener("click", function (e) {
      e.stopPropagation();
    });
  }

  /* -------------------------
     色選択
  ------------------------- */
  for (i = 0; i < editColorChips.length; i++) {
    editColorChips[i].addEventListener("click", function (e) {
      var colorClass;

      e.preventDefault();
      e.stopPropagation();

      colorClass = this.getAttribute("data-edit-color") || "";

      setActiveEditChipByColor(colorClass);
      setEditFormColor(colorClass);
      closeEditPopovers();
    });
  }

  /* -------------------------
     画像ボタン
  ------------------------- */
  if (editImageButton && editNoteImage) {
    editImageButton.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();
      closeEditPopovers();
      editNoteImage.click();
    });
  }

  /* -------------------------
     保存
  ------------------------- */
  editNoteForm.addEventListener("submit", function (e) {
    var noteId;
    var targetCard;
    var titleEl;
    var contentEl;
    var selectedColorClass;
    var j;

    e.preventDefault();

    noteId = editTargetId.value;
    targetCard = document.querySelector('.note-card[data-note-id="' + noteId + '"]');

    if (!targetCard) {
      return;
    }

    titleEl = targetCard.querySelector("h3");
    contentEl = targetCard.querySelector("p");

    if (titleEl) {
      titleEl.textContent = editNoteTitle.value.replace(/^\s+|\s+$/g, "");
    }

    if (contentEl) {
      contentEl.textContent = editNoteContent.value.replace(/^\s+|\s+$/g, "");
    }

    for (j = 0; j < NOTE_COLOR_CLASSES.length; j++) {
      targetCard.classList.remove(NOTE_COLOR_CLASSES[j]);
    }

    selectedColorClass = "";
    for (j = 0; j < NOTE_COLOR_CLASSES.length; j++) {
      if (editNoteForm.classList.contains(NOTE_COLOR_CLASSES[j])) {
        selectedColorClass = NOTE_COLOR_CLASSES[j];
        break;
      }
    }

    if (selectedColorClass !== "") {
      targetCard.classList.add(selectedColorClass);
    }

    closeEditModalFn();
  });

  /* -------------------------
     削除
  ------------------------- */
  if (deleteEditNote) {
    deleteEditNote.addEventListener("click", function (e) {
      var noteId;
      var targetCard;

      e.preventDefault();
      e.stopPropagation();

      noteId = editTargetId.value;
      targetCard = document.querySelector('.note-card[data-note-id="' + noteId + '"]');

      if (!targetCard) {
        return;
      }

      targetCard.parentNode.removeChild(targetCard);
      closeEditModalFn();
    });
  }

  /* -------------------------
     Escキーで閉じる
  ------------------------- */
  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape" && editModalOverlay.classList.contains("show")) {
      closeEditModalFn();
    }
  });

  /* -------------------------
     モーダル内以外クリックで
     ポップオーバーだけ閉じる
  ------------------------- */
  document.addEventListener("click", function () {
    if (editModalOverlay.classList.contains("show")) {
      closeEditPopovers();
    }
  });
}