document.addEventListener("DOMContentLoaded", function () {

	var backButton = document.getElementById("backButton");

	if (backButton) {
		backButton.addEventListener("click", function (e) {
			e.preventDefault();

			if (document.referrer) {
				history.back();
			} else {
				window.location.href = "/dashboard";
			}
		});
	}

});