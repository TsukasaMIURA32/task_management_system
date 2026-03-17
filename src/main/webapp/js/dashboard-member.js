function initMemberSelector(container) {
    if (!container) {
        return null;
    }

    // すでに初期化済みなら、そのAPIを返す
    if (container._memberSelectorApi) {
        return container._memberSelectorApi;
    }

    const keywordInput = container.querySelector('.member-keyword-input');
    const addButton = container.querySelector('.add-member-button');
    const searchResult = container.querySelector('.member-search-result');
    const preview = container.querySelector('.member-preview');
    const hiddenContainer = container.querySelector('.shared-user-ids-container');

    if (!keywordInput || !addButton || !searchResult || !preview || !hiddenContainer) {
        console.warn('member selector の要素取得に失敗', container);
        return null;
    }

    let selectedUser = null;
    const addedUsers = new Map();
    let debounceTimer = null;

    keywordInput.addEventListener('input', function () {
        const keyword = this.value.trim();

        clearTimeout(debounceTimer);

        if (!keyword) {
            searchResult.innerHTML = '';
            selectedUser = null;
            return;
        }

        debounceTimer = setTimeout(function () {
            fetch(window.contextPath + '/user/search?keyword=' + encodeURIComponent(keyword))
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error('ユーザー検索に失敗しました');
                    }
                    return response.json();
                })
                .then(function (users) {
                    searchResult.innerHTML = '';

                    if (!users || users.length === 0) {
                        searchResult.innerHTML = '<div class="search-empty">該当ユーザーがいません</div>';
                        selectedUser = null;
                        return;
                    }

                    for (let i = 0; i < users.length; i++) {
                        const user = users[i];

                        const item = document.createElement('div');
                        item.className = 'search-result-item';
                        item.textContent = user.name + ' (' + user.email + ')';

                        item.addEventListener('click', function () {
                            selectedUser = user;
                            keywordInput.value = user.name + ' (' + user.email + ')';
                            searchResult.innerHTML = '';
                            console.log('selectedUser =', selectedUser);
                        });

                        searchResult.appendChild(item);
                    }
                })
                .catch(function (error) {
                    console.error('ユーザー検索エラー', error);
                });
        }, 300);
    });

    addButton.addEventListener('click', function () {
        console.log('add click selectedUser =', selectedUser);

        if (!selectedUser) {
            alert('候補からユーザーを選択してください');
            return;
        }

        const userId = String(selectedUser.id);

        if (addedUsers.has(userId)) {
            alert('このユーザーはすでに追加されています');
            return;
        }

        addedUsers.set(userId, selectedUser);
        renderAddedUsers();

        keywordInput.value = '';
        searchResult.innerHTML = '';
        selectedUser = null;
    });

    function renderAddedUsers() {
        preview.innerHTML = '';
        hiddenContainer.innerHTML = '';

        const users = Array.from(addedUsers.values());

        for (let i = 0; i < users.length; i++) {
            const user = users[i];

            const chip = document.createElement('div');
            chip.className = 'member-chip';
            chip.innerHTML =
                '<span>' + user.name + '</span>' +
                '<button type="button" class="remove-member-button" data-user-id="' + user.id + '">×</button>';

            preview.appendChild(chip);

            const hidden = document.createElement('input');
            hidden.type = 'hidden';
            hidden.name = 'sharedUserIds';
            hidden.value = user.id;
            hiddenContainer.appendChild(hidden);
        }

        const removeButtons = preview.querySelectorAll('.remove-member-button');
        for (let i = 0; i < removeButtons.length; i++) {
            removeButtons[i].addEventListener('click', function () {
                const userId = this.dataset.userId;
                addedUsers.delete(userId);
                renderAddedUsers();
            });
        }
    }

    const api = {
        setUsers: function (users) {
            addedUsers.clear();

            if (!users || users.length === 0) {
                renderAddedUsers();
                return;
            }

            for (let i = 0; i < users.length; i++) {
                const user = users[i];
                addedUsers.set(String(user.id), user);
            }

            renderAddedUsers();
        },

        getUsers: function () {
            return Array.from(addedUsers.values());
        },

        clearUsers: function () {
            addedUsers.clear();
            selectedUser = null;
            keywordInput.value = '';
            searchResult.innerHTML = '';
            renderAddedUsers();
        }
    };

    // 初期化済みAPIをcontainerに保持
    container._memberSelectorApi = api;

    return api;
}