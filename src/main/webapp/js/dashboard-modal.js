const editModalOverlay = document.getElementById("editModalOverlay");
const editNoteForm = document.getElementById("editNoteForm");
const editTargetId = document.getElementById("editTargetId");
const editNoteTitle = document.getElementById("editNoteTitle");
const editNoteContent = document.getElementById("editNoteContent");
const closeEditModal = document.getElementById("closeEditModal");

const editImageButton = document.getElementById("editImageButton");
const editNoteImage = document.getElementById("editNoteImage");
const editNoteColorId = document.getElementById("editNoteColorId");
const editDeleteImageIds = document.getElementById("editDeleteImageIds");

const editImagePreviewArea = document.getElementById("editImagePreviewArea");
const editImagePreviewList = document.getElementById("editImagePreviewList");

const editDeleteButton = document.getElementById("editDeleteButton");

const noteCards = document.querySelectorAll(".note-card");

let selectedNewFiles = [];

if (
  editModalOverlay &&
  editNoteForm &&
  editTargetId &&
  editNoteTitle &&
  editNoteContent
) {
  const editPopoverButtons = editNoteForm.querySelectorAll("[data-edit-popover]");
  const editColorChips = editNoteForm.querySelectorAll("[data-edit-color]");
  const editPopovers = editNoteForm.querySelectorAll(".popover-panel");

  const NOTE_COLOR_CLASSES = [
    "color-yellow",
    "color-blue",
    "color-green",
    "color-pink"
  ];

  const clearEditFormColors = () => {
    NOTE_COLOR_CLASSES.forEach((colorClass) => {
      editNoteForm.classList.remove(colorClass);
    });
  };

  const setEditFormColor = (colorClass) => {
    clearEditFormColors();
    if (colorClass) {
      editNoteForm.classList.add(colorClass);
    }
  };

  const clearEditChipActive = () => {
    editColorChips.forEach((chip) => chip.classList.remove("active"));
  };

  const setActiveEditChipByColor = (colorClass) => {
    clearEditChipActive();

    const selector = colorClass
      ? `[data-edit-color="${colorClass}"]`
      : `[data-edit-color=""]`;

    const targetChip = editNoteForm.querySelector(selector);

    if (targetChip) {
      targetChip.classList.add("active");
    }
  };

  const getColorClassByColorId = (colorId) => {
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
  };

  const getColorIdByColorClass = (colorClass) => {
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
  };

  const closeEditPopovers = () => {
    editPopovers.forEach((popover) => {
      popover.classList.remove("show");
      popover.style.left = "";
      popover.style.top = "";
    });
  };

  const closeEditModalFn = () => {
    closeEditPopovers();
    editModalOverlay.classList.remove("show");
  };

  const addDeleteImageId = (imageId) => {
    if (!editDeleteImageIds) return;

    const currentIds = editDeleteImageIds.value
      ? editDeleteImageIds.value.split(",").filter((id) => id !== "")
      : [];

    if (!currentIds.includes(String(imageId))) {
      currentIds.push(String(imageId));
      editDeleteImageIds.value = currentIds.join(",");
    }
  };

  const getAllPreviewItems = () => {
    return Array.from(editImagePreviewList.querySelectorAll(".image-preview-item"));
  };

  const updatePreviewGridClass = () => {
    if (!editImagePreviewArea || !editImagePreviewList) return;

    const items = getAllPreviewItems();
    editImagePreviewList.className = "image-preview-list";

    if (items.length === 0) {
      editImagePreviewArea.classList.add("hidden");
      return;
    }

    const gridClass = `image-grid-${items.length >= 4 ? 4 : items.length}`;
    editImagePreviewList.classList.add(gridClass);
    editImagePreviewArea.classList.remove("hidden");
  };

  const bindPreviewRemoveEvents = () => {
    const removeButtons = editImagePreviewList.querySelectorAll(".image-remove-button");

    removeButtons.forEach((button) => {
      button.onclick = (e) => {
        e.preventDefault();
        e.stopPropagation();

        const imageType = button.getAttribute("data-image-type");
        const imageItem = button.closest(".image-preview-item");

        if (!imageItem) return;

        if (imageType === "existing") {
          const imageId = button.getAttribute("data-image-id");
          if (imageId) {
            addDeleteImageId(imageId);
          }
          imageItem.remove();
        }

        if (imageType === "new") {
          const newIndex = Number(button.getAttribute("data-new-index"));
          selectedNewFiles = selectedNewFiles.filter((_, index) => index !== newIndex);
          syncFileInputFromSelectedFiles();
          renderUnifiedPreview();
          return;
        }

        updatePreviewGridClass();
      };
    });
  };

  const syncFileInputFromSelectedFiles = () => {
    if (!editNoteImage) return;

    const dataTransfer = new DataTransfer();
    selectedNewFiles.forEach((file) => dataTransfer.items.add(file));
    editNoteImage.files = dataTransfer.files;
  };

  const renderUnifiedPreview = (existingImageIdList = null) => {
    if (!editImagePreviewArea || !editImagePreviewList) return;

    editImagePreviewList.innerHTML = "";

    if (existingImageIdList !== null) {
      existingImageIdList.forEach((imageId) => {
        const item = document.createElement("div");
        item.className = "image-preview-item existing-image-item";
        item.dataset.imageId = imageId;
        item.dataset.imageType = "existing";

        item.innerHTML = `
          <img src="${window.contextPath}/task/image?imageId=${imageId}" alt="タスク画像">
          <button type="button" class="image-remove-button"
            data-image-id="${imageId}" data-image-type="existing">×</button>
        `;

        editImagePreviewList.appendChild(item);
      });
    }

    selectedNewFiles.forEach((file, index) => {
      const reader = new FileReader();

      reader.onload = (e) => {
        const item = document.createElement("div");
        item.className = "image-preview-item new-image-item";
        item.dataset.imageType = "new";
        item.dataset.newIndex = index;

        item.innerHTML = `
          <img src="${e.target.result}" alt="新規画像">
          <button type="button" class="image-remove-button"
            data-image-type="new" data-new-index="${index}">×</button>
        `;

        editImagePreviewList.appendChild(item);
        updatePreviewGridClass();
        bindPreviewRemoveEvents();
      };

      reader.readAsDataURL(file);
    });

    updatePreviewGridClass();
    bindPreviewRemoveEvents();
  };

  const openEditModalByAjax = (taskId) => {
    fetch(`${window.contextPath}/task/detail?taskId=${encodeURIComponent(taskId)}`)
      .then((response) => {
        if (!response.ok) {
          throw new Error("タスク詳細の取得に失敗しました");
        }
        return response.json();
      })
      .then((task) => {
        editTargetId.value = task.id || "";
        editNoteTitle.value = task.title || "";
        editNoteContent.value = task.content || "";

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

        closeEditPopovers();
        editModalOverlay.classList.add("show");
      })
      .catch((error) => {
        console.error(error);
        alert("タスク詳細の取得に失敗しました。");
      });
  };

  noteCards.forEach((card) => {
    card.addEventListener("click", () => {
      const noteId = card.getAttribute("data-note-id");
      if (!noteId) return;

      openEditModalByAjax(noteId);
    });
  });

  if (closeEditModal) {
    closeEditModal.addEventListener("click", (e) => {
      e.preventDefault();
      e.stopPropagation();
      editNoteForm.submit();
    });
  }

  editModalOverlay.addEventListener("click", (e) => {
    if (e.target === editModalOverlay) {
      closeEditModalFn();
    }
  });
  
  if (editDeleteButton) {
    editDeleteButton.addEventListener("click", (e) => {
      e.preventDefault();
      e.stopPropagation();

      const taskId = editTargetId.value;
      if (!taskId) return;

      const result = confirm("このタスクを削除しますか？");
      if (!result) return;

      const form = document.createElement("form");
      form.method = "post";
      form.action = `${window.contextPath}/task/delete`;

      const input = document.createElement("input");
      input.type = "hidden";
      input.name = "taskId";
      input.value = taskId;

      form.appendChild(input);
      document.body.appendChild(form);
      form.submit();
    });
  }

  editNoteForm.addEventListener("click", (e) => {
    e.stopPropagation();
  });

  editPopoverButtons.forEach((button) => {
    button.addEventListener("click", (e) => {
      e.preventDefault();
      e.stopPropagation();

      const targetId = button.getAttribute("data-edit-popover");
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
    popover.addEventListener("click", (e) => {
      e.stopPropagation();
    });
  });

  editColorChips.forEach((chip) => {
    chip.addEventListener("click", (e) => {
      e.preventDefault();
      e.stopPropagation();

      const colorClass = chip.getAttribute("data-edit-color") || "";
      const colorId = getColorIdByColorClass(colorClass);

      if (editNoteColorId) {
        editNoteColorId.value = colorId;
      }

      setActiveEditChipByColor(colorClass);
      setEditFormColor(colorClass);
      closeEditPopovers();
    });
  });

  if (editImageButton && editNoteImage) {
    editImageButton.addEventListener("click", (e) => {
      e.preventDefault();
      e.stopPropagation();
      closeEditPopovers();
      editNoteImage.click();
    });

    editNoteImage.addEventListener("change", () => {
      const newFiles = Array.from(editNoteImage.files || []);
      selectedNewFiles = [...selectedNewFiles, ...newFiles];
      syncFileInputFromSelectedFiles();

      const existingItems = Array.from(
        editImagePreviewList.querySelectorAll('.existing-image-item')
      ).map((item) => item.dataset.imageId);

      renderUnifiedPreview(existingItems);
    });
  }

  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && editModalOverlay.classList.contains("show")) {
      closeEditModalFn();
    }
  });

  document.addEventListener("click", () => {
    if (editModalOverlay.classList.contains("show")) {
      closeEditPopovers();
    }
  });
}