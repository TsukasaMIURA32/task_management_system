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

  if (editEmailBtn) {
    editEmailBtn.addEventListener("click", function () {
      emailDisplay.classList.add("hidden");
      emailEdit.classList.remove("hidden");
      emailInput.focus();
      emailInput.select();
    });
  }

  if (saveEmailBtn) {
    saveEmailBtn.addEventListener("click", function () {
      const newEmail = emailInput.value.trim();

      if (newEmail === "") {
        alert("メールアドレスを入力してください。");
        return;
      }

      emailText.textContent = newEmail;
      emailEdit.classList.add("hidden");
      emailDisplay.classList.remove("hidden");
    });
  }

  if (cancelEmailBtn) {
    cancelEmailBtn.addEventListener("click", function () {
      emailInput.value = emailText.textContent;
      emailEdit.classList.add("hidden");
      emailDisplay.classList.remove("hidden");
    });
  }


  // ===== 名前編集 =====
  const nameDisplay = document.getElementById("nameDisplay");
  const nameEdit = document.getElementById("nameEdit");
  const nameText = document.getElementById("nameText");
  const nameInput = document.getElementById("nameInput");

  const editNameBtn = document.getElementById("editNameBtn");
  const saveNameBtn = document.getElementById("saveNameBtn");
  const cancelNameBtn = document.getElementById("cancelNameBtn");

  if (editNameBtn) {
    editNameBtn.addEventListener("click", function () {
      nameDisplay.classList.add("hidden");
      nameEdit.classList.remove("hidden");
      nameInput.focus();
      nameInput.select();
    });
  }

  if (saveNameBtn) {
    saveNameBtn.addEventListener("click", function () {
      const newName = nameInput.value.trim();

      if (newName === "") {
        alert("名前を入力してください。");
        return;
      }

      nameText.textContent = newName;
      nameEdit.classList.add("hidden");
      nameDisplay.classList.remove("hidden");
    });
  }

  if (cancelNameBtn) {
    cancelNameBtn.addEventListener("click", function () {
      nameInput.value = nameText.textContent;
      nameEdit.classList.add("hidden");
      nameDisplay.classList.remove("hidden");
    });
  }
  // ===== 退会 =====
  const withdrawBtn = document.getElementById("withdrawBtn");
  	const accountMenuDefault = document.getElementById("accountMenuDefault");
  	const withdrawConfirmBox = document.getElementById("withdrawConfirmBox");
  	const cancelWithdrawBtn = document.getElementById("cancelWithdrawBtn");
  	const confirmWithdrawBtn = document.getElementById("confirmWithdrawBtn");

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

  	if (confirmWithdrawBtn) {
  		confirmWithdrawBtn.addEventListener("click", function () {
  			alert("退会処理を実行します");
  			// ここに退会用の送信処理を書く
  			// 例: location.href = "WithdrawServlet";
  		});
  	}

});

