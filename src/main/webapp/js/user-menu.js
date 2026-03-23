/* -------------------------
   ユーザーメニュー
------------------------- */
const userMenuButton = document.getElementById("userMenuButton");
const userMenuPopover = document.getElementById("userMenuPopover");
const userMenuClose = document.getElementById("userMenuClose");

if (userMenuButton && userMenuPopover) {
  userMenuButton.addEventListener("click", function (e) {
    e.stopPropagation();
    userMenuPopover.classList.toggle("show");
  });

  userMenuPopover.addEventListener("click", function (e) {
    e.stopPropagation();
  });

  if (userMenuClose) {
    userMenuClose.addEventListener("click", function (e) {
      e.stopPropagation();
      userMenuPopover.classList.remove("show");
    });
  }

  document.addEventListener("click", function () {
    userMenuPopover.classList.remove("show");
  });
}

/* -------------------------
   編集
------------------------- */
document.addEventListener("DOMContentLoaded", function () {
  // ===== メール編集 =====
  const emailDisplay = document.getElementById("emailDisplay");
  const emailEdit = document.getElementById("emailEdit");
  const emailText = document.getElementById("emailText");
  const emailInput = document.getElementById("emailInput");

  const editEmailBtn = document.getElementById("editEmailBtn");
  const saveEmailBtn = document.getElementById("saveEmailBtn");
  const cancelEmailBtn = document.getElementById("cancelEmailBtn");

  // ===== 名前編集 =====
  const nameDisplay = document.getElementById("nameDisplay");
  const nameEdit = document.getElementById("nameEdit");
  const nameText = document.getElementById("nameText");
  const nameInput = document.getElementById("nameInput");

  const editNameBtn = document.getElementById("editNameBtn");
  const saveNameBtn = document.getElementById("saveNameBtn");
  const cancelNameBtn = document.getElementById("cancelNameBtn");

  /* -------------------------
     共通：プロフィール更新
  ------------------------- */
  async function updateUserProfile() {
    const newName = nameInput.value.trim();
    const newEmail = emailInput.value.trim();

    if (newName === "") {
      alert("名前を入力してください。");
      return false;
    }

    if (newEmail === "") {
      alert("メールアドレスを入力してください。");
      return false;
    }

    try {
      const response = await fetch(`${window.contextPath}/user/update`, {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body: new URLSearchParams({
          userName: newName,
          email: newEmail
        }).toString()
      });

      const data = await response.json();

      if (!response.ok || !data.success) {
        alert(data.message || "更新に失敗しました。");
        return false;
      }

      nameText.textContent = data.userName;
      emailText.textContent = data.email;

      nameInput.value = data.userName;
      emailInput.value = data.email;

      return true;
    } catch (error) {
      console.error(error);
      alert("更新中にエラーが発生しました。");
      return false;
    }
  }

  /* -------------------------
     メール編集
  ------------------------- */
  if (editEmailBtn) {
    editEmailBtn.addEventListener("click", function () {
      emailDisplay.classList.add("hidden");
      emailEdit.classList.remove("hidden");
      emailInput.focus();
      emailInput.select();
    });
  }

  if (saveEmailBtn) {
    saveEmailBtn.addEventListener("click", async function () {
      const result = await updateUserProfile();

      if (result) {
        emailEdit.classList.add("hidden");
        emailDisplay.classList.remove("hidden");
      }
    });
  }

  if (cancelEmailBtn) {
    cancelEmailBtn.addEventListener("click", function () {
      emailInput.value = emailText.textContent;
      emailEdit.classList.add("hidden");
      emailDisplay.classList.remove("hidden");
    });
  }

  /* -------------------------
     名前編集
  ------------------------- */
  if (editNameBtn) {
    editNameBtn.addEventListener("click", function () {
      nameDisplay.classList.add("hidden");
      nameEdit.classList.remove("hidden");
      nameInput.focus();
      nameInput.select();
    });
  }

  if (saveNameBtn) {
    saveNameBtn.addEventListener("click", async function () {
      const result = await updateUserProfile();

      if (result) {
        nameEdit.classList.add("hidden");
        nameDisplay.classList.remove("hidden");
      }
    });
  }

  if (cancelNameBtn) {
    cancelNameBtn.addEventListener("click", function () {
      nameInput.value = nameText.textContent;
      nameEdit.classList.add("hidden");
      nameDisplay.classList.remove("hidden");
    });
  }

  /* -------------------------
     パスワード変更
  ------------------------- */
  const openPasswordChangeBtn = document.getElementById("openPasswordChangeBtn");
  const passwordChangeBox = document.getElementById("passwordChangeBox");
  const currentPasswordInput = document.getElementById("currentPasswordInput");
  const newPasswordInput = document.getElementById("newPasswordInput");
  const confirmNewPasswordInput = document.getElementById("confirmNewPasswordInput");
  const passwordChangeMessage = document.getElementById("passwordChangeMessage");
  const cancelPasswordChangeBtn = document.getElementById("cancelPasswordChangeBtn");
  const savePasswordChangeBtn = document.getElementById("savePasswordChangeBtn");
 
//  console.log(openPasswordChangeBtn);
//  console.log(passwordChangeBox);
//  console.log(currentPasswordInput);
//  console.log(newPasswordInput);
//  console.log(confirmNewPasswordInput);
//  console.log(passwordChangeMessage);
//  console.log(cancelPasswordChangeBtn);
//  console.log(savePasswordChangeBtn);
  
   //  パスワード変更エリアの表示・非表示の関数  
  function clearPasswordChangeForm() {
    if (currentPasswordInput) currentPasswordInput.value = "";
    if (newPasswordInput) newPasswordInput.value = "";
    if (confirmNewPasswordInput) confirmNewPasswordInput.value = "";
    if (passwordChangeMessage) passwordChangeMessage.textContent = "";
  }

  function showPasswordChangeBox() {
    if (accountMenuDefault) {
      accountMenuDefault.classList.add("hidden");
    }
    if (withdrawConfirmBox) {
      withdrawConfirmBox.classList.add("hidden");
    }
    if (passwordChangeBox) {
      passwordChangeBox.classList.remove("hidden");
    }
  }

  function hidePasswordChangeBox() {
    if (passwordChangeBox) {
      passwordChangeBox.classList.add("hidden");
    }
    if (accountMenuDefault) {
      accountMenuDefault.classList.remove("hidden");
    }
    clearPasswordChangeForm();
  }
  
//  パスワード変更ボタンクリック時のイベント
  if (openPasswordChangeBtn) {
    openPasswordChangeBtn.addEventListener("click", function () {
      showPasswordChangeBox();
    });
  }

  if (cancelPasswordChangeBtn) {
    cancelPasswordChangeBtn.addEventListener("click", function () {
      hidePasswordChangeBox();
    });
  }
//  パスワード変更の非同期処理
  async function updatePassword() {
    const currentPassword = currentPasswordInput ? currentPasswordInput.value : "";
    const newPassword = newPasswordInput ? newPasswordInput.value : "";
    const confirmNewPassword = confirmNewPasswordInput ? confirmNewPasswordInput.value : "";

    if (passwordChangeMessage) {
      passwordChangeMessage.textContent = "";
    }

    try {
      const response = await fetch(window.contextPath + "/user/changepassword", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body:
          "currentPassword=" + encodeURIComponent(currentPassword) +
          "&newPassword=" + encodeURIComponent(newPassword) +
          "&confirmNewPassword=" + encodeURIComponent(confirmNewPassword)
      });

      const result = await response.json();

      if (result.success) {
        if (passwordChangeMessage) {
          passwordChangeMessage.textContent = result.message;
        }
        setTimeout(function () {
          hidePasswordChangeBox();
        }, 1000);
      } else {
        if (passwordChangeMessage) {
          passwordChangeMessage.textContent = result.message;
        }
      }
    } catch (error) {
      console.error("パスワード変更エラー:", error);
      if (passwordChangeMessage) {
        passwordChangeMessage.textContent = "パスワード変更に失敗しました。";
      }
    }
  }

  if (savePasswordChangeBtn) {
    savePasswordChangeBtn.addEventListener("click", function () {
      updatePassword();
    });
  }
  
  /* -------------------------
     退会
  ------------------------- */
  const withdrawBtn = document.getElementById("withdrawBtn");
  const accountMenuDefault = document.getElementById("accountMenuDefault");
  const withdrawConfirmBox = document.getElementById("withdrawConfirmBox");
  const cancelWithdrawBtn = document.getElementById("cancelWithdrawBtn");
  const confirmWithdrawBtn = document.getElementById("confirmWithdrawBtn");
  const withdrawForm = document.getElementById("withdrawForm");

  if (withdrawBtn) {
    withdrawBtn.addEventListener("click", function () {
      accountMenuDefault.classList.add("hidden");
      withdrawConfirmBox.classList.remove("hidden");
    });
  }

  if (cancelWithdrawBtn) {
    cancelWithdrawBtn.addEventListener("click", function () {
      withdrawConfirmBox.classList.add("hidden");
      accountMenuDefault.classList.remove("hidden");
    });
  }

  if (confirmWithdrawBtn && withdrawForm) {
    confirmWithdrawBtn.addEventListener("click", function () {
      confirmWithdrawBtn.disabled = true;
      withdrawForm.submit();
    });
  }
});