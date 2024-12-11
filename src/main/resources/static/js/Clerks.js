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

// 利用者一覧に表示フィルターをつける
function filterTable() {
	const filter = document.getElementById('filterOptions').value;
	const rows = document.querySelectorAll('#passengersTableBody tr');

	let counter = 0; // 利用客のカウント

	rows.forEach(row => {
		const eatingStatusCell = row.querySelector('td:nth-child(4)'); // 4列目の食事状況
		const eatingStatusText = eatingStatusCell.textContent.trim(); // セルのテキストを取得
		const deliveringStatusCell = row.querySelector('td:nth-child(5)'); // 5列目の配達状況
		const deliveringStatusText = deliveringStatusCell.textContent.trim();

		const eatingFlg = (eatingStatusText === "食事中"); // 食事中フラグ
		const undeliveredFlg = (deliveringStatusText !== "お届け済み"); // 未配達フラグ

		if (filter === 'all') {
			row.style.display = ''; // すべて表示
			++counter;
		} else if (filter === 'eating') {
			if (eatingFlg) {
				++counter;
				row.style.display = ''; // 行を表示
			} else {
				row.style.display = 'none'; // 行を非表示
			}
		} else if (filter === 'waiting') {
			if (eatingFlg && undeliveredFlg) {
				++counter;
				row.style.display = ''; // 行を表示
			} else {
				row.style.display = 'none'; // 行を非表示
			}
		}
	});
	document.getElementById('rowCount').textContent = "表示人数 ： " + counter + " 人";
}

// 商品一覧に表示フィルターをつける
function onSaleFilter() {
	const onsaleFilter = document.getElementById('onsaleFilterOptions').value;
	const dishesRows = document.querySelectorAll('#dishesTableBody tbody tr');

	let counter = 0; // 商品数のカウント

	dishesRows.forEach(row => {
		const onsaleStatusCell = row.querySelector('td:nth-child(4)'); // 4列目の提供状況
		const onsaleStatusText = onsaleStatusCell.textContent.trim(); // セルのテキストを取得
		const onSaleFlg = (onsaleStatusText === "提供中"); // 食事中フラグ
		const notOnSaleFlg = (onsaleStatusText === "提供中止"); // 未配達フラグ

		if (onsaleFilter === 'all') {
			row.style.display = ''; // すべて表示
			++counter;

		} else if (onsaleFilter === 'onsale') {
			if (onSaleFlg) {
				++counter;
				row.style.display = ''; // 行を表示
			} else {
				row.style.display = 'none'; // 行を非表示
			}
		} else if (onsaleFilter === 'notonsale') {
			if (notOnSaleFlg) {
				++counter;
				row.style.display = ''; // 行を表示
			} else {
				row.style.display = 'none'; // 行を非表示
			}
		}
	});
	document.getElementById('dishesCount').textContent = "表示品数 ： " + counter + " 個";
}

// 配達済みボタンをすべて取得
const buttons = document.querySelectorAll('.toggleButton');

buttons.forEach(buttons => {
	buttons.addEventListener('click', function(event) {
		const button = this;
		// 対応する注文IDの取得
		const dishId = this.parentElement.querySelector('.dishIdClass').value;

		fetch(`/clerks/dishes?onSale=true&dishId=${dishId}`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				[csrfHeader]: csrfToken
			}, // CSRFトークンをヘッダーに追加
			body: `dishId=${dishId}`
		})
			.then(response => {
				if (response.ok) {

					const statusLabel = button.closest('td').querySelector('.statusLabel');
					if (button.checked) {
						statusLabel.textContent = '提供中　';
						statusLabel.style.color = "darkblue";
					} else {
						statusLabel.textContent = '提供中止';
						statusLabel.style.color = "red";
					}
				} else {
					alert('状態を変更できませんでした。');
				}
			});

	});
});

// 動的にボタンを生成する関数
//function generateRangeButtons(startRange, endRange, step) {
//	const container = document.querySelector('.button-container') || document.body;
//
//	for (let i = startRange; i <= endRange; i += step) {
//		const button = document.createElement('button');
//		button.classList.add('range-button');
//		button.dataset.rangeStart = i;
//		button.dataset.rangeEnd = i + step - 1;
//		button.textContent = `${i}〜${i + step - 1}`;
//
//		container.appendChild(button);
//	}
//}
//
//document.addEventListener('DOMContentLoaded', () => {
//	generateRangeButtons(1000, 5000, 1000);
//});