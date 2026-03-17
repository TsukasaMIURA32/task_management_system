/* ==========================================================
   共同編集者選択機能を初期化する関数
   引数 container には、create用またはedit用の
   .member-selector 要素が渡される想定
========================================================== */
function initMemberSelector(container) {
    /* container が存在しなければ初期化できないので終了 */
    if (!container) {
        return null;
    }

    /* 
       すでに初期化済みなら、同じAPIをそのまま返す
       二重でイベントを付けないためのガード
    */
    if (container._memberSelectorApi) {
        return container._memberSelectorApi;
    }

    /* ==========================================================
       この container 内で使う要素を取得
    ========================================================== */
    const keywordInput = container.querySelector(".member-keyword-input");
    const addButton = container.querySelector(".add-member-button");
    const searchResult = container.querySelector(".member-search-result");
    const preview = container.querySelector(".member-preview");
    const hiddenContainer = container.querySelector(".shared-user-ids-container");

    /* 必須要素のどれかが取れなければ警告を出して終了 */
    if (!keywordInput || !addButton || !searchResult || !preview || !hiddenContainer) {
        console.warn("member selector の要素取得に失敗", container);
        return null;
    }

    /* 
       data-mode で create か edit かを判定する
       create: 新規作成フォーム用
       edit  : 編集モーダル用
    */
    const mode = container.dataset.mode || "";

    /* 現在検索候補から選ばれているユーザーを一時保持 */
    let selectedUser = null;

    /* 
       追加済みユーザーを Map で保持
       key: userId
       value: user object
       重複追加防止のため Map を使っている
    */
    const addedUsers = new Map();

    /* 検索入力時の debounce 用タイマー */
    let debounceTimer = null;

    /* ==========================================================
       どこに「共同編集者名の表示」を出すかを返す関数
       create なら新規フォーム側
       edit なら編集モーダル側
    ========================================================== */
    function getDisplayElement() {
        if (mode === "create") {
            return document.getElementById("sharedUsersInline");
        }

        if (mode === "edit") {
            return document.getElementById("editSharedUsersText");
        }

        return null;
    }

    /* ==========================================================
       追加済みユーザー名をフォーム上に表示する関数
       createなら新規フォーム
       editなら編集フォーム
    ========================================================== */
    function renderDisplayUsers() {
        const displayElement = getDisplayElement();

        /* 表示先がなければ何もしない */
        if (!displayElement) {
            return;
        }

        /* Map からユーザー配列を取り出す */
        const users = Array.from(addedUsers.values());

        /* ユーザーが0人なら表示を空にする */
        if (users.length === 0) {
            displayElement.innerHTML = "";
            return;
        }

        /* 表示用にユーザー名だけ配列へ詰める */
        const names = [];

        for (let i = 0; i < users.length; i++) {
            names.push(users[i].name);
        }

        /* 「共同編集者: 名前1、名前2」の形で表示 */
        displayElement.innerHTML = '<i class="fas fa-users"></i>: ' + names.join("、");
    }

    /* ==========================================================
       検索欄入力時の処理
       入力のたびにユーザー検索APIを叩く
       ただし debounce を入れて連打しすぎないようにする
    ========================================================== */
    keywordInput.addEventListener("input", function () {
        const keyword = this.value.trim();

        /* 前回の検索予約をキャンセル */
        clearTimeout(debounceTimer);

        /* 空なら検索結果を消して終了 */
        if (!keyword) {
            searchResult.innerHTML = "";
            selectedUser = null;
            return;
        }

        /* 300ms 待ってから検索実行 */
        debounceTimer = setTimeout(function () {
            fetch(window.contextPath + "/user/search?keyword=" + encodeURIComponent(keyword))
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error("ユーザー検索に失敗しました");
                    }
                    return response.json();
                })
                .then(function (users) {
                    /* 毎回検索結果表示をクリアして描き直す */
                    searchResult.innerHTML = "";

                    /* 該当ユーザーがいなければメッセージ表示 */
                    if (!users || users.length === 0) {
                        searchResult.innerHTML = '<div class="search-empty">該当ユーザーがいません</div>';
                        selectedUser = null;
                        return;
                    }

                    /* 検索候補を1件ずつ描画 */
                    for (let i = 0; i < users.length; i++) {
                        const user = users[i];

                        const item = document.createElement("div");
                        item.className = "search-result-item";
                        item.textContent = user.name + " (" + user.email + ")";

                        /* 
                           候補クリック時
                           - そのユーザーを selectedUser に保持
                           - input に名前を表示
                           - 候補一覧は閉じる
                        */
                        item.addEventListener("click", function () {
                            selectedUser = user;
                            keywordInput.value = user.name + " (" + user.email + ")";
                            searchResult.innerHTML = "";
                        });

                        searchResult.appendChild(item);
                    }
                })
                .catch(function (error) {
                    console.error("ユーザー検索エラー", error);
                });
        }, 300);
    });

    /* ==========================================================
       「追加」ボタン押下時の処理
       selectedUser を addedUsers に追加する
    ========================================================== */
    addButton.addEventListener("click", function () {
        /* 候補から選んでいなければ追加不可 */
        if (!selectedUser) {
            alert("候補からユーザーを選択してください");
            return;
        }

        const userId = String(selectedUser.id);

        /* すでに追加済みなら重複追加しない */
        if (addedUsers.has(userId)) {
            alert("このユーザーはすでに追加されています");
            return;
        }

        /* Map に追加 */
        addedUsers.set(userId, selectedUser);

        /* 入力欄と検索結果をリセット */
        keywordInput.value = "";
        searchResult.innerHTML = "";
        selectedUser = null;

        /* チップ表示・hidden input・フォーム側表示を更新 */
        renderAddedUsers();
    });

    /* ==========================================================
       追加済みユーザーの表示を描き直す関数
       - モーダル内のチップ表示
       - hidden input 再生成
       - フォーム上の共同編集者表示更新
    ========================================================== */
    function renderAddedUsers() {
        /* 一度全消ししてから作り直す */
        preview.innerHTML = "";
        hiddenContainer.innerHTML = "";

        const users = Array.from(addedUsers.values());

        for (let i = 0; i < users.length; i++) {
            const user = users[i];

            /* --------------------------
               見た目用のチップを作る
            -------------------------- */
            const chip = document.createElement("div");
            chip.className = "member-chip";
            chip.innerHTML =
                '<span>' + user.name + '</span>' +
                '<button type="button" class="remove-member-button" data-user-id="' + user.id + '">×</button>';

            preview.appendChild(chip);

            /* --------------------------
               サーバー送信用 hidden input を作る
               name="sharedUserIds" を複数作ることで配列送信する
            -------------------------- */
            const hidden = document.createElement("input");
            hidden.type = "hidden";
            hidden.name = "sharedUserIds";
            hidden.value = user.id;

            /* 
               共同編集者モーダル自体は form の外にあるので、
               どの form に属するかを form 属性で明示する
            */
            if (mode === "create") {
                hidden.setAttribute("form", "noteForm");
            } else if (mode === "edit") {
                hidden.setAttribute("form", "editNoteForm");
            }

            hiddenContainer.appendChild(hidden);
        }

        /* --------------------------
           チップ内の × ボタンに削除処理を付ける
        -------------------------- */
        const removeButtons = preview.querySelectorAll(".remove-member-button");
        for (let i = 0; i < removeButtons.length; i++) {
            removeButtons[i].addEventListener("click", function () {
                const userId = this.dataset.userId;
                addedUsers.delete(userId);
                renderAddedUsers();
            });
        }

        /* フォーム側の共同編集者表示も更新 */
        renderDisplayUsers();
    }

    /* ==========================================================
       外部から使うAPIを返す
       create/edit のJSから呼ばれる
    ========================================================== */
    const api = {
        /* 
           既存ユーザー一覧で初期化する
           編集モーダルを開いたときなどに使う
        */
        setUsers: function (users) {
            addedUsers.clear();

            if (users && users.length > 0) {
                for (let i = 0; i < users.length; i++) {
                    const user = users[i];
                    addedUsers.set(String(user.id), user);
                }
            }

            selectedUser = null;
            keywordInput.value = "";
            searchResult.innerHTML = "";
            renderAddedUsers();
        },

        /* 追加済みユーザー一覧を配列で返す */
        getUsers: function () {
            return Array.from(addedUsers.values());
        },

        /* 追加済みユーザーを全部クリアする */
        clearUsers: function () {
            addedUsers.clear();
            selectedUser = null;
            keywordInput.value = "";
            searchResult.innerHTML = "";
            renderAddedUsers();
        }
    };

    /* container に API を保持して再利用できるようにする */
    container._memberSelectorApi = api;

    return api;
}

/* ==========================================================
   create 用 member selector 初期化
   新規作成モーダル側
========================================================== */
const createMemberSelector = document.querySelector('.member-selector[data-mode="create"]');
if (createMemberSelector) {
    window.createMemberSelectorApi = initMemberSelector(createMemberSelector);
}

/* ==========================================================
   edit 用 member selector 初期化
   編集モーダル側
========================================================== */
const editMemberSelector = document.querySelector('.member-selector[data-mode="edit"]');
if (editMemberSelector) {
    window.editMemberSelectorApi = initMemberSelector(editMemberSelector);
}