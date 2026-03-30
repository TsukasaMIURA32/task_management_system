document.addEventListener("DOMContentLoaded", function () {
    var toast = document.getElementById("toastMessage");

    if (!toast) {
        return;
    }

    toast.classList.add("show");

    setTimeout(function () {
        toast.classList.remove("show");
        toast.classList.add("hide");
    }, 3000);

    setTimeout(function () {
        if (toast.parentNode) {
            toast.parentNode.removeChild(toast);
        }
    }, 3300);
});