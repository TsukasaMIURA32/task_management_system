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
const editSharedUsersText = document.getElementById("editSharedUsersText");

const noteCards = document.getElementsByClassName("note-card");

let selectedNewFiles = [];

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

  const editMemberSelector = document.querySelector('#editMemberPopover .member-selector');
  if (editMemberSelector && typeof initMemberSelector === "function") {
    window.editMemberSelectorApi = initMemberSelector(editMemberSelector);
  }

  function getEditPopoverButtons() {
    return editNoteForm.querySelectorAll("[data-edit-popover]");
  }

  function getEditColorChips() {
    return editNoteForm.querySelectorAll("[data-edit-color]");
  }

  function getEditPopovers() {
    return editNoteForm.getElementsByClassName("popover-panel");
  }

  function clearEditFormColors() {
    for (let i = 0; i < NOTE_COLOR_CLASSES.length; i++) {
      editNoteForm.classList.remove(NOTE_COLOR_CLASSES[i]);
    }
  }

  function setEditFormColor(colorClass) {
    clearEditFormColors();
    if (colorClass) {
      editNoteForm.classList.add(colorClass);
    }
  }

  function clearEditChipActive() {
    const editColorChips = getEditColorChips();
    for (let i = 0; i < editColorChips.length; i++) {
      editColorChips[i].classList.remove("active");
    }
  }

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

  function closeEditPopovers() {
    const editPopovers = getEditPopovers();

    for (let i = 0; i < editPopovers.length; i++) {
      editPopovers[i].classList.remove("show");
      editPopovers[i].style.left = "";
      editPopovers[i].style.top = "";
    }
  }

  function closeEditModalFn() {
    closeEditPopovers();
    editModalOverlay.classList.remove("show");
  }

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

  function getAllPreviewItems() {
    return editImagePreviewList.getElementsByClassName("image-preview-item");
  }

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

        if (imageType === "existing") {
          const imageId = button.getAttribute("data-image-id");
          if (imageId) {
            addDeleteImageId(imageId);
          }
          imageItem.remove();
          updatePreviewGridClass();
          return;
        }

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

  function renderUnifiedPreview(existingImageIdList) {
    if (!editImagePreviewArea || !editImagePreviewList) {
      return;
    }

    editImagePreviewList.innerHTML = "";

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
          'data-image-id="' + imageId + '" data-image-type="existing">×</button>';

        editImagePreviewList.appendChild(item);
      }
    }

    if (selectedNewFiles.length === 0) {
      updatePreviewGridClass();
      bindPreviewRemoveEvents();
      return;
    }

    for (let j = 0; j < selectedNewFiles.length; j++) {
      ((file, index) => {
        const reader = new FileReader();

        reader.onload = function (e) {
          const item = document.createElement("div");
          item.className = "image-preview-item new-image-item";
          item.dataset.imageType = "new";
          item.dataset.newIndex = index;

          item.innerHTML =
            '<img src="' + e.target.result + '" alt="新規画像">' +
            '<button type="button" class="image-remove-button" ' +
            'data-image-type="new" data-new-index="' + index + '">×</button>';

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

  function renderEditSharedUsers(sharedUsers) {
    if (!editSharedUsersText) {
      return;
    }

    if (!sharedUsers || sharedUsers.length === 0) {
      editSharedUsersText.innerHTML = "";
      return;
    }

    const names = sharedUsers.map(user => user.name).join("、");
    editSharedUsersText.innerHTML = `<i class="fas fa-users"></i>: ${names}`;
  }

  async function openEditModalByAjax(taskId) {
    try {
      const response = await fetch(
        window.contextPath + "/task/detail?taskId=" + encodeURIComponent(taskId)
      );

      if (!response.ok) {
        throw new Error("タスク詳細の取得に失敗しました");
      }

      const task = await response.json();
      console.log("task detail =", task);
      console.log("sharedUsers =", task.sharedUsers);

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
      editModalOverlay.classList.add("show");

    } catch (error) {
      console.error("タスク詳細取得エラー:", error);
      alert("タスク詳細の取得に失敗しました。");
    }
  }

  for (let i = 0; i < noteCards.length; i++) {
    noteCards[i].addEventListener("click", async function () {
      const noteId = this.getAttribute("data-note-id");
      if (!noteId) {
        return;
      }

      await openEditModalByAjax(noteId);
    });
  }

  if (closeEditModal) {
    closeEditModal.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();
      editNoteForm.submit();
    });
  }

  editModalOverlay.addEventListener("click", function (e) {
    if (e.target === editModalOverlay) {
      closeEditModalFn();
    }
  });

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

  editNoteForm.addEventListener("click", function (e) {
    e.stopPropagation();
  });

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

  const editPopovers = getEditPopovers();
  for (let i = 0; i < editPopovers.length; i++) {
    editPopovers[i].addEventListener("click", function (e) {
      e.stopPropagation();
    });
  }

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

  if (editImageButton && editNoteImage) {
    editImageButton.addEventListener("click", function (e) {
      e.preventDefault();
      e.stopPropagation();
      closeEditPopovers();
      editNoteImage.click();
    });

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

  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape" && editModalOverlay.classList.contains("show")) {
      closeEditModalFn();
    }
  });

  document.addEventListener("click", function () {
    if (editModalOverlay.classList.contains("show")) {
      closeEditPopovers();
    }
  });
}