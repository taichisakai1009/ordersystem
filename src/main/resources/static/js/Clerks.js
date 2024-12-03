document.addEventListener('DOMContentLoaded', function() {

	// 配達済みボタンをすべて取得
	const buttons = document.querySelectorAll('.deliveredButton');

	buttons.forEach(buttons => {
		buttons.addEventListener('click', function(event) {
			event.preventDefault(); // フォームのデフォルト送信を防止、フォームを作ったときのために取っておく

			const button = this;
			// 対応する注文IDの取得
			const orderId = this.parentElement.querySelector('.orderid').value;

			// Fetch APIを使用してPOSTリクエスト
			fetch('/clerks/delivered?delivered', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded',
					// CSRFトークンが必要な場合は追加
					// 'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
				},
				body: `id=${orderId}`
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
                        const statusCell = this.closest('tr').querySelector('td:nth-child(5)');
                        statusCell.textContent = "配達済み";

						// ボタンを無効化
						button.disabled = true;
						button.classList.add('disabled-button');

					} else {
						throw new Error(data.message);
					}
				})
				.catch(error => {
					console.error('Error:', error);
					alert('配達済み処理中にエラーが発生しました');
				});
		});
	});
});