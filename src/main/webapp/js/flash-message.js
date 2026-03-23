document.addEventListener("DOMContentLoaded", function () {
    var toast = document.getElementById("toastMessage");

    if (toast) {
        // 3秒後にフェードアウト
        setTimeout(function () {
            toast.classList.add("hide");
        }, 3000);

        // フェードアウト後にDOMから削除
        setTimeout(function () {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 3300); // アニメーション時間 + 少し余裕
    }
});