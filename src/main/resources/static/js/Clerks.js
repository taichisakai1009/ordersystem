// CSRFトークンを取得
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

document.addEventListener('DOMContentLoaded', function() {

	// 配達済みボタンをすべて取得
	const buttons = document.querySelectorAll('.deliveredButton');

	buttons.forEach(buttons => {
		buttons.addEventListener('click', function(event) {
			event.preventDefault(); // フォームのデフォルト送信を防止、フォームを作ったときのために取っておく

			const button = this;
			// 対応する注文IDの取得
			const orderDetailsId = this.parentElement.querySelector('.orderDetailsId').value;
			const orderId = this.parentElement.querySelector('.orderId').value;
			const passengerId = document.getElementById('setDeliverd-passengerId').value;
			
			console.log();

			// Fetch APIを使用してPOSTリクエスト
			fetch('/clerks/delivered?delivered', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded',
					// CSRFトークンが必要な場合は追加
					// 'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
					[csrfHeader]: csrfToken // CSRFトークンをヘッダーに追加
				},
				body: `id=${orderDetailsId}`
			})
				.then(response => {
					if (!response.ok) {
						throw new Error('Network response was not ok');
					}
					return response.json(); // JSONレスポンスを期待
				})
				.then(data => {
					if (data.success) {
						// 成功時の処理
						alert(data.message);

						// ステータス列を「配達済み」に変更
						const statusCell = this.closest('tr').querySelector('td:nth-child(3)');
						statusCell.textContent = "配達済み";

						// ボタンを無効化
						button.disabled = true;
						button.classList.add('disabled-button');

						return fetch('/clerks/delivered?orders', {
							method: 'POST',
							headers: {
								'Content-Type': 'application/x-www-form-urlencoded',
								[csrfHeader]: csrfToken // CSRFトークンをヘッダーに追加
							},
							body: `orderId=${orderId}`
						});

					} else {
						throw new Error(data.message);
					}
				})
				.then(response => {
					if (!response.ok) {
						throw new Error('Network response was not ok');
					}
					return response.json(); // JSONレスポンスを期待
				})
				.then(data => {
					// orders リクエスト成功時の処理
					console.log("注文テーブルの更新完了", data);

					// 利用客の未配達フラグを更新するリクエスト
					return fetch('/clerks/delivered?passenger', {
						method: 'POST',
						headers: {
							'Content-Type': 'application/x-www-form-urlencoded',
							[csrfHeader]: csrfToken // CSRFトークンをヘッダーに追加
						},
						body: `passengerId=${passengerId}`
					});
				})
				.then(response => {
					if (!response.ok) {
						throw new Error('Network response was not ok');
					}
					return response.json(); // JSONレスポンスを期待
				})
				.then(data => {
					// orders リクエスト成功時の処理
					console.log("利用客テーブルの更新完了", data);
				})
				.catch(error => {
					console.error('Error:', error);
					alert('処理中にエラーが発生しました');
				});
		});
	});
});