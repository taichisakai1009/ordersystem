document.getElementById('changePassword').addEventListener('submit', function(event) {
	const newPassword = document.getElementById('changed-password-box').value;
	const confirmPassword = document.getElementById('confirm-password-box').value;

	if (newPassword !== confirmPassword) {
		event.preventDefault();
		alert("新しいパスワードが一致しません。");
	}
});
