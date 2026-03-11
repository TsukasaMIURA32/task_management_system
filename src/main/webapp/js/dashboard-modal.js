/* =========================
   編集モーダル
========================= */

const editModalOverlay = document.getElementById("editModalOverlay");
const editNoteForm = document.getElementById("editNoteForm");
const editTargetId = document.getElementById("editTargetId");
const editNoteTitle = document.getElementById("editNoteTitle");
const editNoteContent = document.getElementById("editNoteContent");
const closeEditModal = document.getElementById("closeEditModal");
const deleteEditNote = document.getElementById("deleteEditNote");

const editPopoverButtons = editNoteForm.querySelectorAll("[data-edit-popover]");
const editColorChips = editNoteForm.querySelectorAll("[data-edit-color]");
const editPopovers = document.querySelectorAll("#editNoteForm .popover-panel");

const editMemberNameInput = document.getElementById("editMemberNameInput");
const editAddMemberButton = document.getElementById("editAddMemberButton");
const editMemberPreview = document.getElementById("editMemberPreview");

const editImageButton = document.getElementById("editImageButton");
const editNoteImage = document.getElementById("editNoteImage");

const editUndoButton = document.getElementById("editUndoButton");
const editRedoButton = document.getElementById("editRedoButton");

const noteCards = document.querySelectorAll(".note-card");


let deletedEditDraft = null;
let editArchived = false;
/* ガード */
if (
  editModalOverlay &&
  editNoteForm &&
  editTargetId &&
  editNoteTitle &&
  editNoteContent
) {
  const NOTE_COLOR_CLASSES = [
    "color-yellow",
    "color-blue",
    "color-green",
    "color-pink"
  ];

  
  function clearEditFormColors() {
    editNoteForm.classList.remove(...NOTE_COLOR_CLASSES);
  }

  function setEditFormColor(colorClass) {
    clearEditFormColors();

    if (colorClass && NOTE_COLOR_CLASSES.includes(colorClass)) {
      editNoteForm.classList.add(colorClass);
    }
  }

  function clearEditChipActive() {
    editColorChips.forEach((chip) => chip.classList.remove("active"));
  }

  function setActiveEditChipByColor(colorClass) {
    clearEditChipActive();

    const selector = colorClass
      ? `[data-edit-color="${colorClass}"]`
      : `[data-edit-color=""]`;

    const targetChip = document.querySelector(selector);
    if (targetChip) {
      targetChip.classList.add("active");
    }
  }

  function getCardColorClass(card) {
    return NOTE_COLOR_CLASSES.find((cls) => card.classList.contains(cls)) || "";
  }

  function closeEditPopovers() {
    editPopovers.forEach((popover) => {
      popover.classList.remove("show");
      popover.style.left = "";
      popover.style.top = "";
    });
  }

  function openEditModal(card) {
    if (!card) return;

    const noteId = card.dataset.noteId || "";
    const title = card.querySelector("h3")?.textContent || "";
    const content = card.querySelector("p")?.textContent || "";
    const colorClass = getCardColorClass(card);

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
  noteCards.forEach((card) => {
    card.addEventListener("click", function () {
      openEditModal(card);
    });
  });

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
  editPopoverButtons.forEach((button) => {
    button.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();

      const targetId = button.dataset.editPopover;
      if (!targetId) return;

      const targetPopover = document.getElementById(targetId);
      if (!targetPopover) return;

      const alreadyOpen = targetPopover.classList.contains("show");
      closeEditPopovers();

      if (!alreadyOpen) {
        const buttonRect = button.getBoundingClientRect();
        const formRect = editNoteForm.getBoundingClientRect();

        const left = buttonRect.left - formRect.left;
        const top = buttonRect.bottom - formRect.top + 8;

        targetPopover.style.left = `${left}px`;
        targetPopover.style.top = `${top}px`;
        targetPopover.classList.add("show");
      }
    });
  });

  editPopovers.forEach((popover) => {
    popover.addEventListener("click", function (e) {
      e.stopPropagation();
    });
  });

  /* -------------------------
     色選択
  ------------------------- */
  editColorChips.forEach((chip) => {
    chip.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();

      const colorClass = chip.dataset.editColor || "";

      setActiveEditChipByColor(colorClass);
      setEditFormColor(colorClass);
      closeEditPopovers();
    });
  });

  /* -------------------------
     保存
  ------------------------- */
  editNoteForm.addEventListener("submit", function (e) {
    e.preventDefault();

    const noteId = editTargetId.value;
    const targetCard = document.querySelector(`.note-card[data-note-id="${noteId}"]`);
    if (!targetCard) return;

    const titleEl = targetCard.querySelector("h3");
    const contentEl = targetCard.querySelector("p");

    if (titleEl) {
      titleEl.textContent = editNoteTitle.value.trim();
    }

    if (contentEl) {
      contentEl.textContent = editNoteContent.value.trim();
    }

    targetCard.classList.remove(...NOTE_COLOR_CLASSES);

    const selectedColorClass =
      NOTE_COLOR_CLASSES.find((cls) => editNoteForm.classList.contains(cls)) || "";

    if (selectedColorClass) {
      targetCard.classList.add(selectedColorClass);
    }

    closeEditModalFn();
  });

  /* -------------------------
     削除
  ------------------------- */
  if (deleteEditNote) {
    deleteEditNote.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();

      const noteId = editTargetId.value;
      const targetCard = document.querySelector(`.note-card[data-note-id="${noteId}"]`);
      if (!targetCard) return;

      targetCard.remove();
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

if (editImageButton && editNoteImage) {
  editImageButton.addEventListener("click", function (e) {
    e.preventDefault();
    e.stopPropagation();
    closeEditPopovers();
    editNoteImage.click();
  });
}